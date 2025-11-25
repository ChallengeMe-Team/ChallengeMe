import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Challenge, Difficulty } from './challenge.model';
import { ChallengeService } from './challenge.service';

@Component({
  selector: 'app-challenges',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './challenges-component.html',
  styleUrls: ['./challenges-component.css'],
  providers: [ChallengeService]
})
export class ChallengesComponent implements OnInit {

  challenges: Challenge[] = [];
  difficultyKeys = Object.values(Difficulty) as Difficulty[];

  constructor(
    private challengeService: ChallengeService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadChallenges();
  }

  loadChallenges() {
    this.challengeService.getAllChallenges().subscribe({
      next: (data: Challenge[]) => {
        this.challenges = data;
        console.log('Challenges loaded successfully:', this.challenges);

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error loading challenges:', error);
      }
    });
  }

  getChallengesByDifficulty(difficulty: Difficulty): Challenge[] {
    return this.challenges.filter(c => c.difficulty === difficulty);
  }
}
