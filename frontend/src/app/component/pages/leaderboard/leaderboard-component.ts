import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AuthService} from '../../../services/auth.service';
import {LeaderboardService} from '../../../services/leaderboard.service';
import {LeaderboardEntry, LeaderboardRange} from '../../../models/leaderboard.model';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './leaderboard-component.html',
  styleUrls: ['./leaderboard-component.css']
})
export class LeaderboardComponent implements OnInit {
  private authService = inject(AuthService);
  private leaderboardService = inject(LeaderboardService);

  // Userul logat
  currentUser = this.authService.currentUser;

  // State
  leaderboardEntries = signal<LeaderboardEntry[]>([]);
  currentRange = signal<LeaderboardRange>(LeaderboardRange.ALL_TIME);
  isLoading = signal<boolean>(false);

  // Expunem enum-ul pentru HTML
  public RangeType = LeaderboardRange;

  // COMPUTED: Găsim rank-ul meu direct din lista descărcată
  myRankData = computed(() => {
    const user = this.currentUser();
    const list = this.leaderboardEntries();

    if (!user || list.length === 0) return null;

    // Căutăm intrarea care are username-ul meu
    return list.find(entry => entry.username === user.username) || null;
  });

  imageErrorFooter = false;
  // COMPUTED: Verificăm dacă sunt vizibil în primele X intrări (ex: top 100)
  // Deoarece backend-ul tău returnează TOATĂ lista, "visible" înseamnă
  // pur și simplu dacă utilizatorul a scrollat până acolo.
  // Dar pentru "Sticky Footer", verificăm dacă sunt în primii 10 (ca exemplu).
  isUserInTopList = computed(() => {
    const myData = this.myRankData();
    if (!myData) return false;
    // Dacă rangul meu e mai mic sau egal cu 10, nu arătam footer-ul
    return myData.rank <= 10;
  });

  ngOnInit() {
    this.loadData();
  }

  // Schimbarea tab-urilor (Weekly / Monthly / All Time)
  setRange(range: LeaderboardRange) {
    this.currentRange.set(range);
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);
    this.leaderboardService.getLeaderboard(this.currentRange())
      .subscribe({
        next: (data: LeaderboardEntry[]) => {
          // Sortăm datele astfel încât cei cu 0 XP să apară la final, dar să fie prezenți
          const sortedData = data.sort((a, b) => b.totalPoints - a.totalPoints);
          this.leaderboardEntries.set(sortedData);
          this.isLoading.set(false);
        },
        error: (err) => {
          console.error('Failed to load leaderboard', err);
          this.isLoading.set(false);
        }
      });
  }
}
