import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * A presentational component used for global system notifications.
 * It features self-managed visibility logic and dynamic semantic styling.
 *
 * * * Key Technical Aspects:
 * - Auto-dismissal Logic: Implements a temporal lifecycle using 'setTimeout'
 * to automatically clear the notification from the DOM after 3 seconds.
 * - Semantic Visual Mapping: Uses getters to derive icon types and Tailwind
 * gradients based on the success/error state provided by the parent.
 * - Standalone Architecture: Designed as a portable unit with its own
 * styles and template, minimizing dependencies.
 */
@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-component.html',
  styleUrls: ['./toast-component.css']
})
export class ToastComponent implements OnInit {
  /** The text string to be displayed within the notification bubble. */
  @Input() message = '';

  /** *
   * Sets the visual theme and icon for the message.
   * Defaults to 'success'. */
  @Input() type: 'success' | 'error' = 'success';

  /** Internal visibility flag synchronized with the CSS fadeInOut animation. */
  visible = false;

  /**
   * Initializes the display sequence. Sets visibility to 'true' immediately
   * upon component creation and schedules a teardown task to hide the
   * component after 3000ms.
   */
  ngOnInit() {
    this.visible = true;
    setTimeout(() => this.visible = false, 3000); // dispare automat după 3 secunde
  }

  /**
   * Returns a semantic emoji icon to provide immediate visual context
   * (Confirmation vs. Alert).
   */
  get icon() {
    return this.type === 'success' ? '✅' : '❌';
  }

  /**
   * Maps the notification 'type' to specific Tailwind CSS gradient classes.
   * - Success: Indigo-to-Purple (Brand colors).
   * - Error: Red-to-Pink (Urgency colors).
   */
  get gradient() {
    return this.type === 'success'
      ? 'from-indigo-500 to-purple-500'
      : 'from-red-600 to-pink-500';
  }
}
