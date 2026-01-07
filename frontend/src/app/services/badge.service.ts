import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Badge } from '../models/badge.model';

@Injectable({
  providedIn: 'root'
})
export class BadgeService {
  private apiUrl = 'http://localhost:8080/api/badges'; // Verifică portul (8080 sau 8081)

  constructor(private http: HttpClient) {}

  getAll(): Observable<Badge[]> {
    return this.http.get<Badge[]>(this.apiUrl);
  }

  getUserBadges(username: string): Observable<Badge[]> {
    return this.http.get<Badge[]>(`${this.apiUrl}/user/${username}`);
  }
}
