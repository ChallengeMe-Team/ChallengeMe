import {
  Component,
  ChangeDetectionStrategy,
  OnInit,
  signal,
  ElementRef,
  AfterViewInit,
  Output,
  EventEmitter, inject
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlusCircle } from 'lucide-angular';
import { HeroSectionComponent } from '../../hero-section/hero-section.component';
import { StatsGridComponent } from '../../stats-grid/stats-grid.component';
import { ActiveChallengesComponent } from './active-challenges/active-challenges.component';
import { BadgeShowcaseComponent } from './badge-showcase/badge-showcase.component';
import {LucideAngularModule, Zap} from 'lucide-angular';
import { Router } from '@angular/router';
import {CompleteChallengeModalComponent} from '../../complete-challenge-modal/complete-challenge-modal-component';
import confetti from 'canvas-confetti';
import {ChallengeService} from '../../../services/challenge.service';
import {AuthService} from '../../../services/auth.service';

/**
 * Manages the landing experience, scroll animations, and the mission completion flow.
 * * Key Responsibilities:
 * - Scroll Reveal System: Uses IntersectionObserver to trigger animations as sections
 * enter the viewport.
 * - Victory Flow: Orchestrates the API call to complete a challenge, updates global
 * user XP, and triggers celebration effects.
 * - Deep Linking: Handles navigation with query parameters to open specific forms
 * in other routes.
 */
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    HeroSectionComponent,
    StatsGridComponent,
    ActiveChallengesComponent,
    BadgeShowcaseComponent,
    LucideAngularModule,
    CompleteChallengeModalComponent
  ],

  changeDetection: ChangeDetectionStrategy.Default,
  templateUrl: './home-component.html',
  styleUrls: ['./home-component.css']
})
export class HomeComponent implements OnInit, AfterViewInit {
  constructor(private el: ElementRef) {}

  // Functional Service Injections
  private router = inject(Router);
  private challengeService = inject(ChallengeService);
  private authService = inject(AuthService);

  /** Signal-based loading state to ensure smooth component transitions. */
  isLoading = signal(true);

  // Icon Definitions
  readonly icons = {
    PlusCircle: PlusCircle,
  };
  readonly zapIcon = Zap;

  // Completion Workflow State
  isCompletionModalVisible = false;
  selectedChallengeForCompletion: any = null;

  /**
   * Method: ngOnInit
   * ----------------
   * Triggers a short artificial delay (400ms) to ensure child widgets have
   * completed their initial internal rendering before revealing the dashboard.
   */
  ngOnInit() {
    setTimeout(() => this.isLoading.set(false), 400);
  }

  /**
   * Method: ngAfterViewInit
   * -----------------------
   * Initializes the IntersectionObserver to handle the "Reveal on Scroll" effect.
   * Elements with the '.reveal-on-scroll' class are tracked and updated with
   * visibility classes when 10% of the element is visible.
   */
  ngAfterViewInit() {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('reveal-visible');
        }
      });
    }, { threshold: 0.1 });

    const sections = this.el.nativeElement.querySelectorAll('.reveal-on-scroll');
    sections.forEach((section: HTMLElement) => observer.observe(section));
  }

  @Output() startChallengeRequest = new EventEmitter<void>();
  @Output() exploreChallenges = new EventEmitter<void>();

  /**
   * Method: onStartChallenge
   * ------------------------
   * Implements "Deep Linking" navigation. Redirects the user to the Challenges page
   * and passes a query parameter to automatically open the creation modal.
   */
  onStartChallenge() {
    console.log('Redirecționare către Challenges pentru deschidere formular...');
    this.router.navigate(['/challenges'], { queryParams: { openModal: 'true' } });
  }

  /** Simple router navigation to the global challenge catalog. */
  onExploreChallenges() { this.router.navigate(['/challenges']); }

  /**
   * Method: handleOpenCompletion
   * ----------------------------
   * Triggered by child components (e.g., ActiveChallenges) to initiate the
   * "Claim Victory" workflow for a specific quest.
   */
  handleOpenCompletion(challenge: any) {
    this.selectedChallengeForCompletion = challenge;
    this.isCompletionModalVisible = true;
  }

  /**
   * Method: handleClaimSuccess
   * --------------------------
   * The core "Victory" logic of the platform:
   * 1. API Call: Updates the persistent challenge link status to 'COMPLETED'.
   * 2. Visual Celebration: Triggers the confetti animation.
   * 3. Session Update: Recalculates user XP and mission counts in the global signal state.
   * 4. State Sync: Forces a refresh of active quest lists.
   */
  handleClaimSuccess() {
    if (!this.selectedChallengeForCompletion) return;

    const currentUser = this.authService.currentUser();
    if (!currentUser) return;

    const challengeUserId = this.selectedChallengeForCompletion.id;

    this.challengeService.updateChallengeUser(challengeUserId, { status: 'COMPLETED' }).subscribe({
      next: () => {
        this.isCompletionModalVisible = false;
        this.triggerWinConfetti();

        // Reactive update of the current user session
        const earnedXP = this.selectedChallengeForCompletion.mappedChallenge?.points || 0;
        const newPoints = (currentUser.points || 0) + earnedXP;
        const newMissions = (currentUser.totalCompletedChallenges || 0) + 1;

        this.authService.currentUser.set({
          ...currentUser,
          points: newPoints,
          totalCompletedChallenges: newMissions
        });
        console.log('Victory! XP adăugat:', earnedXP);

        this.refreshActiveChallenges();
      },
      error: (err) => console.error('Eroare la salvarea victoriei:', err)
    });
  }

  /** Forces a UI refresh to clear completed quests from the dashboard after a delay. */
  private refreshActiveChallenges() {
    setTimeout(() => {
      window.location.reload();
    }, 2000);
  }

  /**
   * Method: triggerWinConfetti
   * --------------------------
   * Visual UX Enhancement: Executes a 3-second multi-origin confetti animation
   * using the platform's signature purple and indigo color palette.
   */
  private triggerWinConfetti() {
    /* Logic for purple & indigo particles */
    const duration = 3 * 1000;
    const end = Date.now() + duration;

    const frame = () => {
      confetti({
        particleCount: 3,
        angle: 60,
        spread: 55,
        origin: { x: 0 },
        colors: ['#a855f7', '#6366f1']
      });
      confetti({
        particleCount: 3,
        angle: 120,
        spread: 55,
        origin: { x: 1 },
        colors: ['#a855f7', '#6366f1']
      });

      if (Date.now() < end) {
        requestAnimationFrame(frame);
      }
    };
    frame();
  }
}
