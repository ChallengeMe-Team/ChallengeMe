import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, map, of, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

interface User {
  id: string;
  username: string;
  email: string;
  points: number;
  password?: string; // Nu e trimis în DTO, dar necesar pentru simulare
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/users'; // Endpoint-ul pentru Users

  // --- Signup (Înregistrare) ---
  // Folosește POST /api/users pentru a crea un utilizator
  signup(user: any): Observable<User> {
    return this.http.post<User>(this.apiUrl, user).pipe(
      catchError(this.handleError)
    );
  }

  // --- Login (Simulare simplă) ---
  // Deoarece nu există un endpoint dedicat de Login, preluăm toți utilizatorii
  // și simulăm verificarea credențialelor pe client (soluție temporară).
  login(credentials: any): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl).pipe(
      catchError(this.handleError)
    );
  }

  // Funcție helper pentru a verifica credențialele simple
  simulateLoginCheck(credentials: any, allUsers: User[]): User {
    const user = allUsers.find(
      u => (u.email === credentials.emailOrUsername || u.username === credentials.emailOrUsername)
      // *Simulare Hack:* În mod normal am verifica parola criptată.
      // Deoarece backend-ul (UserService) nu expune parola
      // și nu putem cere toți userii cu parole, ne bazăm doar pe găsirea după username/email.
      // Un endpoint dedicat de login ar rezolva asta.
    );

    if (user) {
      return user;
    }
    throw new Error('Credențiale invalide.');
  }

  private handleError(error: HttpErrorResponse) {
    // Extrage mesajul de eroare de la GlobalExceptionHandler din backend
    if (error.error instanceof ErrorEvent) {
      // Eroare de rețea
      return throwError(() => new Error('A apărut o eroare de rețea.'));
    } else {
      // Eroare de server (4xx, 5xx)
      const errorResponse = error.error || { message: 'Eroare necunoscută.', errors: null };
      return throwError(() => errorResponse);
    }
  }
}
