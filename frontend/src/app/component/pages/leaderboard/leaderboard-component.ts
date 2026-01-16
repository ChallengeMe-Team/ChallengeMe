import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AuthService} from '../../../services/auth.service';
import {LeaderboardService} from '../../../services/leaderboard.service';
import {LeaderboardEntry, LeaderboardRange} from '../../../models/leaderboard.model';

/**
 * This component manages the competitive ranking interface, orchestrating the
 * data flow between global user performance and temporal filters.
 * * * Key Architecture:
 * - Signal-based State: Manages leaderboard entries and active time ranges reactively.
 * - Computed Dependencies: Derives the current user's rank and visibility status automatically
 * whenever the source signals change.
 * - Business Logic: Handles data sorting and temporal filtering (Weekly/Monthly/All-time).
 */
@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './leaderboard-component.html',
  styleUrls: ['./leaderboard-component.css']
})
export class LeaderboardComponent implements OnInit {
  // Functional Injection of core services
  private authService = inject(AuthService);
  private leaderboardService = inject(LeaderboardService);

  /** Signal reference to the currently authenticated user. */
  currentUser = this.authService.currentUser;

  // --- COMPONENT STATE (SIGNALS) ---

  /** Signal containing the master list of ranked entries fetched from the backend. */
  leaderboardEntries = signal<LeaderboardEntry[]>([]);

  /** Signal tracking the currently active time frame (ALL_TIME by default). */
  currentRange = signal<LeaderboardRange>(LeaderboardRange.ALL_TIME);

  /** Loading flag to manage UI feedback during asynchronous HTTP requests. */
  isLoading = signal<boolean>(false);

  /** Exposing the Enum to the template to facilitate type-safe range switching. */
  public RangeType = LeaderboardRange;

  /**
   * Computed: myRankData
   * -------------------
   * Automatically extracts the logged-in user's entry from the leaderboard list.
   * This updates reactively if either the user or the list changes.
   * @returns The LeaderboardEntry for the current user or null if not found.
   */
  myRankData = computed(() => {
    const user = this.currentUser();
    const list = this.leaderboardEntries();

    if (!user || list.length === 0) return null;

    // Search for the entry matching the authenticated username
    return list.find(entry => entry.username === user.username) || null;
  });

  imageErrorFooter = false;

  /**
   * Computed: isUserInTopList
   * ------------------------
   * Determines if the current user is visible within the primary "Top List" (First 10 ranks).
   * Used to conditionally hide the "Sticky Footer" to avoid UI redundancy.
   * @returns true if rank is <= 10, otherwise false.
   */
   isUserInTopList = computed(() => {
    const myData = this.myRankData();
    if (!myData) return false;
    // If user is within the top 10, they are considered visible in the main view
    return myData.rank <= 10;
  });

  /**
   * Lifecycle Hook: Initialization
   * Executes the initial data fetch upon component startup.
   */
  ngOnInit() {
    this.loadData();
  }

  /**
   * Method: setRange
   * ---------------
   * Updates the temporal filter and triggers a data refresh.
   * Used for switching between Weekly, Monthly, and All-Time leaderboards.
   * @param range The LeaderboardRange selected by the user.
   */
  setRange(range: LeaderboardRange) {
    this.currentRange.set(range);
    this.loadData();
  }

  /**
   * Method: loadData
   * ----------------
   * Orchestrates the API call to the LeaderboardService.
   * Includes post-fetch sorting to ensure the highest XP earners are prioritized at rank 1.
   */
  loadData() {
    this.isLoading.set(true);
    this.leaderboardService.getLeaderboard(this.currentRange())
      .subscribe({
        next: (data: LeaderboardEntry[]) => {
          // Client-side sorting: Highest points first
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
