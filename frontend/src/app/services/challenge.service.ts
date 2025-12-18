import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Challenge } from '../component/pages/challenges/challenge.model';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {
  private http = inject(HttpClient);

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

    return this.http.post<any>(`/api/user-challenges/${challengeId}/accept`, payload);
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


  getChallengesByStatus(userId: string, status: 'RECEIVED' | 'ACCEPTED'): Observable<any[]> {
    return this.http.get<any[]>(`${this.challengeUserUrl}/user/${userId}/status/${status}`);
  }

  updateChallengeStatus(challengeId: string, userId: string, status: 'ACCEPTED' | 'DECLINED'): Observable<any> {
    return this.http.put(`${this.challengeUserUrl}/status`, {
      challengeId,
      userId,
      status
    });
  }
}
