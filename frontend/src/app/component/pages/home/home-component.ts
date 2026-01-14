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

  private router = inject(Router); // Injectează Router-ul pentru navigare
  private challengeService = inject(ChallengeService); // Injectăm serviciul care controlează modalul
  private authService = inject(AuthService);

  isLoading = signal(true);

  readonly icons = {
    PlusCircle: PlusCircle,
  };

  readonly zapIcon = Zap;

  // Starea pentru modalul de finalizare
  isCompletionModalVisible = false;
  selectedChallengeForCompletion: any = null;

  ngOnInit() {
    // Timp scurt de loading pentru a asigura randarea componentelor copil
    setTimeout(() => this.isLoading.set(false), 400);
  }

  ngAfterViewInit() {
    // Initializam observatorul pentru animatiile la scroll
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

  onStartChallenge() {
    console.log('Redirecționare către Challenges pentru deschidere formular...');
    this.router.navigate(['/challenges'], { queryParams: { openModal: 'true' } });
  }

  onExploreChallenges() { this.router.navigate(['/challenges']); }

  // Metodă pentru a deschide modalul (va fi apelată de butonul verde)
  handleOpenCompletion(challenge: any) {
    this.selectedChallengeForCompletion = challenge;
    this.isCompletionModalVisible = true;
  }

  handleClaimSuccess() {
    if (!this.selectedChallengeForCompletion) return;

    const currentUser = this.authService.currentUser();
    if (!currentUser) return;

    // Folosim ID-ul de legătură (8888...) pentru URL-ul de API
    const challengeUserId = this.selectedChallengeForCompletion.id;

    this.challengeService.updateChallengeUser(challengeUserId, { status: 'COMPLETED' }).subscribe({
      next: () => {
        this.isCompletionModalVisible = false;
        this.triggerWinConfetti();

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

  // Metodă pentru a forța reîncărcarea listei de quest-uri active
  private refreshActiveChallenges() {
    setTimeout(() => {
      window.location.reload();
    }, 2000); // Așteaptă 2 secunde pentru confetti
  }

  private triggerWinConfetti() {
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
