import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
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

  private cdr = inject(ChangeDetectorRef); // 2. Injectează ChangeDetectorRef

  displayPoints: number = 0;
  displayChallenges: number = 0;
  displayBadges: number = 0;

  isLoading: boolean = true;

  ngOnInit(): void {
    // Chemăm doar profilul, deoarece acesta conține deja punctele, numărul de misiuni și insignele
    this.userService.getProfile().subscribe({
      next: (userProfile) => {
        this.isLoading = false;

        // Sincronizare directă cu câmpurile din JSON-ul tău
        this.animateValue(userProfile.points, (val) => {
          this.displayPoints = val;
          this.cdr.detectChanges();
        });

        this.animateValue(userProfile.completedChallengesCount, (val) => {
          this.displayChallenges = val;
          this.cdr.detectChanges();
        });

        this.animateValue(userProfile.badges.length, (val) => {
          this.displayBadges = val;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        console.error('Error loading profile', err);
        this.isLoading = false;
        this.cdr.markForCheck();
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
