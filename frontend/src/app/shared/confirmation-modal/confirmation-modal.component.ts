import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, AlertTriangle, Info, X } from 'lucide-angular';

/**
 * A high-order utility component used to intercept user actions that require
 * explicit consent (e.g., deleting a challenge or changing sensitive settings).
 *
 * * * Key Technical Aspects:
 * - Semantic Theming: Supports three distinct visual modes ('danger', 'info', 'warning')
 * to align the modal's aesthetic with the severity of the action.
 * - Event Delegation: Uses a clean @Output pipeline to notify parent components
 * of user decisions without managing the actual business logic internally.
 * - Interactive Iconography: Integrates Lucide-Angular to provide visual context
 * (Warning vs. Info) at a glance.
 */
@Component({
  selector: 'app-confirmation-modal',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './confirmation-modal.component.html',
  styles: []
})
export class ConfirmationModalComponent {
  // --- CONFIGURATION INPUTS ---

  /** Controls the visibility state via the parent component. */
  @Input() isVisible = false;

  /** The primary heading displayed at the top of the modal. */
  @Input() title = 'Are you sure?';

  /** The detailed explanation text clarifying the consequences of the action. */
  @Input() message = '';

  /** Label for the primary action button. */
  @Input() confirmText = 'Confirm';

  /** Label for the dismissal button. */
  @Input() cancelText = 'Cancel';

  /** * Logic: Aesthetic Discriminator
   * Determines the color palette of the primary button (e.g., Red for 'danger', Blue for 'info').
   *
   */
  @Input() type: 'danger' | 'info' | 'warning' = 'info'; // Schimbă culoarea butonului


  // --- INTERACTION PIPELINE (OUTPUTS) ---

  /** Emitted when the user dismisses the modal or clicks 'Cancel'. */
  @Output() close = new EventEmitter<void>();

  /** Emitted when the user validates the action via the primary button. */
  @Output() confirm = new EventEmitter<void>();

  /** Internal icon mapping for Lucide-Angular components. */
  readonly icons = { AlertTriangle, Info, X };

  /** Triggered by the cancel button or backdrop click. */
  onCancel() {
    this.close.emit();
  }

  /** Triggered by the primary action button. */
  onConfirm() {
    this.confirm.emit();
  }
}
