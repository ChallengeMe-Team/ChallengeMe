import { Component, OnInit, inject, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';

import { Challenge, Difficulty } from '../../../models/challenge.model';
import { ChallengeService } from '../../../services/challenge.service';
import { AuthService } from '../../../services/auth.service';
import { ChallengeFormComponent } from '../../forms/challenge-form/challenge-form';
import { ToastComponent } from '../../../shared/toast/toast-component';
import { AcceptChallengeModalComponent } from '../../accept-challenge-modal/accept-challenge-modal';
import { AssignChallengeModalComponent } from '../../assign-challenge-modal/assign-challenge-modal';
import { ChallengeCardComponent } from '../../../shared/challenge-card/challenge-card.component';
import { ConfirmationModalComponent } from '../../../shared/confirmation-modal/confirmation-modal.component';
import { FriendDTO } from '../../../services/user.service';

/**
 * Core component for managing the global challenge catalog.
 * Acts as an orchestrator for several sub-modals (Accept, Assign, Form, Confirmation)
 * and handles the business logic for challenge lifecycles.
 *
 * * Key Features:
 * - Dynamic Status Mapping: Tracks the relationship between the logged-in user
 * and each challenge (e.g., PENDING, ACCEPTED).
 * - Ownership Security: Restricts edit/delete actions to the original creator.
 * - Multi-Step Flow: Manages transition states for signing contracts and restarting completed habits.
 * - Category Theming: Dynamically assigns visual color schemes based on the quest category.
 */
@Component({
  selector: 'app-challenges',
  standalone: true,
  imports: [
    CommonModule,
    ToastComponent,
    ChallengeFormComponent,
    AcceptChallengeModalComponent,
    AssignChallengeModalComponent,
    ChallengeCardComponent,
    ConfirmationModalComponent
  ],
  templateUrl: './challenges-component.html'
})
export class ChallengesComponent implements OnInit {
  // Dependency Injection using the functional inject() API
  public challengeService = inject(ChallengeService);
  public auth = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  /**
   * Constructor handles Deep Linking.
   * Listens for query parameters to auto-trigger specific UI states (e.g., opening the creation modal).
   */
  constructor(private route: ActivatedRoute) {
    this.route.queryParams.subscribe(params => {
      if (params['openModal'] === 'true') {
        this.challengeService.isCreateModalOpen.set(true);
      }
    });
  }

  // --- COMPONENT STATE (SIGNALS) ---

  /** Signal containing the master list of all available quests fetched from the API. */
  challenges = signal<Challenge[]>([]);

  /** * Reactive Map tracking the relationship between the logged-in user and each challenge.
   * Keys are Challenge IDs, values are statuses (PENDING, ACCEPTED, COMPLETED).
   */
  userChallengeStatuses = signal<Map<string, string>>(new Map());

  /** Determines the layout grouping based on difficulty levels (EASY, MEDIUM, HARD). */
  difficultyKeys = Object.values(Difficulty) as Difficulty[];

  /** Loading state flag for asynchronous operations. */
  isLoading = signal(false);
  errorMessage = signal('');

  // --- MODAL & ACTION STATES ---
  isRestartModalOpen = false;
  challengeToRestart: Challenge | null = null;
  isEditModalOpen = signal(false);
  editChallenge = signal<any | null>(null);
  isDeleteModalOpen = signal(false);
  challengeToDelete = signal<Challenge | null>(null);
  isAcceptModalOpen = false;
  selectedChallengeForContract: any = null;
  isAssignModalOpen = false;
  selectedChallengeToAssign = signal<Challenge | null>(null);

  // --- TOAST NOTIFICATION STATE ---
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  /**
   * Lifecycle Hook: Initializes data fetching for both the global catalog
   * and the user's specific participation links.
   */
  ngOnInit(): void {
    this.loadChallenges();
    this.loadUserActiveChallenges();
  }

  /**
   * Fetches the global library of challenges.
   * Manually triggers change detection to ensure UI synchronization after async resolution.
   */
  loadChallenges() {
    this.isLoading.set(true);
    this.challengeService.getAllChallenges().subscribe({
      next: (data) => {
        this.challenges.set(data);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage.set('Error loading data.');
        this.isLoading.set(false);
      }
    });
  }

  /**
   * Retrieves user-specific participation records (ChallengeUser links).
   * Maps participation statuses to the userChallengeStatuses signal for real-time button state updates.
   */
  loadUserActiveChallenges() {
    const user = this.auth.currentUser();
    if (!user) return;

    this.challengeService.getAllUserChallengeLinks(user.id).subscribe({
      next: (data) => {
        const statusMap = new Map<string, string>();
        data.forEach((link: any) => {
          const cId = link.challenge?.id || link.challengeId;
          statusMap.set(cId, link.status);
        });
        this.userChallengeStatuses.set(statusMap);
      }
    });
  }

  /**
   * Filters the main quest list by difficulty for grouped display in the template.
   * @param difficulty The difficulty enum value to filter by.
   */
  getChallengesByDifficulty(difficulty: Difficulty): Challenge[] {
    return this.challenges().filter(c => c.difficulty === difficulty);
  }

  /**
   * Action Handler: Initiates the challenge enrollment flow.
   * Stores the selected quest and triggers the Accept Modal.
   */
  handleStart(challenge: Challenge) {
    this.selectedChallengeForContract = challenge;
    this.isAcceptModalOpen = true;
  }

  /** * Action Handler: Handles the transition for users wishing to restart a 'COMPLETED' challenge.
   * Triggers a confirmation dialog to prevent accidental restarts.
   */
  handleRestart(challenge: Challenge) {
    this.challengeToRestart = challenge;
    this.isRestartModalOpen = true;
  }

  /**
   * Action Handler: Triggers the social sharing flow to send a challenge to a friend.
   */
  handleAssign(challenge: Challenge) {
    this.selectedChallengeToAssign.set(challenge);
    this.isAssignModalOpen = true;
  }

  /** * Action Handler: Permission-guarded method that verifies ownership before opening the edit view.
   * Ensures data integrity by matching current session username with 'createdBy' metadata.
   */
  handleEdit(challenge: Challenge) {
    const user = this.auth.currentUser();
    if (!user || user.username !== challenge.createdBy) {
      this.showToast("You can only edit challenges created by you.", "error");
      return;
    }
    this.editChallenge.set(challenge);
    this.isEditModalOpen.set(true);
  }

  /**
   * Action Handler: Guards deletion requests by verifying ownership and requesting user confirmation.
   */
  handleDeleteRequest(data: {event: MouseEvent, challenge: Challenge}) {
    data.event.preventDefault();
    const user = this.auth.currentUser();
    if (!user || user.username !== data.challenge.createdBy) {
      this.showToast("You can only delete challenges created by you.", "error");
      return;
    }
    this.challengeToDelete.set(data.challenge);
    this.isDeleteModalOpen.set(true);
  }

  /**
   * Logic for Habit Maintenance: Resets a completed challenge back to 'ACCEPTED'.
   * Updates the start date to current day to restart the progress tracking.
   */
  confirmRestart() {
    if (!this.challengeToRestart) return;
    const challenge = this.challengeToRestart;
    const user = this.auth.currentUser();
    if (!user) return;

    this.isRestartModalOpen = false;

    this.challengeService.getAllUserChallengeLinks(user.id).subscribe(links => {
      const linkToUpdate = links.find((l: any) => (l.challenge?.id || l.challengeId) === challenge.id);

      if (linkToUpdate) {
        const payload = {
          status: 'ACCEPTED',
          startDate: new Date().toISOString().split('T')[0]
        };

        this.challengeService.updateChallengeUser(linkToUpdate.id, payload).subscribe({
          next: () => {
            this.loadUserActiveChallenges();
            this.showToast('Challenge restarted! Good luck again.', 'success');
            this.router.navigate(['/my-challenges']);
          },
          error: () => this.showToast('Failed to restart.', 'error')
        });
      }
    });
  }

  /**
   * Social Integration: Sends an invite to the selected friend.
   * @param friend The friend object receiving the assignment.
   */
  onFriendAssigned(friend: FriendDTO) {
    const challenge = this.selectedChallengeToAssign();
    if (!challenge) return;
    this.challengeService.assignChallenge(challenge.id, friend.id).subscribe({
      next: () => {
        this.showToast(`Challenge sent to ${friend.username}!`, "success");
        this.isAssignModalOpen = false;
      },
      error: (err) => this.showToast(err.error?.message || "Failed to assign", "error")
    });
  }

  /**
   * Form Submission: Finalizes the creation of a new challenge.
   * Injects the current user's username as the creator for ownership tracking.
   */
  onCreateChallenge(formValues: any) {
    const user = this.auth.currentUser();
    const newChallenge = { ...formValues, createdBy: user ? user.username : 'Anonymous' };
    this.challengeService.createChallenge(newChallenge).subscribe({
      next: () => {
        this.showToast("Challenge created!", "success");
        this.challengeService.isCreateModalOpen.set(false);
        this.loadChallenges();
      }
    });
  }

  /** * Enrollment Logic: Finalizes the participation contract.
   * Triggers the API link between User and Challenge with target start/end dates.
   */
  onContractSigned(dates: { start: string, end: string }) {
    if (!this.selectedChallengeForContract) return;
    this.challengeService.acceptChallenge(this.selectedChallengeForContract.id, dates.start, dates.end).subscribe({
      next: () => {
        this.isAcceptModalOpen = false;
        this.showToast("Commitment signed! 🚀", "success");
        setTimeout(() => { this.router.navigate(['/my-challenges']); }, 1500);
      }
    });
  }

  /**
   * Update Handler: Commits changes to an existing challenge.
   */
  onChallengeUpdated(updated: any) {
    this.challengeService.updateChallenge(updated.id, updated).subscribe({
      next: () => {
        this.showToast("Challenge updated!", "success");
        this.loadChallenges();
        this.isEditModalOpen.set(false);
      }
    });
  }

  /**
   * Deletion Handler: Permanently removes a quest from the database.
   * Updates the local Signal master list to avoid a full page refresh.
   */
  confirmDelete() {
    const challenge = this.challengeToDelete();
    if (!challenge) return;
    this.challengeService.deleteChallenge(challenge.id).subscribe({
      next: () => {
        this.showToast("Deleted successfully.", "success");
        this.challenges.update(prev => prev.filter(c => c.id !== challenge.id));
        this.isDeleteModalOpen.set(false);
      }
    });
  }

  /**
   * Visual Utility: Manages ephemeral UI notifications.
   * @param message String to be displayed.
   * @param type Contextual coloring (success/error).
   */
  showToast(message: string, type: 'success' | 'error' = 'error') {
    this.toastMessage.set(message);
    this.toastType.set(type);
    setTimeout(() => { this.toastMessage.set(null); }, 3000);
  }

  /**
   * UI Theming: Maps challenge categories to specific Tailwind CSS background classes.
   * Ensures a distinct visual identity for different quest types.
   * @param category The string category name.
   * @returns A string representing the CSS class for the card badge.
   */
  getCategoryClass(category: string): string {
    const mapping: { [key: string]: string } = {
      'Fitness': 'bg-fuchsia-500',
      'Health': 'bg-rose-600',
      'Mindfulness': 'bg-blue-400',
      'Education': 'bg-purple-600',
      'Coding': 'bg-indigo-600',
      'Creativity': 'bg-amber-500',
      'Social': 'bg-orange-500',
      'Food': 'bg-yellow-600',
      'Lifestyle': 'bg-emerald-500'
    };
     return mapping[category] || 'bg-gray-500';
  }
}
