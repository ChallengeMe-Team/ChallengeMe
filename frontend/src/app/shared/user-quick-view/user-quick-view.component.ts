import { Component, Input, Output, EventEmitter, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserProfile } from '../../models/user.model';
import { LucideAngularModule } from 'lucide-angular';

/**
 * A highly reusable "Quick Peek" modal component. It functions as a
 * specialized data visualizer for UserProfile objects.
 * * * Key Technical Aspects:
 * - Encapsulation Strategy: Uses 'ViewEncapsulation.None' to allow modal
 * styles to be shared or overridden by global animation wrappers if needed.
 * - State Control: Orchestrated by an 'isOpen' boolean signal from the parent,
 * ensuring it only consumes rendering resources when active.
 * - Asynchronous Resilience: Includes an 'isLoading' state to manage the
 * transition between the "Requesting Data" and "Displaying Profile" phases.
 */
@Component({
  selector: 'app-user-quick-view',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './user-quick-view.component.html',
  styleUrls: ['./user-quick-view.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class UserQuickViewComponent {
  /**
   * Logical toggle for modal visibility. Marked as 'required' to enforce
   * strict orchestration by the parent component.
   */
  @Input({ required: true }) isOpen = false;

  /**
   * Visual flag used to render skeleton loaders or spinners while the
   * UserService fetches the specific user data.
   */
  @Input() isLoading = false;

  /**
   * The data payload. If null, the template is designed to show a safe
   * empty state or loading indicator.
   */
  @Input() profile: UserProfile | null = null;

  /**
   * Event emitter used to signal the parent to destroy or hide this
   * component instance (e.g., when clicking the 'X' or backdrop).
   */
  @Output() close = new EventEmitter<void>();

  /**
   * Simple delegation method to notify the parent orchestrator that the
   * user has requested to dismiss the view.
   */
  closeModal() {
    this.close.emit();
  }
}
