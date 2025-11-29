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
  imports: [CommonModule, ChallengeFormComponent, ToastComponent],
  templateUrl: './challenges-component.html',
  styleUrls: ['./challenges-component.css']
})
export class ChallengesComponent implements OnInit {
  private challengeService = inject(ChallengeService);
  private cdr = inject(ChangeDetectorRef);
  private auth = inject(AuthService);


  challenges = signal<Challenge[]>([]);
  difficultyKeys = Object.values(Difficulty) as Difficulty[];

  // State for modal
  isCreateModalOpen = signal(false);
  isLoading = signal(false);
  errorMessage = signal('');
  isEditModalOpen = signal(false);
  editChallenge = signal<any | null>(null);
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');



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

  openCreateModal() {
    this.isCreateModalOpen.set(true);
  }

  closeCreateModal() {
    this.isCreateModalOpen.set(false);
  }

  onChallengeCreated() {
    this.loadChallenges();
    this.closeCreateModal();
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

}
