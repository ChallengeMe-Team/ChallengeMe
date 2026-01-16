import {Component, EventEmitter, inject, OnInit, Output, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ChallengeService } from '../../../../services/challenge.service';
import { AuthService } from '../../../../services/auth.service';
import { ChallengeCardComponent } from '../../../../shared/challenge-card/challenge-card.component';
import { Challenge } from '../../../../models/challenge.model';

/**
 * Purpose: Provides a dashboard overview of the user's currently active quests.
 * It filters global participation data to show only 'ACCEPTED' or 'IN_PROGRESS'
 * challenges, prioritized by recent acceptance.
 * * * Key Technical Aspects:
 * - Reactive State: Uses 'signal' to handle the UI state for active quest items.
 * - Data Transformation: Maps complex backend DTOs into a local 'mappedChallenge'
 * format compatible with the standard ChallengeCardComponent.
 * - Event-Driven: Emits the full ChallengeUser context to the parent component
 * to trigger specialized interactions like the "Completion Portal".
 */
@Component({
  selector: 'app-active-challenges',
  standalone: true,
  imports: [CommonModule, ChallengeCardComponent, RouterModule],
  template: `
    <div class="bg-[#1a1627]/50 p-6 rounded-2xl border border-white/5 backdrop-blur-sm h-full flex flex-col">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-bold text-white flex items-center gap-2">
          <span class="text-yellow-400">⚔️</span> Active Quests
        </h2>
        <a routerLink="/my-challenges" [queryParams]="{ tab: 'active' }"
           class="text-xs font-medium text-purple-400 hover:text-purple-300 transition-colors uppercase tracking-wider">
          View All
        </a>
      </div>

      <div class="flex-1 flex flex-col gap-3" *ngIf="activeChallenges().length > 0; else emptyState">
        <div *ngFor="let item of activeChallenges()" class="animate-fade-in">
          <app-challenge-card
            [challenge]="item.mappedChallenge"
            [status]="item.status"
            [categoryClass]="'bg-blue-600/80'"
            [compact]="true"
            (continue)="onResume(item)"> </app-challenge-card>
        </div>
      </div>

      <ng-template #emptyState>
        <div class="flex-1 flex flex-col items-center justify-center text-center p-6 bg-black/20 rounded-xl border border-dashed border-gray-700/50">
          <div class="w-12 h-12 rounded-full bg-gray-800 flex items-center justify-center mb-3">
            <span class="text-2xl">💤</span>
          </div>
          <p class="text-gray-400 text-sm mb-4">No active quests. Time to start a new adventure!</p>
          <a routerLink="/challenges"
             class="px-5 py-2 bg-purple-600 hover:bg-purple-500 text-white text-sm rounded-full font-bold transition-all shadow-lg shadow-purple-900/20">
            Browse Challenges
          </a>
        </div>
      </ng-template>
    </div>
  `
})
export class ActiveChallengesComponent implements OnInit {
  private challengeService = inject(ChallengeService);
  private authService = inject(AuthService);

  /** Signal containing the filtered list of quests currently being pursued by the user. */
  activeChallenges = signal<any[]>([]);

  /** * Lifecycle Hook: Initializes the active quest stream.
   * 1. Retrieves user context from AuthService.
   * 2. Filters links for 'ACCEPTED' and 'IN_PROGRESS' statuses.
   * 3. Sorts by recency (dateAccepted) and limits display to the top 3 items.
   */
  ngOnInit() {
    const userId = this.authService.currentUser()?.id;
    if (!userId) return;

    this.challengeService.getAllUserChallengeLinks(userId).subscribe({
      next: (dtos: any[]) => {
        const filtered = dtos
          .filter(dto => ['ACCEPTED', 'IN_PROGRESS'].includes(dto.status))
          .sort((a, b) => new Date(b.dateAccepted).getTime() - new Date(a.dateAccepted).getTime())
          .slice(0, 3)
          .map(dto => ({
            ...dto,
            // Normalizing the backend DTO into the Challenge model used by child cards
            mappedChallenge: {
              id: dto.challengeId,
              title: dto.challengeTitle,
              description: dto.description,
              category: dto.category,
              difficulty: dto.difficulty,
              points: dto.points,
              createdBy: dto.challengeCreatedBy
            } as Challenge
          }));

        this.activeChallenges.set(filtered);
      }
    });
  }

  /** Emits the specific ChallengeUser link object to trigger completion workflows in the parent. */
  @Output() openPortal = new EventEmitter<any>();

  /**
   * Action Handler: Triggered when a user clicks to resume/complete a quest.
   * Emits the full 'item' context which contains the essential ChallengeUser ID required by the backend.
   */
  onResume(item: any) {
    console.log('Resuming adventure:', item.mappedChallenge.title);
    this.openPortal.emit(item);
  }
}
