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
import {NotificationService, NotificationDTO} from '../../services/notification.service';
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
  imports: [CommonModule, LucideAngularModule, RouterModule],
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
  readonly icons = {User, FileText, Users, Settings, LogOut, ChevronDown, PlusCircle, Menu, Bell, Check};

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
        this.notifications.set(data);
      });
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
