import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import confetti from 'canvas-confetti';

/**
 A singleton service responsible for orchestrating global celebration events.
 * It acts as a bridge between quest completion logic and visual reward feedback.
 *
 * * * Key Technical Aspects:
 * - Particle Simulation: Integrates the 'canvas-confetti' library to generate
 * high-performance, non-blocking visual effects.
 * - Timed Navigation: Manages the transition from a "victory state" to the
 * achievement gallery using controlled delays.
 * - Randomized Physics: Uses interval-based procedural generation to simulate
 * realistic particle physics across the entire viewport.
 */
@Injectable({
  providedIn: 'root'
})
export class AchievementService {
  private router = inject(Router);

  /**
   * Orchestrates the multi-stage reward sequence when a new badge is unlocked.
   * 1. Visual Trigger: Executes the 'Massive Confetti' procedural animation.
   * 2. Contextual Delay: Pauses for 1500ms to allow the user to experience
   * the visual reward before changing state.
   * 3. Navigation: Force-redirects the user to the '/badges' route to view
   * their updated collection.
   */
  celebrateNewBadge() {
    this.fireMassiveConfetti();

    setTimeout(() => {
      this.router.navigate(['/badges']);
    }, 1500);
  }

  /**
   * A private procedural engine that generates a full-screen "Gold & Purple"
   * celebration.
   * * Logic:
   * - Uses a recursive 'setInterval' pattern to maintain particle density
   * over a 1.5-second window.
   * - Origin Manipulation: Particles are fired from two distinct horizontal
   * zones (Left: 10-30%, Right: 70-90%) to create a frame-filling effect.
   * - Tapering Effect: The 'particleCount' decreases linearly over time to
   * simulate a natural fade-out of the celebration.
   */
  private fireMassiveConfetti() {
    const duration = 1500;
    const animationEnd = Date.now() + duration;
    const defaults = { startVelocity: 30, spread: 360, ticks: 60, zIndex: 0 };

    const randomInRange = (min: number, max: number) => Math.random() * (max - min) + min;

    const interval: any = setInterval(function() {
      const timeLeft = animationEnd - Date.now();

      if (timeLeft <= 0) {
        return clearInterval(interval);
      }

      // Linear decay of particle intensity
      const particleCount = 50 * (timeLeft / duration);

      // Procedural trigger for the left-side fountain
      confetti({
        ...defaults,
        particleCount,
        origin: { x: randomInRange(0.1, 0.3), y: Math.random() - 0.2 },
        colors: ['#a855f7', '#fbbf24', '#ffd700'] // Purple & Gold
      });

      // Procedural trigger for the right-side fountain
      confetti({
        ...defaults,
        particleCount,
        origin: { x: randomInRange(0.7, 0.9), y: Math.random() - 0.2 },
        colors: ['#a855f7', '#fbbf24', '#ffd700']
      });
    }, 250); // Fires every quarter-second for a smooth "showering" effect
  }
}
