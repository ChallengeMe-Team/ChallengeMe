import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { LucideAngularModule, Edit, Trash2, PlusCircle, Check, X, Clock, CheckCircle } from 'lucide-angular';
import { ChallengeService } from '../../../services/challenge.service';
import { AuthService } from '../../../services/auth.service';
import { Challenge } from '../challenges/challenge.model';
import { ChallengeFormComponent } from '../../forms/challenge-form/challenge-form';
import { ToastComponent } from '../../../shared/toast/toast-component';
import { AcceptChallengeModalComponent } from '../../accept-challenge-modal/accept-challenge-modal';
import { ConfirmationModalComponent } from '../../../shared/confirmation-modal/confirmation-modal.component';
import { CompleteChallengeModalComponent } from '../../complete-challenge-modal/complete-challenge-modal-component';

import confetti from 'canvas-confetti';

@Component({
  selector: 'app-my-challenges',
  standalone: true,
  imports: [
    CommonModule,
    ChallengeFormComponent,
    ToastComponent,
    LucideAngularModule,
    AcceptChallengeModalComponent,
    CompleteChallengeModalComponent,
    ConfirmationModalComponent
  ],
  templateUrl: './my-challenges-component.html',
  styles: []
})
export class MyChallengesComponent implements OnInit {
  public challengeService = inject(ChallengeService);
  public auth = inject(AuthService);
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

  // Modale Accept (Contract)
  isAcceptModalOpen = false;
  selectedChallengeForContract: any = null;

  // State for Completion Modal
  isCompleteModalOpen = false;
  selectedChallengeForCompletion: any = null;

  // State pentru Decline Modal
  isDeclineModalOpen = false;
  challengeToDecline: any = null;

  // Toast
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  readonly icons = { Edit, Trash2, PlusCircle, Check, X, Clock, CheckCircle };

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['tab'] === 'inbox') {
        setTimeout(() => {
          this.switchTab('inbox');
        }, 50);
      }
    });
    this.loadAllData();
  }

  loadAllData() {
    const currentUser = this.auth.currentUser();
    if (!currentUser) return;

    this.isLoading.set(true);

    this.challengeService.getUserChallenges(currentUser.username).subscribe({
      next: (data) => this.myChallenges.set(data),
      complete: () => this.isLoading.set(false)
    });

    if (currentUser.id) {
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

  acceptChallenge(item: any) {
    this.selectedChallengeForContract = item;
    this.isAcceptModalOpen = true;
  }

  onContractSigned(dates: { start: string, end: string }) {
    if (!this.selectedChallengeForContract) return;

    const payload = {
      status: 'ACCEPTED',
      startDate: dates.start,
      targetDeadline: dates.end
    };

    this.challengeService.updateChallengeUser(this.selectedChallengeForContract.id, payload).subscribe({
      next: () => {
        this.showToast('Challenge Accepted! 🚀', 'success');
        this.isAcceptModalOpen = false;
        this.inboxChallenges.update(prev => prev.filter(c => c.id !== this.selectedChallengeForContract.id));
        this.activeChallenges.update(prev => [...prev, {
          ...this.selectedChallengeForContract,
          status: 'ACCEPTED',
          startDate: dates.start
        }]);
        this.switchTab('active');
      },
      error: (err) => {
        console.error(err);
        this.showToast(err.error?.message || 'Failed to accept challenge.', 'error');
      }
    });
  }

  declineChallenge(item: any) {
    this.challengeToDecline = item;
    this.isDeclineModalOpen = true;
  }

  onConfirmDecline() {
    if (!this.challengeToDecline) return;

    this.challengeService.refuseChallenge(this.challengeToDecline.id).subscribe({
      next: () => {
        this.showToast('Challenge Declined.', 'success');
        this.inboxChallenges.update(prev => prev.filter(c => c.id !== this.challengeToDecline.id));
        this.isDeclineModalOpen = false;
        this.challengeToDecline = null;
      },
      error: () => {
        this.showToast('Action failed.', 'error');
        this.isDeclineModalOpen = false;
      }
    });
  }

  openCompleteModal(item: any) {
    this.selectedChallengeForCompletion = item;
    this.isCompleteModalOpen = true;
  }

  onChallengeCompleted() {
    if (!this.selectedChallengeForCompletion) return;

    const payload = { status: 'COMPLETED' };

    this.challengeService.updateChallengeUser(this.selectedChallengeForCompletion.id, payload).subscribe({
      next: () => {
        this.triggerSuccessAnimation();
        this.showToast(`Victory! +${this.selectedChallengeForCompletion.points} XP Earned! 🏆`, 'success');
        this.isCompleteModalOpen = false;
        this.activeChallenges.update(prev => prev.filter(c => c.id !== this.selectedChallengeForCompletion.id));

        const currentUser = this.auth.currentUser();
        if (currentUser) {
          const newPoints = (currentUser.points || 0) + (this.selectedChallengeForCompletion.points || 0);
          this.auth.currentUser.set({ ...currentUser, points: newPoints });
        }
      },
      error: (err) => {
        this.showToast('Failed to complete challenge.', 'error');
      }
    });
  }

  private triggerSuccessAnimation() {
    const duration = 3 * 1000;
    const end = Date.now() + duration;

    const frame = () => {
      confetti({
        particleCount: 3,
        angle: 60,
        spread: 55,
        origin: { x: 0 },
        colors: ['#a855f7', '#3b82f6', '#22c55e']
      });
      confetti({
        particleCount: 3,
        angle: 120,
        spread: 55,
        origin: { x: 1 },
        colors: ['#a855f7', '#3b82f6', '#22c55e']
      });

      if (Date.now() < end) {
        requestAnimationFrame(frame);
      }
    };
    frame();
  }

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
}//
