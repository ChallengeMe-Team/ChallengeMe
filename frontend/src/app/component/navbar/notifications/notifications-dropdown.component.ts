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

@Component({
  selector: 'app-notification-dropdown',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './notifications-dropdown.component.html',
  styleUrls: ['./notifications-dropdown.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class NotificationDropdownComponent implements OnInit {
  private notificationService = inject(NotificationService);
  @Output() stateChanged = new EventEmitter<void>();
  private cdr = inject(ChangeDetectorRef);

  notifications = this.notificationService.notifications;

  ngOnInit() {
  }

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
      case 'SYSTEM': return UserPlus; // Folosit pentru Friend Requests
      case 'CHALLENGE_COMPLETED': return PartyPopper;
      case 'CHALLENGE': return Handshake; // Pentru acceptat/refuzat
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

  markAllAsRead() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (!user.id) return;

    this.notificationService.markAllAsRead(user.id).subscribe({
      next: () => {
        this.stateChanged.emit(); // Trimite semnalul la Navbar
        console.log('UI: All read signal sent');
      }
    });
  }

  formatMessage(message: string): string {
    if (!message) return '';
    return message
      .replace(/declined/gi, '<b class="text-red-500 font-extrabold uppercase">declined</b>')
      .replace(/accepted/gi, '<b class="text-emerald-400 font-extrabold uppercase">accepted</b>')
      .replace(/congratulations/gi, '<b class="text-amber-400 font-black italic">Congratulations</b>')
      .replace(/crushed/gi, '<b class="text-orange-400 italic">crushed</b>')
      .replace(/victory/gi, '<b class="text-yellow-500 uppercase tracking-widest">victory</b>')
      .replace(/friend/gi, '<b class="text-blue-400">friend</b>')
      .replace(/game on/gi, '<b class="text-purple-400 uppercase">Game On!</b>');
  }
}
