import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, of } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  // URL-ul Backend-ului
  private apiUrl = 'http://localhost:8080/api/auth';

  // Cheia pentru LocalStorage
  private readonly TOKEN_KEY = 'auth-token';

  // Signal pentru a tine minte userul curent in toata aplicatia (Reactive)
  currentUser = signal<any>(null);

  constructor() {
    // La pornirea aplicatiei (refresh), incercam sa recuperam userul

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
    // Optional: Aici poti face redirect catre Home
    // this.router.navigate(['/']);
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
  public initializeSession(): Promise<void> {
    if (!this.getToken()) {
      return Promise.resolve();
    }

    // PAS 2: Returneaza un Promise care se rezolva dupa ce se termina request-ul
    return new Promise((resolve) => {
      this.http.get<any>(`${this.apiUrl}/me`).pipe(
        catchError((error) => {
          this.logout();
          return of(null);
        })
      ).subscribe({
        next: (user: any | null) => {
          if (user) {
            this.currentUser.set(user);
          }
        },
        // NU e necesara tratare eroare, complete se ocupa de resolve
        complete: () => {
          resolve(); // GARANTEAZA CA APLICATIA PORNESTE
        }
      });
    });
  }
}
