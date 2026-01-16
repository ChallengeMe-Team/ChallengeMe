import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { ChallengeService } from '../../services/challenge.service';
import { BadgeService } from '../../services/badge.service';
import { UserService } from '../../services/user.service';

import { LucideAngularModule, Zap, CheckCircle, Award } from 'lucide-angular';

/**
 * This component functions as a real-time data visualizer for user metrics.
 * It features procedural number animations and handles multi-source data
 * synchronization through the User Profile aggregate.
 * * * Key Technical Aspects:
 * - Procedural Animation: Implements a custom 'easeOut' math function within
 * requestAnimationFrame to create a smooth counting effect for XP and stats.
 * - Change Detection Strategy: Explicitly uses ChangeDetectorRef to force UI
 * updates during micro-frame animations, bypassing standard zone triggers for performance.
 * - Icon Integration: Uses the Lucide-Angular library for a standardized,
 * lightweight vector iconography system.
 */
@Component({
  selector: 'app-stats-grid',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './stats-grid.component.html',
  styleUrls: ['./stats-grid.component.css']
})
export class StatsGridComponent implements OnInit {
  // --- ICON DEFINITIONS ---
  /** Standardized Lucide icons for XP (Zap), Missions (Check), and Achievement (Award). */
  readonly zapIcon = Zap;
  readonly checkIcon = CheckCircle;
  readonly awardIcon = Award;

  // --- SERVICE INJECTIONS ---
  private authService = inject(AuthService);
  private challengeService = inject(ChallengeService);
  private badgeService = inject(BadgeService);
  private userService = inject(UserService);
  private cdr = inject(ChangeDetectorRef);

  // --- DISPLAY PROPERTIES (Animated) ---
  displayPoints: number = 0;
  displayChallenges: number = 0;
  displayBadges: number = 0;

  isLoading: boolean = true;

  /**
   * Triggers the data retrieval process from the User Service.
   * Once the profile DTO is received, it initializes the counting animations for:
   * 1. Total XP (points)
   * 2. Completed Missions (totalCompletedChallenges)
   * 3. Badge Collection Count (badges.length)
   */
  ngOnInit(): void {
    this.userService.getProfile().subscribe({
      next: (userProfile) => {
        this.isLoading = false;

        // Synchronize and animate Experience Points
        this.animateValue(userProfile.points, (val) => {
          this.displayPoints = val;
          this.cdr.detectChanges(); // Force view update during animation frame
        });

        // Synchronize and animate Mission counts with null-safety check
        const missionsValue = userProfile.totalCompletedChallenges ?? 0;
        this.animateValue(missionsValue, (val) => {
          this.displayChallenges = val;
          this.cdr.detectChanges();
        });

        // Synchronize and animate Badge count
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

  /**
   * A procedural animation engine that increments numbers over a set duration.
   * Logic: Uses a quadratic ease-out function: 1 - (1 - x)^2.
   * @param targetValue The final number to reach.
   * @param setter Callback to update the specific property.
   * @param duration Total time for the animation in milliseconds (Default: 1500ms).
   */
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

      // QUADRATIC EASE OUT: Starts fast, slows down at the end for "premium" feel
      const easeOut = 1 - (1 - progress) * (1 - progress);
      const currentValue = Math.floor(startValue + (targetValue - startValue) * easeOut);
      setter(currentValue);

      if (progress < 1) {
        requestAnimationFrame(update);
      } else {
        setter(targetValue); // Final snap to target
      }
    };

    requestAnimationFrame(update);
  }
}
