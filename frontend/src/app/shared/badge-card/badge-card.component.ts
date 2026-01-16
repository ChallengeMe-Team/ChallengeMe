import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Badge } from '../../models/badge.model';

/**
 * A presentational (dumb) component responsible for displaying a single
 * achievement badge. It relies on parent-provided inputs to determine
 * its visual state.
 * * * Key Technical Aspects:
 * - Decoupled UI Logic: Separates the badge metadata (definition) from
 * the user's progress (isUnlocked state).
 * - Dynamic Tooltips: Uses a calculated getter to provide context-aware
 * descriptions based on badge criteria.
 * - Pure Component: Optimized for performance by avoiding internal service
 * injections and relying on @Input bindings.
 */
@Component({
  selector: 'app-badge-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './badge-card.component.html',
  styleUrls: ['./badge-card.component.css']
})
export class BadgeCardComponent {
  /**
   * The static metadata of the badge, including its name, icon URL,
   * and description.
   */
  @Input() badge!: Badge;

  /**
   * State flag determining the CSS class application (unlocked vs. locked).
   * Impacts saturation and opacity filters in the view.
   */
  @Input() isUnlocked: boolean = false;

  /**
   * Computes a human-readable string for the 'title' attribute.
   * Logic: Appends the badge criteria in parentheses if it exists,
   * providing the user with instructions on how to unlock the badge
   * upon hovering.
   * @returns A formatted description string.
   */
  get tooltip(): string {
    if (!this.badge) return '';
    const criteriaText = this.badge.criteria ? ` (${this.badge.criteria})` : '';
    return `${this.badge.description}${criteriaText}`;
  }

}
