import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LeaderboardEntry, LeaderboardRange } from '../models/leaderboard.model';

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/leaderboard';

  /**
   * Aduce leaderboard-ul filtrat după range.
   * Exemplu URL: /api/leaderboard?range=WEEKLY
   */
  getLeaderboard(range: LeaderboardRange = LeaderboardRange.ALL_TIME): Observable<LeaderboardEntry[]> {
    return this.http.get<LeaderboardEntry[]>(`${this.apiUrl}?range=${range}`);
  }
}
