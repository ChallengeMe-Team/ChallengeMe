import { Component, OnInit, ChangeDetectorRef, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { Challenge, Difficulty } from './challenge.model';
import { ChallengeService } from '../../../services/challenge.service';
import { AuthService } from '../../../services/auth.service';
import { ChallengeFormComponent } from '../../forms/challenge-form/challenge-form';
import { ToastComponent } from '../../../shared/toast/toast-component';
import { AcceptChallengeModalComponent } from '../../accept-challenge-modal/accept-challenge-modal';
import { AssignChallengeModalComponent } from '../../assign-challenge-modal/assign-challenge-modal';
import { FriendDTO } from '../../../services/user.service';
import { LucideAngularModule, CheckCircle, RotateCcw } from 'lucide-angular';
import { ConfirmationModalComponent } from '../../../shared/confirmation-modal/confirmation-modal.component'; // Verifică calea

@Component({
  selector: 'app-challenges',
  standalone: true,
  imports: [
    CommonModule,
    ToastComponent,
    ChallengeFormComponent,
    AcceptChallengeModalComponent,
    AssignChallengeModalComponent,
    LucideAngularModule,
    ConfirmationModalComponent
  ],
  templateUrl: './challenges-component.html',
  styleUrls: ['./challenges-component.css']
})
export class ChallengesComponent implements OnInit {
  public challengeService = inject(ChallengeService);
  private cdr = inject(ChangeDetectorRef);
  public auth = inject(AuthService); // Public pentru template
  private router = inject(Router);

  // Mapare ID Challenge -> Status (ex: "ACCEPTED", "COMPLETED")
  userChallengeStatuses = signal<Map<string, string>>(new Map());

  challenges = signal<Challenge[]>([]);
  difficultyKeys = Object.values(Difficulty) as Difficulty[];

  // State general
  isLoading = signal(false);
  errorMessage = signal('');

  //// State pentru Modalul de Restart
  isRestartModalOpen = false;
  challengeToRestart: Challenge | null = null;

  // Edit & Create Modals
  isEditModalOpen = signal(false);
  editChallenge = signal<any | null>(null);

  // Toast
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  // Delete Modal
  isDeleteModalOpen = signal(false);
  challengeToDelete = signal<Challenge | null>(null);

  // Accept/Contract Modal
  isAcceptModalOpen = false;
  selectedChallengeForContract: any = null;

  // Assign Challenge Modal
  isAssignModalOpen = false;
  selectedChallengeToAssign = signal<Challenge | null>(null);

  // Iconite
  readonly icons = { CheckCircle, RotateCcw };

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
      error: (err) => {
        console.error(err);
        this.errorMessage.set('Eroare la incarcarea datelor.');
        this.isLoading.set(false);
      }
    });
  }

  getChallengesByDifficulty(difficulty: Difficulty): Challenge[] {
    return this.challenges().filter(c => c.difficulty === difficulty);
  }

  // --- LOGICĂ STATUS UTILIZATOR ---

  loadUserActiveChallenges() {
    const user = this.auth.currentUser();
    if (!user) return;

    this.challengeService.getAllUserChallengeLinks(user.id).subscribe({
      next: (data) => {
        const statusMap = new Map<string, string>();
        data.forEach((link: any) => {
          // Uneori vine populat (link.challenge.id), alteori doar ID (link.challengeId)
          const cId = link.challenge?.id || link.challengeId;
          statusMap.set(cId, link.status);
        });
        this.userChallengeStatuses.set(statusMap);
      },
      error: (err) => console.error('Could not load user challenges status', err)
    });
  }

  getChallengeStatus(challengeId: string): string | undefined {
    return this.userChallengeStatuses().get(challengeId);
  }

  // --- LOGICĂ START AGAIN (RESET) ---

  onStartAgain(challenge: Challenge, event: Event) {
    event.stopPropagation();
    this.challengeToRestart = challenge;
    this.isRestartModalOpen = true; // Deschide modalul custom
  }

  // 2. Metoda care se execută când dai "Confirm" în modal
  confirmRestart() {
    if (!this.challengeToRestart) return;

    this.isRestartModalOpen = false; // Închide modalul
    const challenge = this.challengeToRestart;
    const user = this.auth.currentUser();

    if (!user) return;

    // Logica de ștergere și restart (aceeași ca înainte)
    this.challengeService.getAllUserChallengeLinks(user.id).subscribe(links => {
      const linkToDelete = links.find((l: any) => (l.challenge?.id || l.challengeId) === challenge.id);

      if (linkToDelete) {
        this.challengeService.deleteChallengeUser(linkToDelete.id).subscribe({
          next: () => {
            this.openContractModal(challenge);

            // Update UI
            const newMap = new Map(this.userChallengeStatuses());
            newMap.delete(challenge.id);
            this.userChallengeStatuses.set(newMap);
            this.showToast('Progress reset. Ready to restart!', 'success');
          },
          error: () => this.showToast('Could not reset challenge.', 'error')
        });
      } else {
        this.openContractModal(challenge);
      }
    });
  }

  // --- LOGICA THROW CHALLENGE ---

  openAssignModal(challenge: Challenge) {
    this.selectedChallengeToAssign.set(challenge);
    this.isAssignModalOpen = true;
  }

  onFriendAssigned(friend: FriendDTO) {
    const challenge = this.selectedChallengeToAssign();
    if (!challenge) return;

    this.challengeService.assignChallenge(challenge.id, friend.id).subscribe({
      next: () => {
        this.showToast(`Challenge sent to ${friend.username}!`, "success");
        this.isAssignModalOpen = false;
        this.selectedChallengeToAssign.set(null);
      },
      error: (err) => {
        const errorMsg = err.error?.message || err.message || "";
        if (errorMsg.includes("already") || err.status === 409) {
          this.showToast(`You already sent this challenge to ${friend.username}`, "error");
        } else {
          this.showToast("Failed to assign challenge.", "error");
        }
      }
    });
  }

  // --- LOGICA CREATE/EDIT/DELETE ---

  closeCreateModal() {
    this.challengeService.isCreateModalOpen.set(false);
  }

  onCreateChallenge(formValues: any) {
    const user = this.auth.currentUser();
    const newChallenge = {
      ...formValues,
      createdBy: user ? user.username : 'Anonymous'
    };

    this.isLoading.set(true);
    this.challengeService.createChallenge(newChallenge).subscribe({
      next: () => {
        this.showToast("Challenge created successfully!", "success");
        this.closeCreateModal();
        this.loadChallenges();
      },
      error: (err) => {
        console.error(err);
        this.showToast("Error creating challenge.", "error");
        this.isLoading.set(false);
      }
    });
  }

  onChallengeDoubleClick(challenge: Challenge) {
    const user = this.auth.currentUser();
    if (!user || user.username !== challenge.createdBy) {
      this.showToast("You can only edit challenges created by you.", "error");
      return;
    }
    this.editChallenge.set(challenge);
    this.isEditModalOpen.set(true);
  }

  openContractModal(challenge: any) {
    this.selectedChallengeForContract = challenge;
    this.isAcceptModalOpen = true;
  }

  onContractSigned(dates: { start: string, end: string }) {
    if (!this.selectedChallengeForContract) return;

    this.challengeService.acceptChallenge(
      this.selectedChallengeForContract.id,
      dates.start,
      dates.end
    ).subscribe({
      next: () => {
        this.isAcceptModalOpen = false;
        this.showToast("Commitment signed! Good luck! 🚀", "success");
        setTimeout(() => {
          this.router.navigate(['/my-challenges']);
        }, 1500);
        this.loadUserActiveChallenges();
      },
      error: (err) => {
        console.error(err);
        this.showToast(err.error?.message || "Failed to accept challenge.", "error");
      }
    });
  }

  onChallengeUpdated(updated: any) {
    this.challengeService.updateChallenge(updated.id, updated).subscribe({
      next: () => {
        this.showToast("Challenge updated successfully!", "success");
        this.loadChallenges();
        this.isEditModalOpen.set(false);
      },
      error: (err) => {
        this.showToast(err.error?.message || "Error updating challenge.", "error");
      }
    });
  }

  showToast(message: string, type: 'success' | 'error' = 'error') {
    this.toastMessage.set(message);
    this.toastType.set(type);
    setTimeout(() => {
      this.toastMessage.set(null);
    }, 3000);
  }

  onRightClick(event: MouseEvent, challenge: Challenge) {
    event.preventDefault();
    const user = this.auth.currentUser();
    if (!user || user.username !== challenge.createdBy) {
      this.showToast("You can only delete challenges created by you.", "error");
      return;
    }
    this.challengeToDelete.set(challenge);
    this.isDeleteModalOpen.set(true);
  }

  confirmDelete() {
    const challenge = this.challengeToDelete();
    if (!challenge) return;

    this.challengeService.deleteChallenge(challenge.id).subscribe({
      next: () => {
        this.showToast("Challenge deleted successfully.", "success");
        this.challenges.update(prev => prev.filter(c => c.id !== challenge.id));
        this.closeDeleteModal();
      },
      error: (err) => {
        console.error(err);
        this.showToast("Error deleting challenge.", "error");
        this.closeDeleteModal();
      }
    });
  }

  closeDeleteModal() {
    this.isDeleteModalOpen.set(false);
    this.challengeToDelete.set(null);
  }
}
