import {Component, OnInit, ChangeDetectorRef, inject, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Challenge, Difficulty } from './challenge.model';
import { ChallengeService } from '../../../services/challenge.service';

@Component({
  selector: 'app-challenges',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './challenges-component.html',
  styleUrls: ['./challenges-component.css']
})
export class ChallengesComponent implements OnInit {
  private challengeService = inject(ChallengeService);
  private cdr = inject(ChangeDetectorRef);

  challenges = signal<Challenge[]>([]);
  difficultyKeys = Object.values(Difficulty) as Difficulty[];

  // State for modal
  isCreateModalOpen = signal(false);
  isLoading = signal(false);
  errorMessage = signal('');

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
}
