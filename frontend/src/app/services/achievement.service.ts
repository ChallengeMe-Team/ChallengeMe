import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import confetti from 'canvas-confetti';

@Injectable({
  providedIn: 'root'
})
export class AchievementService {
  private router = inject(Router);

  /**
   * Triggers the "New Badge" celebration sequence:
   * 1. Full screen confetti.
   * 2. Wait 1.5s.
   * 3. Redirect to /badges.
   */
  celebrateNewBadge() {
    this.fireMassiveConfetti();

    setTimeout(() => {
      this.router.navigate(['/badges']);
    }, 1500);
  }

  private fireMassiveConfetti() {
    // A more impressive, full-screen "Gold & Purple" celebration
    const duration = 1500;
    const animationEnd = Date.now() + duration;
    const defaults = { startVelocity: 30, spread: 360, ticks: 60, zIndex: 0 };

    const randomInRange = (min: number, max: number) => Math.random() * (max - min) + min;

    const interval: any = setInterval(function() {
      const timeLeft = animationEnd - Date.now();

      if (timeLeft <= 0) {
        return clearInterval(interval);
      }

      const particleCount = 50 * (timeLeft / duration);

      // Since particles fall down, start a bit higher than random
      confetti({
        ...defaults,
        particleCount,
        origin: { x: randomInRange(0.1, 0.3), y: Math.random() - 0.2 },
        colors: ['#a855f7', '#fbbf24', '#ffd700'] // Purple & Gold
      });
      confetti({
        ...defaults,
        particleCount,
        origin: { x: randomInRange(0.7, 0.9), y: Math.random() - 0.2 },
        colors: ['#a855f7', '#fbbf24', '#ffd700']
      });
    }, 250);
  }
}
