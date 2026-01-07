import { Component, OnInit, inject, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { UserProfile } from '../../models/user.model';

@Component({
  selector: 'app-hero-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hero-section.component.html',
  styleUrls: ['./hero-section.component.css']
})
export class HeroSectionComponent implements OnInit {
  private userService = inject(UserService);

  // Semnal pentru datele utilizatorului
  userProfile = signal<UserProfile | null>(null);

  // Calcule derivate folosind computed signals
  readonly points = computed(() => this.userProfile()?.points ?? 0);
  readonly username = computed(() => this.userProfile()?.username ?? 'Challenger');

  readonly level = computed(() => Math.floor(this.points() / 100) + 1);
  readonly xpInCurrentLevel = computed(() => this.points() % 100);
  readonly xpNeededForNextLevel = computed(() => 100 - this.xpInCurrentLevel());
  readonly progressPercentage = computed(() => this.xpInCurrentLevel());

  // Logică pentru Social Proof (bazată pe praguri statice momentan)
  readonly socialProofMessage = computed(() => {
    const pts = this.points();
    if (pts > 500) return "You're in the top 5% of achievers this week!";
    if (pts > 200) return "You're outperforming 70% of new challengers!";
    return "Top 10% potential! Keep pushing!";
  });

  ngOnInit(): void {
    this.userService.getProfile().subscribe({
      next: (profile) => this.userProfile.set(profile),
      error: (err) => console.error('Could not fetch profile', err)
    });
  }
}
