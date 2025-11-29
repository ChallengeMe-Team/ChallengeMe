import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Challenge } from '../component/pages/challenges/challenge.model';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {
  private http = inject(HttpClient);

  // The URL must be complete and point to port 8080 of the backend
  private apiUrl = 'http://localhost:8080/api/challenges';

  constructor() { }

  // Method to get the list (used in the table)
  getAllChallenges(): Observable<Challenge[]> {
    return this.http.get<Challenge[]>(this.apiUrl);
  }

  createChallenge(challenge: any): Observable<Challenge> {
    return this.http.post<Challenge>(this.apiUrl, challenge);
  }

  updateChallenge(id: string, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, data);
  }

}

