import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import {Observable, tap, catchError, of, lastValueFrom} from 'rxjs';

/**
 * A singleton service managing the security context and identity lifecycle.
 * It acts as the primary interface for authentication and session persistence.
 *
 * * * Key Technical Aspects:
 * - Session Pre-loading: Implements a Promise-based initialization strategy
 * compatible with Angular's APP_INITIALIZER to prevent UI "flicker".
 * - Reactive Identity: Uses Angular Signals (`currentUser`) to propagate
 * authentication changes throughout the entire component tree.
 * - Token Persistence: Handles local storage synchronization for JWT tokens
 * ensuring session continuity across browser reloads.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private apiUrl = 'http://localhost:8080/api/auth';
  private readonly TOKEN_KEY = 'auth-token';

  /** * Global Signal representing the authenticated user state.
   * Components react automatically when this signal is updated.
   */
  currentUser = signal<any>(null);

  constructor() {
  }

  /**
   * A critical boot-time procedure. It intercepts the application startup to
   * verify existing credentials.
   * 1. Token Check: If no token exists, the app boots as a 'Guest'.
   * 2. Identity Verification: If a token is present, it validates it against
   * the `/me` endpoint to recover the user profile.
   * @returns A Promise that Angular awaits before rendering the root view.
   */
  initializeSession(): Promise<void> {
    return new Promise((resolve) => {
       if (!this.getToken()) {
        resolve();
        return;
      }

      this.http.get(`${this.apiUrl}/me`).subscribe({
        next: (user) => {
          this.currentUser.set(user);
          resolve();
        },
        error: () => {
          this.logout(); // Wipe invalid/expired sessions
          resolve();
        }
      });
    });
  }

  /**
   * Authenticates the user and establishes the security context.
   * Logic: On success, it persists the JWT to localStorage and updates
   * the identity signal.
   */
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

  /** Triggers account creation via the backend API. */
  signup(user: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, user);
  }

  /**
   * Resets the application to a 'Guest' state by clearing sensitive tokens
   * and nullifying the user signal.
   */
  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    this.currentUser.set(null);
  }

  /** Returns the current raw JWT from browser storage. */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /** * Verifies if the session is active.
   * Unlike basic token checks, this ensures user data is actually loaded in memory.
   */
  isLoggedIn(): boolean {
    return !!this.currentUser();
  }

  /** Utility to retrieve the unique identifier of the logged-in user. */
  getUsername(): string | null {
    return this.currentUser()?.username || null;
  }
}
