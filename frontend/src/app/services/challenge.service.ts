import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Challenge } from '../models/challenge.model';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {
  private http = inject(HttpClient);

  // URL-urile API
  private readonly baseUrl = 'http://localhost:8080/api';
  private apiUrl = 'http://localhost:8080/api/challenges';
  private challengeUserUrl = 'http://localhost:8080/api/challenge-users';

  isCreateModalOpen = signal(false);

  constructor() { }

  // --- CHALLENGE DEFINITIONS (CRUD Admin/User) ---

  getAllChallenges(): Observable<Challenge[]> {
    return this.http.get<Challenge[]>(this.apiUrl);
  }

  getUserChallenges(username: string): Observable<Challenge[]> {
    return this.http.get<Challenge[]>(`${this.apiUrl}/user/${username}`);
  }

  createChallenge(challenge: any): Observable<Challenge> {
    return this.http.post<Challenge>(this.apiUrl, challenge);
  }

  updateChallenge(id: string, data: any) {
    return this.http.put(`${this.apiUrl}/${id}`, data);
  }

  deleteChallenge(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // --- CHALLENGE USER LINKS (Assign/Accept/Complete) ---

  acceptChallenge(challengeId: string, startDate: string, deadline: string | null) {
    const payload = {
      status: 'ACCEPTED',
      startDate: startDate,
      targetDeadline: deadline
    };
    return this.http.post<any>(`${this.challengeUserUrl}/${challengeId}/accept`, payload);
  }

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

  // Metoda pentru a refuza (sterge) o invitatie
  refuseChallenge(challengeUserId: string): Observable<void> {
    return this.http.delete<void>(`${this.challengeUserUrl}/${challengeUserId}`);
  }

  updateChallengeUser(challengeUserId: string, data: any): Observable<any> {
    // SCHIMBĂ URL-ul și Metoda (din PATCH în PUT)
    // Trebuie să meargă la /api/challenges/{id}/status conform ChallengeController.java
    return this.http.put(`${this.apiUrl}/${challengeUserId}/status`, data);
  }

  // --- METODA CARE LIPSEA (Fix pentru eroare) ---
  // Aceasta metoda sterge relatia dintre user si challenge (reset progress)
  deleteChallengeUser(challengeUserId: string): Observable<void> {
    return this.http.delete<void>(`${this.challengeUserUrl}/${challengeUserId}`);
  }
}
