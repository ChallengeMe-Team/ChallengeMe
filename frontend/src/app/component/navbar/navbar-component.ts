import {
  Component,
  ChangeDetectionStrategy,
  inject,
  ElementRef,
  HostListener,
  EventEmitter,
  Output,
  OnInit,
  signal
} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {ChallengeService} from '../../services/challenge.service';
import {NotificationService} from '../../services/notification.service';
import { NotificationDTO } from '../../models/notification.model';
import { TimeAgoPipe } from '../../pipes/time-ago.pipe';
import { Swords, UserPlus, Info } from 'lucide-angular';
import {
  LucideAngularModule,
  User,
  FileText,
  Users,
  Settings,
  LogOut,
  ChevronDown,
  PlusCircle,
  Menu,
  Bell,
  Check
} from 'lucide-angular';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, RouterModule, TimeAgoPipe],
  templateUrl: './navbar-component.html',
  styleUrls: ['./navbar-component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent implements OnInit {

  private authService = inject(AuthService);
  public challengeService = inject(ChallengeService);
  private notificationService = inject(NotificationService);
  private elementRef = inject(ElementRef);
  private router = inject(Router);

  @Output() createChallengeRequest = new EventEmitter<void>();
  @Output() toastRequest = new EventEmitter<{ message: string, type: 'success' | 'error' }>();

  // Icons
  readonly icons = {User, FileText, Users, Settings, LogOut, ChevronDown, PlusCircle, Menu, Bell, Check, Swords, UserPlus, Info};

  isDropdownOpen = false;
  isMenuOpen = false;

  // Notifications State
  isNotificationsOpen = false;
  notifications = signal<NotificationDTO[]>([]);
  unreadCount = this.notificationService.unreadCount;

  // User data
  user = this.authService.currentUser;

  get username(): string { return this.user()?.username || 'Guest'; }
  get userPoints(): number { return this.user()?.points || 0; }
  get userLevel(): number { return Math.floor(this.userPoints / 100) + 1; }
  get userInitials(): string { return this.username.substring(0, 2).toUpperCase(); }

  navLinks = [
    {label: 'Home', path: '/'},
    {label: 'Challenges', path: '/challenges'},
    {label: 'Leaderboard', path: '/leaderboard'}
  ];

  ngOnInit() {
    this.loadNotifications();
  }

  loadNotifications() {
    const u = this.user();
    if (u && u.id) {
      this.notificationService.getUserNotifications(u.id).subscribe(data => {
        // SORTARE: Cele mai noi primele
        const sorted = data.sort((a, b) => {
          // Helper pentru a gestiona atât Array (Java) cât și String (ISO)
          const getTime = (t: any) => {
            if (Array.isArray(t)) {
              // [An, Luna(1-12), Zi, Ora, Min, Sec] -> Luna in JS e 0-11, deci t[1]-1
              return new Date(t[0], t[1] - 1, t[2], t[3] || 0, t[4] || 0).getTime();
            }
            return new Date(t).getTime();
          };

          return getTime(b.timestamp) - getTime(a.timestamp);
        });

        // Păstrează doar primele 10 elemente
        const limitedList = sorted.slice(0, 10);

        this.notifications.set(limitedList); // Setează lista limitată
      });
    }
  }

  getIconForType(type: string): any {
    console.log('Notification Type received:', type);

    // Asigură-te că string-ul de aici se potrivește cu cel din consolă (Java Enum)
    switch (type) {
      case 'CHALLENGE':       // Probabil așa vine din Java
      case 'CHALLENGE_REQ':   // Varianta veche
        return this.icons.Swords;

      case 'FRIEND':
      case 'FRIEND_REQ':
        return this.icons.UserPlus;

      default:
        return this.icons.Info;
    }
  }

  // Actions
  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
    this.isNotificationsOpen = false;
  }

  toggleNotifications() {
    this.isNotificationsOpen = !this.isNotificationsOpen;
    this.isDropdownOpen = false;
  }

  onNotificationClick(notif: NotificationDTO) {
    // 1. Mark as read
    if (!notif.isRead) {
      this.notificationService.markAsRead(notif.id).subscribe(() => {
        this.notifications.update(list =>
          list.map(n => n.id === notif.id ? { ...n, isRead: true } : n)
        );
      });
    }

    this.isNotificationsOpen = false;

    // 2. Redirect logic
    if (notif.type === 'CHALLENGE_REQ' || notif.message.toLowerCase().includes('challenge')) {
      this.router.navigate(['/my-challenges'], { queryParams: { tab: 'inbox' } });
    }

    if (notif.type === 'CHALLENGE' || notif.message.includes('provocat')) {
      // Navigăm către pagina My Challenges și setăm tab-ul activ pe 'inbox'
      this.router.navigate(['/my-challenges'], { queryParams: { tab: 'inbox' } });
    }
  }

  onCreateChallenge() {
    this.challengeService.isCreateModalOpen.set(true);
    this.isMenuOpen = false;
  }

  onLogout() {
    this.authService.logout();
    this.isDropdownOpen = false;
    this.toastRequest.emit({ message: 'You have been logged out successfully.', type: 'success' });
    this.router.navigate(['/auth']);
  }

  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isDropdownOpen = false;
      this.isNotificationsOpen = false;
    }
  }
}
