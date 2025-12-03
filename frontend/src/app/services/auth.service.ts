import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  // URL-ul Backend-ului
  private apiUrl = 'http://localhost:8080/api/auth';

  // Cheia pentru LocalStorage
  private readonly TOKEN_KEY = 'auth-token';

  // Signal pentru a tine minte userul curent in toata aplicatia (Reactive)
  currentUser = signal<any>(null);

  constructor() {
    // La pornirea aplicatiei (refresh), incercam sa recuperam userul
    this.fetchCurrentUser();
  }

  // --- LOGIN ---
  login(credentials: { emailOrUsername: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: any) => {
        // Salvam token-ul primit in LocalStorage
        if (response.token) {
          localStorage.setItem(this.TOKEN_KEY, response.token);
        }

        // Actualizam userul in starea aplicatiei
        // Backend-ul returneaza acum: { token: "...", user: { username: "...", role: "..." } }
        if (response.user) {
          this.currentUser.set(response.user);
        }
      })
    );
  }

  // --- SIGNUP ---
  signup(user: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, user);
  }

  // --- LOGOUT ---
  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/']);
  }

  // --- UTILITARE ---
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken(); // Returneaza true daca exista token
  }

  // --- PERSISTENTA (REFRESH) ---
  // Aceasta metoda este apelata in constructor pentru a verifica daca token-ul
  // salvat este inca valid si pentru a recuceri datele userului.
  private fetchCurrentUser() {
    if (!this.getToken()) return;

    this.http.get(`${this.apiUrl}/me`).pipe(
      catchError(() => {
        // Daca token-ul este expirat sau invalid (401), facem logout automat
        this.logout();
        return of(null);
      })
    ).subscribe((user) => {
      if (user) {
        this.currentUser.set(user);
      }
    });
  }
}
