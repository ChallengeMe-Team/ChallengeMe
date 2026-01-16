import {
  Component,
  ChangeDetectionStrategy,
  inject,
  ElementRef,
  HostListener,
  EventEmitter,
  Output,
  OnInit,
  ChangeDetectorRef, effect
} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {NotificationService} from '../../services/notification.service';

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
  Check,
  Swords,
  UserPlus,
  Info,
  Trophy
} from 'lucide-angular';
import {NotificationDropdownComponent} from './notifications/notifications-dropdown.component';

/**
 * Core navigation component providing global access to features and user state.
 * It manages authentication sessions, real-time notification badges, and
 * profile progression metrics (Level/XP).
 * * Key Architectural Implementation:
 * - Angular Signals Integration: Consumes AuthService and NotificationService signals
 * for efficient, reactive UI updates.
 * - Change Detection Optimization: Combines 'Default' strategy with manual 'ChangeDetectorRef'
 * triggers and 'effect()' to ensure notification counts are perfectly synchronized.
 * - Interaction Logic: Implements @HostListener to handle click-outside events for dropdown closure.
 */
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, RouterModule, NotificationDropdownComponent],
  templateUrl: './navbar-component.html',
  styleUrls: ['./navbar-component.css'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class NavbarComponent implements OnInit {

  private authService = inject(AuthService);
  private notificationService = inject(NotificationService);
  private elementRef = inject(ElementRef);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  /** * Reactive effect to force UI refresh on notification count changes.
   * Ensures the 'unread-pulse' badge updates instantly.
   */
  constructor() {
    effect(() => {
      this.notificationService.unreadCount();
      this.cdr.detectChanges();
    });
  }

  @Output() toastRequest = new EventEmitter<{ message: string, type: 'success' | 'error' }>();

  /** Global icons set for navigation and dropdown items. */
  readonly icons = {User, FileText, Users, Settings, LogOut, ChevronDown, PlusCircle, Menu, Bell, Check, Swords, UserPlus, Info, Trophy};

  // State
  isDropdownOpen = false;
  isMenuOpen = false;
  isNotificationsOpen = false;

  // --- STATE SIGNALS ---
  /** Reference to the currently authenticated user. */
  unreadCount = this.notificationService.unreadCount;
  user = this.authService.currentUser;

  // --- COMPUTED PROPERTIES ---
  get username(): string { return this.user()?.username || 'Guest'; }
  get userPoints(): number { return this.user()?.points || 0; }
  /** Formats the user's level based on total XP (100 points per level progression). */
  get userLevel(): number { return Math.floor(this.userPoints / 100) + 1; }
  /** Extracts initials for the avatar placeholder if no image is present. */
  get userInitials(): string { return this.username.substring(0, 2).toUpperCase(); }

  // Navigation Links
  navLinks = [
    {label: 'Home', path: '/'},
    {label: 'Challenges', path: '/challenges'},
    {label: 'Leaderboard', path: '/leaderboard'},
    {label: 'Badges', path: '/badges'}
  ];

  ngOnInit(): void {
  }

  // --- ACTIONS ---
  /** Toggles the notification dropdown and ensures the profile menu is closed. */
  toggleNotifications() {
    this.isNotificationsOpen = !this.isNotificationsOpen;
    if (this.isNotificationsOpen) this.isDropdownOpen = false;
    this.cdr.detectChanges();
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
    if (this.isDropdownOpen) {
      this.isNotificationsOpen = false;
    }
  }

  /** Terminate session logic: stops polling and redirects to Auth. */
  onLogout() {
    this.notificationService.stopPolling();
    this.authService.logout();
    this.isDropdownOpen = false;
    this.toastRequest.emit({ message: 'You have been logged out successfully.', type: 'success' });
    this.router.navigate(['/auth']);
  }

  onNotificationClick(notif: any) {
    console.log('Notification clicked:', notif);
    this.isNotificationsOpen = false;
  }

  getIconForType(type: string): any {
    switch (type) {
      case 'CHALLENGE_VICTORY': return Trophy;
      case 'CHALLENGE_INVITE': return FileText;
      case 'FRIEND_REQUEST': return Users;
      default: return Info;
    }
  }

  /** Listens for global clicks to automatically close open menus (UX improvement). */
  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isDropdownOpen = false;
      this.isNotificationsOpen = false;
    }
  }

  onStateRefresh() {
    this.cdr.detectChanges();
  }
}
