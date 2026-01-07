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
import {AuthService} from '../../services/auth.service'; // Verifică calea!
import {ChallengeService} from '../../services/challenge.service'; // Verifică calea!
import {NotificationService} from '../../services/notification.service'; // Verifică calea!
import { NotificationDTO } from '../../models/notification.model';
// Am scos TimeAgoPipe pentru a scăpa de warning

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

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, RouterModule], // Fără TimeAgoPipe
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
  readonly icons = {User, FileText, Users, Settings, LogOut, ChevronDown, PlusCircle, Menu, Bell, Check, Swords, UserPlus, Info, Trophy};

  // State
  isDropdownOpen = false;
  isMenuOpen = false;
  isNotificationsOpen = false;

  // Data
  notifications = signal<NotificationDTO[]>([]);
  unreadCount = this.notificationService.unreadCount;
  user = this.authService.currentUser;

  // Computed
  get username(): string { return this.user()?.username || 'Guest'; }
  get userPoints(): number { return this.user()?.points || 0; }
  get userLevel(): number { return Math.floor(this.userPoints / 100) + 1; }
  get userInitials(): string { return this.username.substring(0, 2).toUpperCase(); }

  // Navigation Links
  navLinks = [
    {label: 'Home', path: '/'},
    {label: 'Challenges', path: '/challenges'},
    {label: 'Leaderboard', path: '/leaderboard'},
    {label: 'Badges', path: '/badges'}
  ]; // <--- AICI era greșeala (aveai '}' în loc de '];')

  ngOnInit(): void {
    // Dacă ai nevoie de inițializare, o pui aici
  }

  // --- ACTIONS ---

  toggleNotifications() {
    this.isNotificationsOpen = !this.isNotificationsOpen;
    if (this.isNotificationsOpen) {
      this.isDropdownOpen = false;
    }
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
    if (this.isDropdownOpen) {
      this.isNotificationsOpen = false;
    }
  }

  onCreateChallenge() {
    // Dacă folosești un modal global sau navigare:
    this.challengeService.isCreateModalOpen.set(true);
    this.isMenuOpen = false;
  }

  onLogout() {
    this.notificationService.stopPolling();
    this.authService.logout();
    this.isDropdownOpen = false;
    this.toastRequest.emit({ message: 'You have been logged out successfully.', type: 'success' });
    this.router.navigate(['/auth']);
  }

  onNotificationClick(notif: any) {
    console.log('Notification clicked:', notif);
    // this.notificationService.markAsRead(notif.id);
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

  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isDropdownOpen = false;
      this.isNotificationsOpen = false;
    }
  }
}
