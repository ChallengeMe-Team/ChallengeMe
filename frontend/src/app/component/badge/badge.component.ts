import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, Trophy, Star, Medal, Crown } from 'lucide-angular';

/**
 * Presentational component for displaying individual user achievements (badges).
 * It dynamically maps badge names to specific visual icons and handles
 * the logic for the hover-based tooltip information.
 * * @usage Used primarily in Profile and Leaderboard views to showcase user progression.
 */
@Component({
  selector: 'app-badge',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './badge.component.html',
  styleUrls: ['./badge.component.css']
})
export class BadgeComponent {
  /** The display name of the badge (e.g., 'First Step', 'Marathoner'). */
  @Input() name: string = '';

  /** Brief description explaining the achievement. */
  @Input() description: string = '';

  /** Specific technical requirements met to unlock the badge. */
  @Input() criteria: string = '';

  /** Reference set of Lucide icons used for consistent gamification styling. */
  readonly icons = { Trophy, Star, Medal, Crown };

  /**
   * Logic-to-Icon Mapping:
   * Dynamically selects a Lucide icon based on keywords found in the badge name.
   * This ensures that even dynamically created badges have a fallback visual representation.
   * * @returns A Lucide icon reference.
   */
  getBadgeIcon(): any {
    const n = this.name.toLowerCase();
    if (n.includes('step')) return this.icons.Trophy;
    if (n.includes('xp') || n.includes('point')) return this.icons.Crown;
    if (n.includes('five')) return this.icons.Star;
    return this.icons.Medal;
  }
}
