import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

export enum Difficulty {
  EASY = 'EASY',
  MEDIUM = 'MEDIUM',
  HARD = 'HARD'
}

interface Challenge {
  title: string;
  description: string;
  category: string;
  difficulty: Difficulty;
  points: number;
}

@Component({
  selector: 'app-challenges',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './challenges-component.html',
  styleUrls: ['./challenges-component.css']
})
export class ChallengesComponent {
  challenges: Challenge[] = [
    { title: '30-Day Fitness Challenge', description: 'Complete daily exercises and track your progress.', category: 'Fitness', difficulty: Difficulty.EASY, points: 100 },
    { title: 'Reading Challenge', description: 'Read one book per week and earn points.', category: 'Education', difficulty: Difficulty.MEDIUM, points: 200 },
    { title: '60-Day Fitness Challenge', description: 'Complete daily exercises and track your progress.', category: 'Fitness', difficulty: Difficulty.MEDIUM, points: 200 },
    { title: 'Coding Challenge', description: 'Solve algorithm tasks and improve your skills.', category: 'Coding', difficulty: Difficulty.HARD, points: 300 },
  ];

  difficultyKeys = Object.values(Difficulty) as Difficulty[];
  getChallengesByDifficulty(difficulty: Difficulty) {
    return this.challenges.filter(c => c.difficulty === difficulty);
  }
}
