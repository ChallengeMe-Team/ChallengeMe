import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { BadgeService } from '../../../services/badge.service';
import { AuthService } from '../../../services/auth.service'; // Asigură-te că calea e corectă
import { BadgeDisplay } from '../../../models/badge.model';

@Component({
  selector: 'app-badges-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './badges-page.component.html',
  styleUrls: ['./badges-page.component.css']
})
export class BadgesPageComponent implements OnInit {
  badgeList: BadgeDisplay[] = [];
  isLoading = signal(true);

  constructor(
    private badgeService: BadgeService,
    private authService: AuthService // 1. Injectăm AuthService
  ) {}

  ngOnInit(): void {
    // 2. Luăm username-ul dinamic (modifică metoda dacă în service-ul tău se numește altfel, ex: getUser().username)
    const username = this.authService.getUsername();

    if (!username) {
      console.error('User not logged in');
      this.isLoading.set(false);
      return;
    }

    forkJoin({
      allBadges: this.badgeService.getAll(),
      userBadges: this.badgeService.getUserBadges(username) // 3. Folosim variabila dinamică
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
