import { Component, ChangeDetectionStrategy, Input, Output, EventEmitter, inject, ElementRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { LucideAngularModule, User, FileText, Users, Settings, LogOut, ChevronDown, PlusCircle, Menu } from 'lucide-angular';

// Define the Page type here for safety/consistency
type Page = 'home' | 'challenges' | 'leaderboard' | 'create' | 'auth' | 'profile' | 'my-challenges' | 'friends' | 'settings';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './navbar-component.html',
  styleUrls: ['./navbar-component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {
  @Input() currentPage: Page = 'home';
  @Output() navigateRequest = new EventEmitter<Page>();
  @Output() createChallengeRequest = new EventEmitter<void>();

  private authService = inject(AuthService);
  private elementRef = inject(ElementRef);

  // Icons
  readonly icons = { User, FileText, Users, Settings, LogOut, ChevronDown, PlusCircle, Menu };

  isDropdownOpen = false;
  isMenuOpen = false;

  // User data from AuthService
  user = this.authService.currentUser;

  // Helpers for HTML template
  get username(): string { return this.user()?.username || 'Guest'; }
  get userPoints(): number { return this.user()?.points || 0; }

  // Calculate level based on points (e.g., 100 xp = 1 level)
  get userLevel(): number {
    return Math.floor(this.userPoints / 100) + 1;
  }

  get userInitials(): string { return this.username.substring(0, 2).toUpperCase(); }

  navLinks: { label: string; page: Page }[] = [
    { label: 'Home', page: 'home' },
    { label: 'Challenges', page: 'challenges' },
    { label: 'Leaderboard', page: 'leaderboard' }
  ];

  // Actions
  toggleDropdown() { this.isDropdownOpen = !this.isDropdownOpen; }

  onCreateChallenge() { this.createChallengeRequest.emit(); }

  onNavigate(page: string) {
    this.navigateRequest.emit(page as Page);
    this.isDropdownOpen = false;
    this.isMenuOpen = false;
  }

  onLogout() {
    this.authService.logout();
    this.isDropdownOpen = false;
    this.onNavigate('auth');
  }

  // CLICK OUTSIDE
  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isDropdownOpen = false;
    }
  }
}
