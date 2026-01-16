import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Badge } from '../models/badge.model';

/**
 * A singleton service responsible for retrieving achievement data.
 * It facilitates the display of both the global badge library and individual
 * user progress.
 * * * Key Technical Aspects:
 * - REST Integration: Performs GET requests to synchronized endpoints for
 * aggregate and filtered data.
 * - Type Safety: Leverages TypeScript Generics with the 'Badge' model to
 * ensure strict data structure adherence during HTTP streaming.
 */
@Injectable({
  providedIn: 'root'
})
export class BadgeService {
  /** The base URL for badge-related operations on the Spring Boot backend. */
  private apiUrl = 'http://localhost:8080/api/badges';

  constructor(private http: HttpClient) {}

  /**
   * Retrieves the master list of all available badges in the system.
   * Used to populate the "Locked/Available" section of the Badge Gallery.
   * @returns An Observable stream containing an array of static Badge definitions.
   */
  getAll(): Observable<Badge[]> {
    return this.http.get<Badge[]>(this.apiUrl);
  }

  /*** -------------------------------
   * Fetches the specific collection of badges unlocked by a unique user.
   * This logic is critical for personal profile rendering and the
   * "New Badge" comparison logic in MyChallenges.
   * @param username The identifier for the targeted user profile.
   * @returns An Observable stream containing the user's earned achievements.
   */
  getUserBadges(username: string): Observable<Badge[]> {
    return this.http.get<Badge[]>(`${this.apiUrl}/user/${username}`);
  }
}
