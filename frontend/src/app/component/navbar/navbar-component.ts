import { Component, ChangeDetectionStrategy, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export type Page = 'home' | 'challenges' | 'leaderboard' | 'create' | 'auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar-component.html',
  styleUrls: ['./navbar-component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {
  @Input() currentPage: Page = 'home';
  @Output() navigateRequest = new EventEmitter<Page>();
  @Output() createChallengeRequest = new EventEmitter<void>();

  isMenuOpen = false;

  navLinks: { label: string; page: Page }[] = [
    { label: 'Home', page: 'home' },
    { label: 'Challenges', page: 'challenges' },
    { label: 'Leaderboard', page: 'leaderboard' }
  ];

  onNavigate(page: Page) {
    this.navigateRequest.emit(page);
    this.isMenuOpen = false;
  }

  onCreateChallenge() {
    this.createChallengeRequest.emit();
  }
}
