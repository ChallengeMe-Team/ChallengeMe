import { Component, OnInit, inject, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';

import { Challenge, Difficulty } from '../../../models/challenge.model';
import { ChallengeService } from '../../../services/challenge.service';
import { AuthService } from '../../../services/auth.service';
import { ChallengeFormComponent } from '../../forms/challenge-form/challenge-form';
import { ToastComponent } from '../../../shared/toast/toast-component';
import { AcceptChallengeModalComponent } from '../../accept-challenge-modal/accept-challenge-modal';
import { AssignChallengeModalComponent } from '../../assign-challenge-modal/assign-challenge-modal';
import { ChallengeCardComponent } from '../../../shared/challenge-card/challenge-card.component';
import { ConfirmationModalComponent } from '../../../shared/confirmation-modal/confirmation-modal.component';
import { FriendDTO } from '../../../services/user.service';

@Component({
  selector: 'app-challenges',
  standalone: true,
  imports: [
    CommonModule,
    ToastComponent,
    ChallengeFormComponent,
    AcceptChallengeModalComponent,
    AssignChallengeModalComponent,
    ChallengeCardComponent,
    ConfirmationModalComponent
  ],
  templateUrl: './challenges-component.html'
})
export class ChallengesComponent implements OnInit {
  public challengeService = inject(ChallengeService);
  public auth = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  constructor(private route: ActivatedRoute) {
    this.route.queryParams.subscribe(params => {
      if (params['openModal'] === 'true') {
        this.challengeService.isCreateModalOpen.set(true);
      }
    });
  }

  challenges = signal<Challenge[]>([]);
  userChallengeStatuses = signal<Map<string, string>>(new Map());
  difficultyKeys = Object.values(Difficulty) as Difficulty[];

  isLoading = signal(false);
  errorMessage = signal('');

  // Modal States
  isRestartModalOpen = false;
  challengeToRestart: Challenge | null = null;
  isEditModalOpen = signal(false);
  editChallenge = signal<any | null>(null);
  isDeleteModalOpen = signal(false);
  challengeToDelete = signal<Challenge | null>(null);
  isAcceptModalOpen = false;
  selectedChallengeForContract: any = null;
  isAssignModalOpen = false;
  selectedChallengeToAssign = signal<Challenge | null>(null);

  // Toast State
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  ngOnInit(): void {
    this.loadChallenges();
    this.loadUserActiveChallenges();
  }

  loadChallenges() {
    this.isLoading.set(true);
    this.challengeService.getAllChallenges().subscribe({
      next: (data) => {
        this.challenges.set(data);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage.set('Error loading data.');
        this.isLoading.set(false);
      }
    });
  }

  loadUserActiveChallenges() {
    const user = this.auth.currentUser();
    if (!user) return;

    this.challengeService.getAllUserChallengeLinks(user.id).subscribe({
      next: (data) => {
        const statusMap = new Map<string, string>();
        data.forEach((link: any) => {
          const cId = link.challenge?.id || link.challengeId;
          statusMap.set(cId, link.status);
        });
        this.userChallengeStatuses.set(statusMap);
      }
    });
  }

  getChallengesByDifficulty(difficulty: Difficulty): Challenge[] {
    return this.challenges().filter(c => c.difficulty === difficulty);
  }

  // Action Handlers (Evenimente primite de la ChallengeCard)
  handleStart(challenge: Challenge) {
    this.selectedChallengeForContract = challenge;
    this.isAcceptModalOpen = true;
  }

  handleRestart(challenge: Challenge) {
    this.challengeToRestart = challenge;
    this.isRestartModalOpen = true;
  }

  handleAssign(challenge: Challenge) {
    this.selectedChallengeToAssign.set(challenge);
    this.isAssignModalOpen = true;
  }

  handleEdit(challenge: Challenge) {
    const user = this.auth.currentUser();
    if (!user || user.username !== challenge.createdBy) {
      this.showToast("You can only edit challenges created by you.", "error");
      return;
    }
    this.editChallenge.set(challenge);
    this.isEditModalOpen.set(true);
  }

  handleDeleteRequest(data: {event: MouseEvent, challenge: Challenge}) {
    data.event.preventDefault();
    const user = this.auth.currentUser();
    if (!user || user.username !== data.challenge.createdBy) {
      this.showToast("You can only delete challenges created by you.", "error");
      return;
    }
    this.challengeToDelete.set(data.challenge);
    this.isDeleteModalOpen.set(true);
  }

  confirmRestart() {
    if (!this.challengeToRestart) return;
    const challenge = this.challengeToRestart;
    const user = this.auth.currentUser();
    if (!user) return;

    this.isRestartModalOpen = false;

    this.challengeService.getAllUserChallengeLinks(user.id).subscribe(links => {
      const linkToUpdate = links.find((l: any) => (l.challenge?.id || l.challengeId) === challenge.id);

      if (linkToUpdate) {
        // ÎN LOC DE DELETE, FACEM UPDATE LA ACCEPTED
        const payload = {
          status: 'ACCEPTED',
          startDate: new Date().toISOString().split('T')[0] // Resetăm data de start la azi
        };

        this.challengeService.updateChallengeUser(linkToUpdate.id, payload).subscribe({
          next: () => {
            this.loadUserActiveChallenges(); // Reîncărcăm statusurile
            this.showToast('Challenge restarted! Good luck again.', 'success');
            this.router.navigate(['/my-challenges']);
          },
          error: () => this.showToast('Failed to restart.', 'error')
        });
      }
    });
  }

  onFriendAssigned(friend: FriendDTO) {
    const challenge = this.selectedChallengeToAssign();
    if (!challenge) return;
    this.challengeService.assignChallenge(challenge.id, friend.id).subscribe({
      next: () => {
        this.showToast(`Challenge sent to ${friend.username}!`, "success");
        this.isAssignModalOpen = false;
      },
      error: (err) => this.showToast(err.error?.message || "Failed to assign", "error")
    });
  }

  onCreateChallenge(formValues: any) {
    const user = this.auth.currentUser();
    const newChallenge = { ...formValues, createdBy: user ? user.username : 'Anonymous' };
    this.challengeService.createChallenge(newChallenge).subscribe({
      next: () => {
        this.showToast("Challenge created!", "success");
        this.challengeService.isCreateModalOpen.set(false);
        this.loadChallenges();
      }
    });
  }

  onContractSigned(dates: { start: string, end: string }) {
    if (!this.selectedChallengeForContract) return;
    this.challengeService.acceptChallenge(this.selectedChallengeForContract.id, dates.start, dates.end).subscribe({
      next: () => {
        this.isAcceptModalOpen = false;
        this.showToast("Commitment signed! 🚀", "success");
        setTimeout(() => { this.router.navigate(['/my-challenges']); }, 1500);
      }
    });
  }

  onChallengeUpdated(updated: any) {
    this.challengeService.updateChallenge(updated.id, updated).subscribe({
      next: () => {
        this.showToast("Challenge updated!", "success");
        this.loadChallenges();
        this.isEditModalOpen.set(false);
      }
    });
  }

  confirmDelete() {
    const challenge = this.challengeToDelete();
    if (!challenge) return;
    this.challengeService.deleteChallenge(challenge.id).subscribe({
      next: () => {
        this.showToast("Deleted successfully.", "success");
        this.challenges.update(prev => prev.filter(c => c.id !== challenge.id));
        this.isDeleteModalOpen.set(false);
      }
    });
  }

  showToast(message: string, type: 'success' | 'error' = 'error') {
    this.toastMessage.set(message);
    this.toastType.set(type);
    setTimeout(() => { this.toastMessage.set(null); }, 3000);
  }

  getCategoryClass(category: string): string {
    const mapping: { [key: string]: string } = {
      'Fitness': 'bg-fuchsia-500',
      'Health': 'bg-rose-600',
      'Mindfulness': 'bg-blue-400',
      'Education': 'bg-purple-600',
      'Coding': 'bg-indigo-600',
      'Creativity': 'bg-amber-500',
      'Social': 'bg-orange-500',
      'Food': 'bg-yellow-600',
      'Lifestyle': 'bg-emerald-500'
    };
    // Returnează culoarea din mapare sau o culoare gri implicită dacă categoria nu este găsită
    return mapping[category] || 'bg-gray-500';
  }
}
