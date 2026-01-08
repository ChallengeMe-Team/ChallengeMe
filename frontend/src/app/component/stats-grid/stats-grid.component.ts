import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { ChallengeService } from '../../services/challenge.service';
import { BadgeService } from '../../services/badge.service';
import { UserService } from '../../services/user.service';

// 1. Importăm Iconițele și Modulul
import { LucideAngularModule, Zap, CheckCircle, Award } from 'lucide-angular';

@Component({
  selector: 'app-stats-grid',
  standalone: true,
  // 2. Adăugăm modulul în imports
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './stats-grid.component.html',
  styleUrls: ['./stats-grid.component.css']
})
export class StatsGridComponent implements OnInit {
  // 3. Definim iconițele ca variabile pentru a le folosi în HTML
  readonly zapIcon = Zap;
  readonly checkIcon = CheckCircle;
  readonly awardIcon = Award;

  private authService = inject(AuthService);
  private challengeService = inject(ChallengeService);
  private badgeService = inject(BadgeService);
  private userService = inject(UserService);

  displayPoints: number = 0;
  displayChallenges: number = 0;
  displayBadges: number = 0;

  isLoading: boolean = true;

  ngOnInit(): void {
    const username = this.authService.getUsername();

    if (!username) {
      console.error('User not logged in');
      return;
    }

    forkJoin({
      userProfile: this.userService.getProfile(),
      challenges: this.challengeService.getUserChallenges(username),
      badges: this.badgeService.getUserBadges(username)
    }).pipe(
      map(data => {
        return {
          points: data.userProfile.points,
          completedChallengesCount: (data.challenges as any[]).filter(c => c.status === 'COMPLETED').length,
          badgesCount: data.badges.length
        };
      })
    ).subscribe({
      next: (stats) => {
        this.isLoading = false;
        this.animateValue(stats.points, (val) => this.displayPoints = val);
        this.animateValue(stats.completedChallengesCount, (val) => this.displayChallenges = val);
        this.animateValue(stats.badgesCount, (val) => this.displayBadges = val);
      },
      error: (err) => {
        console.error('Error loading stats', err);
        this.isLoading = false;
      }
    });
  }

  private animateValue(targetValue: number, setter: (val: number) => void, duration: number = 1500): void {
    if (targetValue === 0) {
      setter(0);
      return;
    }

    const startValue = 0;
    const startTime = performance.now();

    const update = (currentTime: number) => {
      const elapsed = currentTime - startTime;
      const progress = Math.min(elapsed / duration, 1);
      const easeOut = 1 - (1 - progress) * (1 - progress);
      const currentValue = Math.floor(startValue + (targetValue - startValue) * easeOut);
      setter(currentValue);

      if (progress < 1) {
        requestAnimationFrame(update);
      } else {
        setter(targetValue);
      }
    };

    requestAnimationFrame(update);
  }
}
