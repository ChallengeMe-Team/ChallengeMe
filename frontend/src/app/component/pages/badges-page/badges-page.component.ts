import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { BadgeService } from '../../../services/badge.service';
import { AuthService } from '../../../services/auth.service';
import { LucideAngularModule, Zap } from 'lucide-angular';
import { BadgeDisplay } from '../../../models/badge.model';

/**
 * Component responsible for displaying the global achievement catalog.
 * It synchronizes the full list of available badges with the specific set
 * earned by the current user to provide a "Collection" style interface.
 * * * Key Logic:
 * - Reactive Data Fetching: Uses RxJS 'forkJoin' to execute parallel API calls
 * to both the global catalog and user-specific achievements.
 * - State Management: Utilizes Angular Signals for the loading state to ensure
 * high-performance UI reactivity.
 * - Security Integration: Consumes 'AuthService' to dynamically retrieve the
 * logged-in user's identity for personalized badge filtering.
 */
@Component({
  selector: 'app-badges-page',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './badges-page.component.html'
})
export class BadgesPageComponent implements OnInit {
  /** local list used for display, combining badge data with unlock status. */
  badgeList: BadgeDisplay[] = [];

  /** Signal tracking the asynchronous data loading process. */
  isLoading = signal(true);
  readonly icons = { Zap };

  constructor(
    private badgeService: BadgeService,
    private authService: AuthService
  ) {}

  /**
   * Orchestrates the multi-source data initialization.
   * 1. Retrieves current username from session.
   * 2. Fetches global and user-owned badges simultaneously.
   * 3. Maps the data into a unified display model by checking ID existence.
   */
  ngOnInit(): void {
    const username = this.authService.getUsername();

    if (!username) {
      console.error('User not logged in');
      this.isLoading.set(false);
      return;
    }

    forkJoin({
      allBadges: this.badgeService.getAll(),
      userBadges: this.badgeService.getUserBadges(username)
    }).subscribe({
      next: ({ allBadges, userBadges }) => {
        this.badgeList = allBadges.map(badge => {

          const isOwned = userBadges.some(owned => owned.id === badge.id);

          return {
            badge: badge,
            isUnlocked: isOwned
          };
        });
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error loading badges', err);
        this.isLoading.set(false);
      }
    });
  }
}
