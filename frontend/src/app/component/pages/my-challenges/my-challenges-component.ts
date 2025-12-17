import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { LucideAngularModule, Edit, Trash2, PlusCircle, Check, X } from 'lucide-angular';
import { ChallengeService } from '../../../services/challenge.service';
import { AuthService } from '../../../services/auth.service';
import { Challenge } from '../challenges/challenge.model';
import { ChallengeFormComponent } from '../../forms/challenge-form/challenge-form';
import { ToastComponent } from '../../../shared/toast/toast-component';

@Component({
  selector: 'app-my-challenges',
  standalone: true,
  imports: [CommonModule, ChallengeFormComponent, ToastComponent, LucideAngularModule],
  templateUrl: './my-challenges-component.html',
  styles: []
})
export class MyChallengesComponent implements OnInit {
  public challengeService = inject(ChallengeService);
  private auth = inject(AuthService);
  private route = inject(ActivatedRoute);

  // Tabs: 'active' (Accepted), 'inbox' (Received), 'created' (My own)
  activeTab = signal<'active' | 'inbox' | 'created'>('active');

  // Lists
  myChallenges = signal<Challenge[]>([]); // Tab: Created
  inboxChallenges = signal<any[]>([]);    // Tab: Inbox
  activeChallenges = signal<any[]>([]);   // Tab: Active

  isLoading = signal(false);

  // Modale
  isEditModalOpen = signal(false);
  editChallengeData = signal<any | null>(null);
  isDeleteModalOpen = signal(false);
  challengeToDelete = signal<Challenge | null>(null);

  // Toast
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  readonly icons = { Edit, Trash2, PlusCircle, Check, X };

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['tab'] === 'inbox') {
        this.activeTab.set('inbox');
      }
    });
    this.loadAllData();
  }

  loadAllData() {
    const currentUser = this.auth.currentUser();
    if (!currentUser) return;

    this.isLoading.set(true);

    // 1. Load Created Challenges
    this.challengeService.getUserChallenges(currentUser.username).subscribe({
      next: (data) => this.myChallenges.set(data),
      complete: () => this.isLoading.set(false)
    });

    // 2. Load Inbox & Active
    if(currentUser.id) {
      this.challengeService.getChallengesByStatus(currentUser.id, 'RECEIVED').subscribe(data => {
        this.inboxChallenges.set(data);
      });

      this.challengeService.getChallengesByStatus(currentUser.id, 'ACCEPTED').subscribe(data => {
        this.activeChallenges.set(data);
      });
    }
  }

  // --- TAB SWITCH ---
  switchTab(tab: 'active' | 'inbox' | 'created') {
    this.activeTab.set(tab);
  }

  // --- ACTIONS ---
  acceptChallenge(item: any) {
    const user = this.auth.currentUser();
    if(!user?.id) return;

    this.challengeService.updateChallengeStatus(item.id, user.id, 'ACCEPTED').subscribe(() => {
      this.showToast('Challenge Accepted!', 'success');
      this.inboxChallenges.update(prev => prev.filter(c => c.id !== item.id));
      this.activeChallenges.update(prev => [...prev, item]);
    });
  }

  declineChallenge(item: any) {
    const user = this.auth.currentUser();
    if(!user?.id) return;

    this.challengeService.updateChallengeStatus(item.id, user.id, 'DECLINED').subscribe(() => {
      this.showToast('Challenge Declined.', 'success');
      this.inboxChallenges.update(prev => prev.filter(c => c.id !== item.id));
    });
  }

  // --- CRUD (For Created Tab) ---
  onCreateChallenge(formValues: any) {
    const currentUser = this.auth.currentUser();
    if (!currentUser) return;
    const newChallenge = { ...formValues, createdBy: currentUser.username };

    this.challengeService.createChallenge(newChallenge).subscribe({
      next: () => {
        this.showToast('Challenge created successfully!', 'success');
        this.challengeService.isCreateModalOpen.set(false);
        this.loadAllData();
      },
      error: (err) => this.showToast('Failed to create.', 'error')
    });
  }

  openEditModal(challenge: Challenge) {
    this.editChallengeData.set(challenge);
    this.isEditModalOpen.set(true);
  }

  onChallengeUpdated(updatedData: any) {
    this.challengeService.updateChallenge(updatedData.id, updatedData).subscribe({
      next: () => {
        this.showToast('Challenge updated!', 'success');
        this.isEditModalOpen.set(false);
        this.loadAllData();
      },
      error: () => this.showToast('Update failed.', 'error')
    });
  }

  confirmDelete(challenge: Challenge) {
    this.challengeToDelete.set(challenge);
    this.isDeleteModalOpen.set(true);
  }

  performDelete() {
    const target = this.challengeToDelete();
    if (!target) return;
    this.challengeService.deleteChallenge(target.id).subscribe({
      next: () => {
        this.showToast('Challenge deleted.', 'success');
        this.isDeleteModalOpen.set(false);
        this.myChallenges.update(list => list.filter(c => c.id !== target.id));
      },
      error: () => this.showToast('Delete failed.', 'error')
    });
  }

  showToast(msg: string, type: 'success' | 'error') {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    setTimeout(() => this.toastMessage.set(null), 3000);
  }
}
