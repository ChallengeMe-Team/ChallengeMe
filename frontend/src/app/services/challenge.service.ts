import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Challenge } from '../component/pages/challenges/challenge.model';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {
  private http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080/api';

  private apiUrl = 'http://localhost:8080/api/challenges';
  private challengeUserUrl = 'http://localhost:8080/api/challenge-users';

  isCreateModalOpen = signal(false);

  constructor() { }

  getAllChallenges(): Observable<Challenge[]> {
    return this.http.get<Challenge[]>(this.apiUrl);
  }

  acceptChallenge(challengeId: string, startDate: string, deadline: string | null) {
    const payload = {
      status: 'ACCEPTED',
      startDate: startDate,
      targetDeadline: deadline
    };

    return this.http.post<any>(`${this.challengeUserUrl}/${challengeId}/accept`, payload);
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

  // Metoda noua pentru a verifica statusul global al utilizatorului
  getAllUserChallengeLinks(userId: string): Observable<any[]> {
    // Backend-ul tău are deja repository.findByUserId(userId),
    // trebuie doar să ai un endpoint expus în Controller pentru asta.
    // Presupunând că endpoint-ul este GET /api/challenge-users/user/{userId}
    return this.http.get<any[]>(`${this.challengeUserUrl}/user/${userId}`);
  }

  // Metoda pentru a șterge invitația (Refuse)
  refuseChallenge(challengeUserId: string): Observable<void> {
    return this.http.delete<void>(`${this.challengeUserUrl}/${challengeUserId}`);
  }

  // Metoda generică de update pentru ChallengeUser (pentru a accepta invitații existente)
  updateChallengeUser(challengeUserId: string, data: any): Observable<any> {
    // PATCH sau PUT pe /api/challenge-users/{id}
    return this.http.patch(`${this.challengeUserUrl}/${challengeUserId}`, data);
  }
}
