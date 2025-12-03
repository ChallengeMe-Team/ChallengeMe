import {Component, OnInit, ChangeDetectorRef, inject, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Challenge, Difficulty } from './challenge.model';
import { ChallengeService } from '../../../services/challenge.service';
import { AuthService } from '../../../services/auth.service';
import {ChallengeFormComponent} from '../../forms/challenge-form/challenge-form';
import {ToastComponent} from '../../../shared/toast/toast-component';


@Component({
  selector: 'app-challenges',
  standalone: true,
  imports: [CommonModule, ToastComponent, ChallengeFormComponent],
  templateUrl: './challenges-component.html',
  styleUrls: ['./challenges-component.css']
})
export class ChallengesComponent implements OnInit {
  public challengeService = inject(ChallengeService);
  private cdr = inject(ChangeDetectorRef);
  private auth = inject(AuthService);


  challenges = signal<Challenge[]>([]);
  difficultyKeys = Object.values(Difficulty) as Difficulty[];

  // State for modal
  isLoading = signal(false);
  errorMessage = signal('');
  isEditModalOpen = signal(false);
  editChallenge = signal<any | null>(null);
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');
  isDeleteModalOpen = signal(false);
  challengeToDelete = signal<Challenge | null>(null);


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
    // 1. Oprim meniul browserului (Inspect, Save As, etc.)
    event.preventDefault();

    const user = this.auth.currentUser();

    // 2. Verificam Ownership-ul
    if (!user || user.username !== challenge.createdBy) {
      // Cazul A: Nu esti proprietar -> Eroare Rosie
      this.showToast("You can only delete challenges created by you.", "error");
      return;
    }

    // Cazul B: Esti proprietar -> Deschidem fereastra de confirmare
    this.challengeToDelete.set(challenge);
    this.isDeleteModalOpen.set(true);
  }
  confirmDelete() {
    const challenge = this.challengeToDelete();
    if (!challenge) return;

    // Apelam Backend-ul
    this.challengeService.deleteChallenge(challenge.id).subscribe({
      next: () => {
        // Succes: Mesaj Verde + Scoatem din lista instant (fara reload la toata pagina)
        this.showToast("Challenge deleted successfully.", "success");

        // Actualizam lista locala (filtram elementul sters)
        this.challenges.update(prev => prev.filter(c => c.id !== challenge.id));

        // Inchidem modala
        this.closeDeleteModal();
      },
      error: (err) => {
        // Eroare (ex: Backend-ul zice 403 Forbidden daca cineva a "fentat" UI-ul)
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
