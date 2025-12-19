import { Component, OnInit, ChangeDetectorRef, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { Challenge, Difficulty } from './challenge.model';
import { ChallengeService } from '../../../services/challenge.service';
import { AuthService } from '../../../services/auth.service';
import { ChallengeFormComponent } from '../../forms/challenge-form/challenge-form';
import { ToastComponent } from '../../../shared/toast/toast-component';
import { AcceptChallengeModalComponent } from '../../accept-challenge-modal/accept-challenge-modal';
// Import corectat conform structurii tale de directoare
import { AssignChallengeModalComponent } from '../../assign-challenge-modal/assign-challenge-modal';
import { FriendDTO } from '../../../services/user.service';

@Component({
  selector: 'app-challenges',
  standalone: true,
  imports: [
    CommonModule,
    ToastComponent,
    ChallengeFormComponent,
    AcceptChallengeModalComponent,
    AssignChallengeModalComponent // Adăugat în lista de imports
  ],
  templateUrl: './challenges-component.html',
  styleUrls: ['./challenges-component.css']
})
export class ChallengesComponent implements OnInit {
  public challengeService = inject(ChallengeService);
  private cdr = inject(ChangeDetectorRef);
  private auth = inject(AuthService);
  private router = inject(Router);

  activeChallengeIds = signal<Set<string>>(new Set());

  challenges = signal<Challenge[]>([]);
  difficultyKeys = Object.values(Difficulty) as Difficulty[];

  // State general
  isLoading = signal(false);
  errorMessage = signal('');

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

  // --- State pentru Assign Challenge Modal ---
  isAssignModalOpen = false;
  selectedChallengeToAssign = signal<Challenge | null>(null);

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

  // NEW: Încarcă relațiile user-challenge existente
  loadUserActiveChallenges() {
    const user = this.auth.currentUser();
    if (!user) return;

    this.challengeService.getAllUserChallengeLinks(user.id).subscribe({
      next: (data) => {
        // Colectăm ID-urile challenge-urilor într-un Set pentru căutare rapidă
        // data este un array de ChallengeUserDTO sau entități care conțin obiectul 'challenge' sau 'challengeId'
        const ids = new Set(data.map((link: any) =>
          // Verificăm structura: poate fi link.challenge.id sau link.challengeId direct
          link.challenge?.id || link.challengeId
        ));
        this.activeChallengeIds.set(ids);
      },
      error: (err) => console.error('Could not load user challenges status', err)
    });
  }

  // NEW: Helper pentru template
  isChallengeActive(challengeId: string): boolean {
    return this.activeChallengeIds().has(challengeId);
  }

  // --- Logica pentru Throw Challenge ---

  openAssignModal(challenge: Challenge) {
    this.selectedChallengeToAssign.set(challenge); // Salvează challenge-ul pe care vrei să îl trimiți
    this.isAssignModalOpen = true; // Deschide modalul
  }

  onFriendAssigned(friend: FriendDTO) {
    const challenge = this.selectedChallengeToAssign();
    if (!challenge) return;

    // Apelăm service-ul cu cele două ID-uri conform semnăturii tale de metodă
    this.challengeService.assignChallenge(challenge.id, friend.id).subscribe({
      next: () => {
        this.showToast(`Challenge sent to ${friend.username}!`, "success"); // Feedback succes
        this.isAssignModalOpen = false; // Închide modalul automat
        this.selectedChallengeToAssign.set(null);
      },
      error: (err) => {
        // Gestionare eroare duplicat conform Acceptance Criteria
        const errorMsg = err.error?.message || err.message || "";
        if (errorMsg.includes("already") || err.status === 409) {
          this.showToast(`You already sent this challenge to ${friend.username}`, "error");
        } else {
          this.showToast("Failed to assign challenge.", "error");
        }
      }
    });
  }

  // --- Logica existenta: Create/Edit/Delete ---

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
