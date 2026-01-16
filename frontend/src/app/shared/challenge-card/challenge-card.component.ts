import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Challenge } from '../../models/challenge.model';
import {LucideAngularModule, CheckCircle, RotateCcw, Play, LucideIconProvider, LUCIDE_ICONS} from 'lucide-angular';

/**
 * A highly reusable presentational component representing a single quest.
 * It features a reactive event system and supports multiple layout modes
 * (Standard vs. Compact).
 * * * Key Technical Aspects:
 * - Event Delegation: Uses a robust @Output system to bubble interaction
 * events (start, restart, edit, delete) to parent orchestrators.
 * - Contextual Rendering: Adapts its visual state based on the 'status'
 * and 'compact' inputs to fit various dashboard sections.
 * - Custom Icon Injection: Configures a local LucideIconProvider to specifically
 * handle the 'RotateCcw' icon requirement.
 */
@Component({
  selector: 'app-challenge-card',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  providers: [
    { provide: LUCIDE_ICONS, useValue: new LucideIconProvider({ RotateCcw }) }
  ],
  templateUrl: './challenge-card.component.html',
  styleUrls: ['./challenge-card.component.css']
})
export class ChallengeCardComponent {
  // --- STATE & INPUTS ---

  /** The core Challenge object containing title, points, and difficulty. */
  @Input({ required: true }) challenge!: Challenge;

  /** * Tracks the user's relationship with the quest (e.g., 'ACCEPTED', 'COMPLETED').
   * Used for conditional rendering of action buttons.
   */
  @Input() status: string | undefined;

  /** Dynamic Tailwind background class (e.g., 'bg-indigo-500') provided by parent utility. */
  @Input() categoryClass: string = 'bg-gray-500';

  /** * Boolean flag to switch to a simplified layout.
   * Ideal for sidebar widgets or "Quick View" lists.
   */
  @Input() compact: boolean = false;

  // --- INTERACTION PIPELINE (OUTPUTS) ---

  /** Triggers when a user accepts a new challenge from the catalog. */
  @Output() start = new EventEmitter<Challenge>();

  /** Triggers when a user chooses to replay a completed challenge. */
  @Output() restart = new EventEmitter<Challenge>();

  /** Opens the 'Assign to Friend' social modal. */
  @Output() assign = new EventEmitter<Challenge>();

  /** Administrative event to trigger the modification form. */
  @Output() edit = new EventEmitter<Challenge>();

  /** * Administrative event to remove the quest.
   * Passes the original MouseEvent to handle click propagation issues.
   */
  @Output() delete = new EventEmitter<{event: MouseEvent, challenge: Challenge}>();

  /** Resumes progress for an 'IN_PROGRESS' challenge. */
  @Output() continue = new EventEmitter<Challenge>();

  /** Internal icon mapping for Lucide-Angular components. */
  readonly icons = { CheckCircle, RotateCcw, Play };
}
