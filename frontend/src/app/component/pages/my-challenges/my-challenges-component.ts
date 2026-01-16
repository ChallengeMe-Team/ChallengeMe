import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { LucideAngularModule, Edit, Trash2, PlusCircle, Check, X, Clock, CheckCircle } from 'lucide-angular';
import { ChallengeService } from '../../../services/challenge.service';
import { AuthService } from '../../../services/auth.service';
import { BadgeService } from '../../../services/badge.service';
import { AchievementService } from '../../../services/achievement.service';
import { Challenge } from '../../../models/challenge.model';
import { ChallengeFormComponent } from '../../forms/challenge-form/challenge-form';
import { ToastComponent } from '../../../shared/toast/toast-component';
import { AcceptChallengeModalComponent } from '../../accept-challenge-modal/accept-challenge-modal';
import { ConfirmationModalComponent } from '../../../shared/confirmation-modal/confirmation-modal.component';
import { CompleteChallengeModalComponent } from '../../complete-challenge-modal/complete-challenge-modal-component';
import { getCategoryGradient } from '../../../shared/utils/color-utils';
import { firstValueFrom } from 'rxjs';

import confetti from 'canvas-confetti';

/**
 * This component serves as the central hub for a user's quest management.
 * It organizes challenges into three distinct operational states using Angular Signals:
 * 1. Active: Challenges currently in progress.
 * 2. Inbox: New challenges received from other users (Pending).
 * 3. Created: Quests designed and published by the current user.
 *
 * * Key Architectural Patterns:
 * - Reactive State Management: Uses 'signal' for high-performance UI updates without full re-renders.
 * - Async/Await Synchronization: Uses 'firstValueFrom' to handle sequential badge verification logic.
 * - Modal Orchestration: Controls 6 specialized modals for CRUD and participation workflows.
 */
@Component({
  selector: 'app-my-challenges',
  standalone: true,
  imports: [
    CommonModule,
    ChallengeFormComponent,
    ToastComponent,
    LucideAngularModule,
    AcceptChallengeModalComponent,
    CompleteChallengeModalComponent,
    ConfirmationModalComponent
  ],
  templateUrl: './my-challenges-component.html',
  styles: []
})
export class MyChallengesComponent implements OnInit {
  // Injections
  public challengeService = inject(ChallengeService);
  public auth = inject(AuthService);
  public badgeService = inject(BadgeService);
  public achievementService = inject(AchievementService);

  private route = inject(ActivatedRoute);
  private router = inject(Router);

  // --- STATE MANAGEMENT (SIGNALS) ---
  /** Tracks the currently visible tab to toggle list visibility. */
  activeTab = signal<'active' | 'inbox' | 'created'>('active');

  // Lists
  /** Master list of challenges authored by the user. */
  myChallenges = signal<Challenge[]>([]);
  /** Challenges awaiting user response (Accept/Decline). */
  inboxChallenges = signal<any[]>([]);
  /** Challenges currently being pursued. */
  activeChallenges = signal<any[]>([]);

  isLoading = signal(false);

  // Modale Existing
  isEditModalOpen = signal(false);
  editChallengeData = signal<any | null>(null);
  isDeleteModalOpen = signal(false);
  challengeToDelete = signal<Challenge | null>(null);

  // Modale Accept (Contract)
  isAcceptModalOpen = false;
  selectedChallengeForContract: any = null;

  // State for Completion Modal
  isCompleteModalOpen = false;
  selectedChallengeForCompletion: any = null;

  // State pentru Decline Modal
  isDeclineModalOpen = false;
  challengeToDecline: any = null;

// --- UI FEEDBACK ---
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  readonly icons = { Edit, Trash2, PlusCircle, Check, X, Clock, CheckCircle };

  /** * Lifecycle Hook: Initialization
   * - Reset scroll position for UX consistency.
   * - Deep Linking: Subscribes to queryParams to automatically switch to the 'inbox' or 'active' tab
   * when navigated from notifications or the dashboard.
   */
  ngOnInit() {
    window.scrollTo(0, 0);

    this.route.queryParams.subscribe(params => {
      if (params['tab'] === 'inbox') {
        setTimeout(() => {
          this.switchTab('inbox');
        }, 50);
      }
      // Verifică dacă ai primit tab='active' din Home
      else if (params['tab'] === 'active') {
        this.switchTab('active');
      }
    });
    this.loadAllData();
  }

  /**
   * Data Orchestrator: loadAllData()
   * Simultaneously fetches three distinct datasets from ChallengeService:
   * 1. Personal creations (By username).
   * 2. Pending invites (Status: PENDING).
   * 3. Current quests (Status: ACCEPTED).
   */
  loadAllData() {
    const currentUser = this.auth.currentUser();
    if (!currentUser) return;

    this.isLoading.set(true);

    this.challengeService.getUserChallenges(currentUser.username).subscribe({
      next: (data) => this.myChallenges.set(data),
      error: () => this.showToast('Failed to load created challenges', 'error'),
      complete: () => this.isLoading.set(false)
    });

    if (currentUser.id) {
      this.challengeService.getChallengesByStatus(currentUser.id, 'PENDING').subscribe({
        next: (data) => this.inboxChallenges.set(data),
        error: () => this.showToast('Failed to load inbox', 'error')
      });

      this.challengeService.getChallengesByStatus(currentUser.id, 'ACCEPTED').subscribe({
        next: (data) => this.activeChallenges.set(data),
        error: () => this.showToast('Failed to load active challenges', 'error')
      });
    }
  }

  /** Swaps the active view and triggers template re-rendering via Signal update. */
  switchTab(tab: 'active' | 'inbox' | 'created') {
    this.activeTab.set(tab);
  }

  /** Initiates the acceptance flow by capturing the selected inbox item. */
  acceptChallenge(item: any) {
    this.selectedChallengeForContract = item;
    this.isAcceptModalOpen = true;
  }

  /**
   * Workflow: onContractSigned(dates)
   * Finalizes the "Pending -> Active" transition.
   * - Sends a payload with 'ACCEPTED' status and target deadlines to the backend.
   * - Optimistically updates local signals to move the item from 'inbox' to 'active'.
   */
  onContractSigned(dates: { start: string, end: string }) {
    if (!this.selectedChallengeForContract) return;

    const payload = {
      status: 'ACCEPTED',
      startDate: dates.start,
      targetDeadline: dates.end
    };

    this.challengeService.updateChallengeUser(this.selectedChallengeForContract.id, payload).subscribe({
      next: () => {
        this.showToast('Challenge Accepted! 🚀', 'success');
        this.isAcceptModalOpen = false;
        this.inboxChallenges.update(prev => prev.filter(c => c.id !== this.selectedChallengeForContract.id));
        this.activeChallenges.update(prev => [...prev, {
          ...this.selectedChallengeForContract,
          status: 'ACCEPTED',
          startDate: dates.start
        }]);
        this.switchTab('active');
      },
      error: (err) => {
        this.showToast(err.error?.message || 'Failed to accept challenge.', 'error');
      }
    });
  }

  /** Triggers the decline confirmation modal for an inbox item. */
  declineChallenge(item: any) {
    this.challengeToDecline = item;
    this.isDeclineModalOpen = true;
  }

  /** Finalizes the rejection of a challenge, removing it from the user's inbox. */
  onConfirmDecline() {
    if (!this.challengeToDecline) return;

    this.challengeService.refuseChallenge(this.challengeToDecline.id).subscribe({
      next: () => {
        this.showToast('Challenge Declined.', 'success');
        this.inboxChallenges.update(prev => prev.filter(c => c.id !== this.challengeToDecline.id));
        this.isDeclineModalOpen = false;
        this.challengeToDecline = null;
      },
      error: () => {
        this.showToast('Action failed.', 'error');
        this.isDeclineModalOpen = false;
      }
    });
  }

  /** Opens the victory portal for a specific active challenge. */
  openCompleteModal(item: any) {
    this.selectedChallengeForCompletion = item;
    this.isCompleteModalOpen = true;
  }

  /**
   * Complex Workflow: onChallengeCompleted()
   * --------------------------------------
   * Implements the "Victory & Achievement" logic:
   * 1. Pre-Check: Captures the current badge state.
   * 2. Transaction: Updates status to 'COMPLETED' in the DB.
   * 3. Reward: Calculates and updates user XP reactively via AuthService.
   * 4. Badge Comparison: Diffs the old vs. new badge list to detect if this completion unlocked a new achievement.
   * 5. Celebration: Triggers the AchievementService for badge redirects or local confetti for standard XP gain.
   */
  async onChallengeCompleted() {
    if (!this.selectedChallengeForCompletion) return;
    const currentUser = this.auth.currentUser();
    if (!currentUser) return;

    try {

      const oldBadges = await firstValueFrom(this.badgeService.getUserBadges(currentUser.username));

      const payload = { status: 'COMPLETED' };
      await firstValueFrom(this.challengeService.updateChallengeUser(this.selectedChallengeForCompletion.id, payload));

      const newBadges = await firstValueFrom(this.badgeService.getUserBadges(currentUser.username));

      // UI Sync
      this.activeChallenges.update(prev => prev.filter(c => c.id !== this.selectedChallengeForCompletion.id));
      const newPoints = (currentUser.points || 0) + (this.selectedChallengeForCompletion.points || 0);
      this.auth.currentUser.set({ ...currentUser, points: newPoints });

      const newlyEarnedBadge = this.findNewBadge(oldBadges, newBadges);

      this.isCompleteModalOpen = false;

      if (newlyEarnedBadge) {
        const badgeName = newlyEarnedBadge.badge?.name || 'New Badge';
        this.showToast(`New Badge Unlocked: ${badgeName}! 🏆`, 'success');

        this.achievementService.celebrateNewBadge();
      } else {
        this.showToast(`Victory! +${this.selectedChallengeForCompletion.points} XP Earned!`, 'success');
        this.triggerSuccessAnimation(); // standard small confetti
      }

    } catch (err) {
      console.error(err);
      this.showToast('Failed to complete challenge.', 'error');
    }
  }

  /**
   * A pure utility function that performs an array difference to isolate
   * a newly earned badge object by unique ID.
   */
  private findNewBadge(oldList: any[], newList: any[]): any | undefined {
    if (!newList || !oldList) return undefined;
    return newList.find(newItem => !oldList.some(oldItem => oldItem.id === newItem.id));
  }

  /** Visual UX: Triggers a multi-colored confetti effect for quest victories. */
  private triggerSuccessAnimation() {
    const duration = 2 * 1000;
    const end = Date.now() + duration;

    const frame = () => {
      confetti({
        particleCount: 3,
        angle: 60,
        spread: 55,
        origin: { x: 0 },
        colors: ['#a855f7', '#3b82f6', '#22c55e']
      });
      confetti({
        particleCount: 3,
        angle: 120,
        spread: 55,
        origin: { x: 1 },
        colors: ['#a855f7', '#3b82f6', '#22c55e']
      });

      if (Date.now() < end) {
        requestAnimationFrame(frame);
      }
    };
    frame();
  }

  /** Helper for category-based visual theming. */
  getCategoryStyle(category: string) {
    return getCategoryGradient(category);
  }

  /** Finalizes challenge creation, injecting the author's identity into the payload. */
  onCreateChallenge(formValues: any) {
    const currentUser = this.auth.currentUser();
    if (!currentUser) return;
    const newChallenge = { ...formValues, createdBy: currentUser.username };

    this.challengeService.createChallenge(newChallenge).subscribe({
      next: () => {
        this.showToast('Challenge created successfully!', 'success');
        this.challengeService.isCreateModalOpen.set(false);
        this.loadAllData();
      },
      error: () => this.showToast('Failed to create challenge.', 'error')
    });
  }

  /** Maps selected challenge to the edit signal and opens the form modal. */
  openEditModal(challenge: Challenge) {
    this.editChallengeData.set(challenge);
    this.isEditModalOpen.set(true);
  }

  /** Commits changes to an existing challenge and refreshes the lists. */
  onChallengeUpdated(updatedData: any) {
    this.challengeService.updateChallenge(updatedData.id, updatedData).subscribe({
      next: () => {
        this.showToast('Challenge updated!', 'success');
        this.isEditModalOpen.set(false);
        this.loadAllData();
      },
      error: () => this.showToast('Update failed.', 'error')
    });
  }

  /** Sets target for deletion and triggers confirmation UI. */
  confirmDelete(challenge: Challenge) {
    this.challengeToDelete.set(challenge);
    this.isDeleteModalOpen.set(true);
  }

  /** Orchestrates the deletion of a challenge and optimistically removes it from the signal. */
  performDelete() {
    const target = this.challengeToDelete();
    if (!target) return;
    this.challengeService.deleteChallenge(target.id).subscribe({
      next: () => {
        this.showToast('Challenge deleted.', 'success');
        this.isDeleteModalOpen.set(false);
        this.myChallenges.update(list => list.filter(c => c.id !== target.id));
      },
      error: () => this.showToast('Delete failed.', 'error')
    });
  }

  /**
   * Standardized feedback mechanism. Uses a 3-second auto-cleanup to prevent
   * UI clutter.
   */
  showToast(msg: string, type: 'success' | 'error') {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    setTimeout(() => this.toastMessage.set(null), 3000);
  }
}
