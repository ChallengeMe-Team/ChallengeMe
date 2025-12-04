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

  private apiUrl = 'http://localhost:8080/api/auth';
  private readonly TOKEN_KEY = 'auth-token';

  currentUser = signal<any>(null);

  constructor() {
  }

  // Aceasta metoda returneaza un Promise. Angular va astepta ca acest Promise
  // sa se rezolve (resolve()) inainte sa afiseze pagina.
  initializeSession(): Promise<void> {
    return new Promise((resolve) => {
      // 1. Daca nu avem token, nu avem ce verifica.
      // Rezolvam promisiunea imediat si lasam aplicatia sa porneasca (ca Guest).
      if (!this.getToken()) {
        resolve();
        return;
      }

      // 2. Daca avem token, intrebam backend-ul cine e userul
      this.http.get(`${this.apiUrl}/me`).subscribe({
        next: (user) => {
          this.currentUser.set(user);
          resolve(); // Gata, am incarcat userul, aplicatia poate porni
        },
        error: () => {
          // Token expirat sau eroare server -> stergem tokenul
          this.logout();
          resolve(); // Gata, aplicatia porneste (dar ca Guest)
        }
      });
    });
  }

  // --- LOGIN ---
  login(credentials: { emailOrUsername: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: any) => {
        if (response.token) {
          localStorage.setItem(this.TOKEN_KEY, response.token);
        }
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
    // Nu facem redirect aici, lasam componenta sa decida
  }

  // --- UTILITARE ---
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
