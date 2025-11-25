import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Challenge } from '../component/pages/challenges/challenge.model';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {
  private http = inject(HttpClient);

  // URL-ul trebuie să fie complet către portul 8080 al backend-ului
  private apiUrl = 'http://localhost:8080/api/challenges';

  constructor() { }

  // Metoda pentru a lua lista (folosită în tabel)
  getAllChallenges(): Observable<Challenge[]> {
    return this.http.get<Challenge[]>(this.apiUrl);
  }

  createChallenge(challenge: any): Observable<Challenge> {
    return this.http.post<Challenge>(this.apiUrl, challenge);
  }
}
