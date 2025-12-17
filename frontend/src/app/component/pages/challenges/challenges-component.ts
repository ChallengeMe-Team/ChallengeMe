import { Component, OnInit, ChangeDetectorRef, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router'; // 1. Import Router pentru redirect

import { Challenge, Difficulty } from './challenge.model';
import { ChallengeService } from '../../../services/challenge.service';
import { AuthService } from '../../../services/auth.service';
import { ChallengeFormComponent } from '../../forms/challenge-form/challenge-form';
import { ToastComponent } from '../../../shared/toast/toast-component';
import {AcceptChallengeModalComponent} from '../../accept-challenge-modal/accept-challenge-modal';

// 2. Import Componenta Noua

@Component({
  selector: 'app-challenges',
  standalone: true,
  // 3. Adaugam componenta in lista de imports
  imports: [
    CommonModule,
    ToastComponent,
    ChallengeFormComponent,
    AcceptChallengeModalComponent
  ],
  templateUrl: './challenges-component.html',
  styleUrls: ['./challenges-component.css']
})
export class ChallengesComponent implements OnInit {
  public challengeService = inject(ChallengeService);
  private cdr = inject(ChangeDetectorRef);
  private auth = inject(AuthService);
  private router = inject(Router); // 4. Injectam Router-ul

  challenges = signal<Challenge[]>([]);
  difficultyKeys = Object.values(Difficulty) as Difficulty[];

  // State for modal
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

  // 5. Variables for Accept/Contract Modal
  isAcceptModalOpen = false;
  selectedChallengeForContract: any = null;

  ngOnInit(): void {
    this.loadChallenges();
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
      next: (createdChallenge) => {
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

  // --- LOGICA PENTRU ACCEPT CHALLENGE (CONTRACT) ---

  // Se apeleaza cand dai click pe "Start Challenge"
  openContractModal(challenge: any) {
    this.selectedChallengeForContract = challenge;
    this.isAcceptModalOpen = true;
  }

  // Se apeleaza cand userul da click pe "Sign & Start" in modal
  onContractSigned(dates: { start: string, end: string }) {
    if (!this.selectedChallengeForContract) return;

    this.challengeService.acceptChallenge(
      this.selectedChallengeForContract.id,
      dates.start,
      dates.end
    ).subscribe({
      next: () => {
        // 1. Inchidem modalul
        this.isAcceptModalOpen = false;

        // 2. Afisam succes
        this.showToast("Commitment signed! Good luck! 🚀", "success");

        // 3. Optional: Redirectionam catre pagina "My Challenges"
        // (Daca ai o ruta definita pentru asta)
        this.router.navigate(['/my-challenges']);

        // Alternativa: Doar reincarcam lista daca ramanem pe pagina
        // this.loadChallenges();
        setTimeout(() => {
          this.router.navigate(['/my-challenges']);
        }, 1500);
      },
      error: (err) => {
        console.error(err);
        this.showToast(err.error?.message || "Failed to accept challenge.", "error");
      }
    });
  }

  // --------------------------------------------------

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
