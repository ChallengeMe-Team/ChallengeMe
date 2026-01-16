import {
  Component,
  inject,
  signal,
  OnInit,
  ViewEncapsulation,
  ChangeDetectorRef,
  EventEmitter,
  Output
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../services/notification.service';
import { NotificationDTO } from '../../../models/notification.model';
import { LucideAngularModule} from 'lucide-angular';
import { Medal, UserPlus, PartyPopper, Handshake, BellRing } from 'lucide-angular';
import {AuthService} from '../../../services/auth.service';
import { Bell, BellOff } from 'lucide-angular';
import { TimeAgoPipe } from '../../../pipes/time-ago.pipe';

/**
 * UI Component that manages the display and interaction logic for user notifications.
 * It provides a real-time view of system alerts, friend requests, and achievements.
 * * * Key Technical Features:
 * - Dynamic Message Formatting: Uses Regex to parse plain-text messages into rich HTML with semantic coloring.
 * - Reactive State Management: Directly integrates with NotificationService signals for instantaneous UI updates.
 * - Encapsulation Bypass: Uses 'ViewEncapsulation.None' to apply custom styles to dynamically injected HTML tags (via [innerHTML]).
 * - Contextual Iconography: Maps notification types (BADGE, SYSTEM, CHALLENGE) to specific Lucide icons and color palettes.
 */
@Component({
  selector: 'app-notification-dropdown',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, TimeAgoPipe],
  templateUrl: './notifications-dropdown.component.html',
  styleUrls: ['./notifications-dropdown.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class NotificationDropdownComponent{
  private notificationService = inject(NotificationService);
  /** Event emitted when a significant state change occurs (e.g., mass marking as read). */
  @Output() stateChanged = new EventEmitter<void>();
  private authService = inject(AuthService);

  /** Signal-based list of notifications synchronized with the global store. */
  notifications = this.notificationService.notifications;

  readonly icons = { Bell, BellOff };

  /** * Handles individual notification interactions.
   * Marks unread notifications as read via the API and updates the local signal state.
   */
  onNotificationClick(notif: NotificationDTO) {
    if (!notif.isRead) {
      this.notificationService.markAsRead(notif.id).subscribe({
        next: () => {
          this.notificationService.notifications.update(list =>
            list.map(n => n.id === notif.id ? { ...n, isRead: true } : n)
          );
        }
      });
    }
  }

  getIconForType(type: string): any {
    switch (type) {
      case 'BADGE': return Medal;
      case 'SYSTEM': return UserPlus;
      case 'CHALLENGE_COMPLETED': return PartyPopper;
      case 'CHALLENGE': return Handshake;
      default: return BellRing;
    }
  }

  getTypeColor(type: string): string {
    switch (type) {
      case 'BADGE': return 'text-amber-400';
      case 'SYSTEM': return 'text-blue-400';
      case 'CHALLENGE_COMPLETED': return 'text-emerald-400';
      case 'CHALLENGE': return 'text-purple-400';
      default: return 'text-gray-400';
    }
  }

  /**
   * Bulk action to mark all notifications for the current user as read.
   * Utilizes the AuthService to ensure proper user identification.
   */
  markAllAsRead() {

    const currentUser = this.authService.currentUser();
    const userId = currentUser?.id;

    if (!userId) {
      return;
    }

    this.notificationService.markAllAsRead(userId).subscribe({
      next: () => {
        this.stateChanged.emit();
      },
      error: (err) => {
        console.error('API ERROR: Serverul a respins cererea de mark-all-read:', err);
      }
    });
  }

  /**
   * Rich-text formatter that injects Tailwind CSS classes into specific keywords.
   * Transforms plain strings into visually highlighted interactive messages.
   * @param message Raw message string from the backend.
   */
  formatMessage(message: string): string {
    if (!message) return '';

    let formatted = message;

    // Keyword highlighting logic using Regex patterns
    formatted = formatted
      .replace(/declined/gi, '<b class="text-red-500 font-extrabold uppercase">declined</b>')
      .replace(/accepted/gi, '<b class="text-emerald-400 font-extrabold uppercase">accepted</b>')
      .replace(/congratulations/gi, '<b class="text-amber-400 font-black italic">Congratulations</b>')
      .replace(/crushed/gi, '<b class="text-orange-400 italic">crushed</b>')
      .replace(/victory/gi, '<b class="text-yellow-500 uppercase tracking-widest">victory</b>')
      .replace(/friend/gi, '<b class="text-blue-400">friend</b>')
      .replace(/game on/gi, '<b class="text-purple-400 uppercase">Game On!</b>')
      .replace(/challenged you to/gi, '<b class="text-indigo-400">challenged you to</b>');

    formatted = formatted.replace(/^(\w+)/, '<b class="text-purple-400 font-black hover:text-purple-300 transition-colors">$1</b>');

    return formatted;
  }
}
