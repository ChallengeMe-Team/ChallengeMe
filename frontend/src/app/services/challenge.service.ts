import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Challenge } from '../models/challenge.model';

/**
 * The primary orchestrator for quest data. It bridges the gap between static
 * challenge definitions and the dynamic relational links between users and tasks.
 *
 * * * Key Technical Aspects:
 * - CRUD Operations: Manages the 'Challenge' entity (the blueprint of a quest).
 * - Relational Logic: Manages the 'ChallengeUser' entity (the specific instance
 * of a quest assigned to a user).
 * - State Management: Uses Angular Signals to control global UI components
 * like the creation modal.
 */
@Injectable({
  providedIn: 'root'
})
export class ChallengeService {
  private http = inject(HttpClient);

  // API Endpoint Definitions
  private readonly baseUrl = 'http://localhost:8080/api';
  private apiUrl = 'http://localhost:8080/api/challenges';
  private challengeUserUrl = 'http://localhost:8080/api/challenge-users';

  /** Signal to toggle the visibility of the Challenge Creation form across the app. */
  isCreateModalOpen = signal(false);

  constructor() { }

  // --- SECTION 1: CHALLENGE DEFINITIONS (Static Blueprint) ---

  /** Fetches all quests available in the global library. */
  getAllChallenges(): Observable<Challenge[]> {
    return this.http.get<Challenge[]>(this.apiUrl);
  }

  /** Retrieves quests authored by a specific user. */
  getUserChallenges(username: string): Observable<Challenge[]> {
    return this.http.get<Challenge[]>(`${this.apiUrl}/user/${username}`);
  }

  /** Persists a new quest definition to the database. */
  createChallenge(challenge: any): Observable<Challenge> {
    return this.http.post<Challenge>(this.apiUrl, challenge);
  }

  /** Modifies an existing quest's metadata (title, points, etc.). */
  updateChallenge(id: string, data: any) {
    return this.http.put(`${this.apiUrl}/${id}`, data);
  }

  /** Permanently removes a quest from the global library. */
  deleteChallenge(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // --- SECTION 2: CHALLENGE USER LINKS (Dynamic State) ---

  /**
   * Transitions a challenge from 'PENDING' to 'ACCEPTED' for the current user.
   * Sets the target deadlines for completion.
   */
  acceptChallenge(challengeId: string, startDate: string, deadline: string | null) {
    const payload = {
      status: 'ACCEPTED',
      startDate: startDate,
      targetDeadline: deadline
    };
    return this.http.post<any>(`${this.challengeUserUrl}/${challengeId}/accept`, payload);
  }

  /**
   * Filters a user's quest list by their current progress state.
   * Used to populate the 'Inbox' vs 'Active' dashboard tabs.
   */
  getChallengesByStatus(userId: string, status: 'RECEIVED' | 'ACCEPTED' | 'PENDING'): Observable<any[]> {
    return this.http.get<any[]>(`${this.challengeUserUrl}/user/${userId}/status/${status}`);
  }

  updateChallengeStatus(challengeId: string, userId: string, status: 'ACCEPTED' | 'DECLINED'): Observable<any> {
    return this.http.put(`${this.challengeUserUrl}/status`, {
      challengeId,
      userId,
      status
    });
  }

  /** Sends a quest invitation to another user (friend). */
  assignChallenge(challengeId: string, friendId: string) {
    return this.http.post(
      `${this.challengeUserUrl}/assign`,
      {
        challengeId,
        userId: friendId
      }
    );
  }

  getAllUserChallengeLinks(userId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.challengeUserUrl}/user/${userId}`);
  }

  /** Permanently rejects an invitation or deletes a progress link. */
  refuseChallenge(challengeUserId: string): Observable<void> {
    return this.http.delete<void>(`${this.challengeUserUrl}/${challengeUserId}`);
  }

  /**
   * The primary endpoint for state transitions (e.g., 'ACCEPTED' -> 'COMPLETED').
   * Note: This hits the ChallengeController specifically to trigger XP reward logic.
   */
  updateChallengeUser(challengeUserId: string, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${challengeUserId}/status`, data);
  }

  deleteChallengeUser(challengeUserId: string): Observable<void> {
    return this.http.delete<void>(`${this.challengeUserUrl}/${challengeUserId}`);
  }
}
