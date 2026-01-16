import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LeaderboardEntry, LeaderboardRange } from '../models/leaderboard.model';

/**
 * A singleton service that interfaces with the ranking engine of the backend.
 * It provides time-scoped performance data to facilitate a competitive user experience.
 *
 * * * Key Technical Aspects:
 * - Temporal Querying: Uses URL query parameters to request specific data subsets
 * based on time ranges (Weekly, Monthly, All-Time).
 * - Type Safety: Implements the 'LeaderboardEntry' interface to ensure the
 * response data structure is strictly validated.
 * - Default State Management: Handles fallback logic by defaulting to 'ALL_TIME'
 * rankings if no range is specified.
 */
@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {
  /** The primary HTTP communication client. */
  private http = inject(HttpClient);

  /** The base endpoint for competitive ranking data. */
  private apiUrl = 'http://localhost:8080/api/leaderboard';

  /**
   * Retrieves a list of users and their respective XP totals, sorted by rank.
   * * Logic: Appends the 'range' enum value as a query string to the GET request.
   *
   * * @param range The temporal scope (e.g., WEEKLY, MONTHLY, ALL_TIME).
   * @returns An Observable stream of LeaderboardEntry objects.
   */
  getLeaderboard(range: LeaderboardRange = LeaderboardRange.ALL_TIME): Observable<LeaderboardEntry[]> {
    return this.http.get<LeaderboardEntry[]>(`${this.apiUrl}?range=${range}`);
  }
}
