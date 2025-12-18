import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router'; // Import Router
import { LucideAngularModule, Edit, Trash2, PlusCircle, Check, X, Clock } from 'lucide-angular';
import { ChallengeService } from '../../../services/challenge.service';
import { AuthService } from '../../../services/auth.service';
import { Challenge } from '../challenges/challenge.model';
import { ChallengeFormComponent } from '../../forms/challenge-form/challenge-form';
import { ToastComponent } from '../../../shared/toast/toast-component';
import { AcceptChallengeModalComponent } from '../../accept-challenge-modal/accept-challenge-modal';

@Component({
  selector: 'app-my-challenges',
  standalone: true,
  imports: [
    CommonModule,
    ChallengeFormComponent,
    ToastComponent,
    LucideAngularModule,
    AcceptChallengeModalComponent // 1. Importam Modalul
  ],
  templateUrl: './my-challenges-component.html',
  styles: []
})
export class MyChallengesComponent implements OnInit {
  public challengeService = inject(ChallengeService);
  private auth = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  // Tabs
  activeTab = signal<'active' | 'inbox' | 'created'>('active');

  // Lists
  myChallenges = signal<Challenge[]>([]);
  inboxChallenges = signal<any[]>([]);
  activeChallenges = signal<any[]>([]);

  isLoading = signal(false);

  // Modale Existing
  isEditModalOpen = signal(false);
  editChallengeData = signal<any | null>(null);
  isDeleteModalOpen = signal(false);
  challengeToDelete = signal<Challenge | null>(null);

  // 2. Modale Noi (Contract)
  isAcceptModalOpen = false;
  selectedChallengeForContract: any = null;

  // Toast
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  readonly icons = { Edit, Trash2, PlusCircle, Check, X, Clock };

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
      this.challengeService.getChallengesByStatus(currentUser.id, 'PENDING').subscribe(data => {
        this.inboxChallenges.set(data);
      });

      this.challengeService.getChallengesByStatus(currentUser.id, 'ACCEPTED').subscribe(data => {
        this.activeChallenges.set(data);
      });
    }
  }

  switchTab(tab: 'active' | 'inbox' | 'created') {
    this.activeTab.set(tab);
  }

  // --- ACTIONS ---

  // Nu mai cheama API direct, deschide modalul
  acceptChallenge(item: any) {
    this.selectedChallengeForContract = item;
    this.isAcceptModalOpen = true;
  }

  // Se apeleaza cand userul semneaza contractul in modal
  onContractSigned(dates: { start: string, end: string }) {
    if (!this.selectedChallengeForContract) return;


    const payload = {
      status: 'ACCEPTED',
      startDate: dates.start,
      targetDeadline: dates.end
    };

    // Apelăm direct un PUT/PATCH pe ID-ul relației
    this.challengeService.updateChallengeUser(this.selectedChallengeForContract.id, payload).subscribe({
      next: () => {
        this.showToast('Challenge Accepted! 🚀', 'success');
        this.isAcceptModalOpen = false;

        // Refresh local
        this.inboxChallenges.update(prev => prev.filter(c => c.id !== this.selectedChallengeForContract.id));
        this.activeChallenges.update(prev => [...prev, { ...this.selectedChallengeForContract, status: 'ACCEPTED', startDate: dates.start }]);

        this.switchTab('active');
      },
      error: (err) => {
        console.error(err);
        this.showToast(err.error?.message || 'Failed to accept challenge.', 'error');
      }
    });
  }

  declineChallenge(item: any) {
    if(!confirm('Are you sure you want to decline this challenge?')) return;

    // MODIFICARE: Folosim delete (Refuse) în loc de update status
    // item.id este ID-ul relației (ChallengeUser ID)
    this.challengeService.refuseChallenge(item.id).subscribe({
      next: () => {
        this.showToast('Challenge Declined.', 'success');
        this.inboxChallenges.update(prev => prev.filter(c => c.id !== item.id));
      },
      error: () => this.showToast('Action failed.', 'error')
    });
  }

  // --- CRUD ---
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
