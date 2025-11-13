import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './leaderboard-component.html',
  styleUrls: ['./leaderboard-component.css']
})
export class LeaderboardComponent {
  players = [
    { rank: 1, username: 'ChampionX', points: 2450, completedChallenges: 28 },
    { rank: 2, username: 'MotivatedMike', points: 2100, completedChallenges: 24 },
    { rank: 3, username: 'FitnessFreak', points: 1850, completedChallenges: 21 },
    { rank: 4, username: 'ProductivePro', points: 1620, completedChallenges: 19 },
    { rank: 5, username: 'ChallengeSeeker', points: 1480, completedChallenges: 17 },
    { rank: 6, username: 'GoalCrusher', points: 1390, completedChallenges: 16 }
  ];

  podium = this.players.slice(0, 3);
  rest = this.players.slice(3);

  getPodiumHeight(rank: number) {
    switch (rank) {
      case 1: return '200px';
      case 2: return '160px';
      case 3: return '140px';
      default: return '100px';
    }
  }

  getPodiumClass(rank: number) {
    switch (rank) {
      case 1: return 'first-place';
      case 2: return 'second-place';
      case 3: return 'third-place';
      default: return '';
    }
  }
}
