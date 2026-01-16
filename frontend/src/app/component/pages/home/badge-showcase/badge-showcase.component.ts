import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BadgeService } from '../../../../services/badge.service';
import { AuthService } from '../../../../services/auth.service';
import { BadgeCardComponent } from '../../../../shared/badge-card/badge-card.component';
import { RouterModule } from '@angular/router';
import { Badge } from '../../../../models/badge.model';

/**
 * Purpose: Provides a dashboard overview of the user's latest achievements.
 * It fetches the user's collection, sorts them by award date, and displays
 * the top 3 most recent badges.
 * * * Key Technical Aspects:
 * - Reactive Data Flow: Utilizes 'signal' to manage the UI state of the badge list.
 * - Data Normalization: Transforms backend Achievement DTOs into the local 'Badge'
 * model required by the BadgeCardComponent.
 * - Visual Feedback: Implements a custom "Fade In Up" CSS animation for card entry.
 */
@Component({
  selector: 'app-badge-showcase',
  standalone: true,
  imports: [CommonModule, BadgeCardComponent, RouterModule],
  template: `
    <div class="bg-gradient-to-br from-[#1a1627] to-[#2d2445] p-6 rounded-2xl border border-white/5 shadow-xl h-full flex flex-col">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-xl font-bold text-white flex items-center gap-2">
          <span class="text-yellow-400">🏆</span> Latest Glory
        </h2>
        <a routerLink="/badges"
           class="text-xs font-medium text-purple-400 hover:text-purple-300 transition-colors uppercase tracking-wider">
          Collection
        </a>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 flex-1" *ngIf="recentBadges().length > 0; else noBadges">
        <div *ngFor="let item of recentBadges()" class="animate-fade-in-up">
          <app-badge-card
            [badge]="item.mappedBadge"
            [isUnlocked]="true">
          </app-badge-card>
          <div class="text-center mt-2">
             <span class="text-[10px] text-gray-400 uppercase tracking-wide">
               {{ item.dateAwarded | date:'mediumDate' }}
             </span>
          </div>
        </div>
      </div>

      <ng-template #noBadges>
        <div class="flex-1 flex flex-col items-center justify-center text-gray-500 bg-black/20 rounded-xl border border-dashed border-gray-700/50 min-h-[150px]">
          <span class="text-3xl mb-2 opacity-50">🔒</span>
          <p class="text-sm">No badges earned yet.</p>
        </div>
      </ng-template>
    </div>
  `,
  styles: [`
    .animate-fade-in-up { animation: fadeInUp 0.5s ease-out forwards; }
    @keyframes fadeInUp { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
  `]
})
export class BadgeShowcaseComponent implements OnInit {
  private badgeService = inject(BadgeService);
  private authService = inject(AuthService);

  /** Signal holding the processed list of recent achievements. */
  recentBadges = signal<any[]>([]);

  /**
   * Lifecycle Hook: Component Initialization.
   * 1. Retrieves user context (username) from AuthService.
   * 2. Fetches user badges from BadgeService.
   * 3. Sorts by date (newest first) and slices to show only the top 3.
   */
  ngOnInit() {
    const username = this.authService.getUsername();
    if (!username) return;

    this.badgeService.getUserBadges(username).subscribe({
      next: (dtos: any[]) => {
        const sorted = dtos
          // Date-based sorting logic: Descending order
          .sort((a, b) => new Date(b.dateAwarded).getTime() - new Date(a.dateAwarded).getTime())
          .slice(0, 3)
          .map(dto => ({
            ...dto,
            // Normalizing DTO to the standard Badge model
            mappedBadge: {
              id: dto.badgeId,
              name: dto.badgeName,
              description: dto.description,
              iconUrl: dto.iconUrl,
              pointsReward: dto.pointsReward,
              criteria: ''
            } as Badge
          }));

        this.recentBadges.set(sorted);
      }
    });
  }
}
