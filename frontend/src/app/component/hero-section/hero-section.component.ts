import { Component, OnInit, inject, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { UserProfile } from '../../models/user.model';

/**
 * Component representing the main user dashboard header.
 * It calculates and displays gamification metrics such as Level, Current XP,
 * and progress towards the next rank.
 * * * Key Architectural Features:
 * - Angular Signals: Uses 'signal' for the core profile data and 'computed' for all derived metrics.
 * - Reactive Progress: Automatically recalculates Level and XP percentages whenever the point total updates.
 * - Social Proof Logic: Generates dynamic messages based on performance thresholds to boost user engagement.
 */
@Component({
  selector: 'app-hero-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hero-section.component.html',
  styleUrls: ['./hero-section.component.css']
})
export class HeroSectionComponent implements OnInit {
  private userService = inject(UserService);

  /** Reactive signal holding the user's profile information. */
  userProfile = signal<UserProfile | null>(null);

  /** DERIVED METRICS (Computed Signals) */
  /** Current total points accumulated by the user. */
  readonly points = computed(() => this.userProfile()?.points ?? 0);
  /** Current display name. */
  readonly username = computed(() => this.userProfile()?.username ?? 'Challenger');
  /** * Level Calculation Logic:
   * Level is calculated by dividing total points by a constant factor (100 XP per level).
   * Level 1 starts at 0 points.
   */
  readonly level = computed(() => Math.floor(this.points() / 100) + 1);
  /** Remaining XP within the current level tier. */
  readonly xpInCurrentLevel = computed(() => this.points() % 100);
  /** XP required to reach the subsequent level. */
  readonly xpNeededForNextLevel = computed(() => 100 - this.xpInCurrentLevel());
  /** Percentage value (0-100) used to drive the CSS width of the progress bar. */
  readonly progressPercentage = computed(() => this.xpInCurrentLevel());

  /** * Dynamic Social Proof Message:
   * Compares user points against static milestones to provide competitive context.
   */
  readonly socialProofMessage = computed(() => {
    const pts = this.points();
    if (pts > 500) return "You're in the top 5% of achievers this week!";
    if (pts > 200) return "You're outperforming 70% of new challengers!";
    return "Top 10% potential! Keep pushing!";
  });

  ngOnInit(): void {
    /** Fetch initial profile data from the UserService. */
    this.userService.getProfile().subscribe({
      next: (profile) => this.userProfile.set(profile),
      error: (err) => console.error('Could not fetch profile', err)
    });
  }
}
