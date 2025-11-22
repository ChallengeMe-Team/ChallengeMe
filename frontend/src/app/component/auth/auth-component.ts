import { Component, ChangeDetectionStrategy, signal, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

import { LoginFormComponent } from './login-form/login-form-component';
import { SignupFormComponent } from './signup-form/signup-form-component';
import { AuthService } from '../../services/auth.service';
import { LucideAngularModule, LUCIDE_ICONS, LucideIconProvider, Loader2 } from 'lucide-angular';

@Component({
  selector: 'app-auth-container',
  standalone: true,
  imports: [
    CommonModule,
    LoginFormComponent,
    SignupFormComponent,
    HttpClientModule,
    LucideAngularModule
  ],
  providers: [
    { provide: LUCIDE_ICONS, useValue: new LucideIconProvider({ Loader2 }) }
  ],
  templateUrl: './auth-component.html',
  styleUrl: './auth-component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AuthComponent {
  // Emite evenimente către AppComponent (părinte) pentru a afișa Toast-ul
  @Output() toastEvent = new EventEmitter<{ message: string, type: 'success' | 'error' }>();
  @Output() authSuccess = new EventEmitter<any>();

  isLoginMode = signal(true); // true = Login, false = Signup
  isLoading = signal(false);

  private authService = inject(AuthService);

  toggleMode() {
    this.isLoginMode.update(mode => !mode);
  }

  handleSignup(data: any) {
    this.isLoading.set(true);

    this.authService.signup(data).pipe(
      finalize(() => this.isLoading.set(false))
    ).subscribe({
      next: () => {
        // Succes: Backend-ul returneaza string simplu sau 200 OK
        this.toastEvent.emit({ message: `Cont creat cu succes! Te poți autentifica.`, type: 'success' });
        // Comutam automat pe modul Login
        this.isLoginMode.set(true);
      },
      error: (err) => {
        // Gestionare erori (ex: Username taken)
        // Verificam daca backend-ul a trimis un mesaj de eroare in body
        const errorMessage = typeof err.error === 'string' ? err.error : 'Eroare la înregistrare. Verifică datele.';
        this.toastEvent.emit({ message: errorMessage, type: 'error' });
      }
    });
  }

  handleLogin(credentials: any) {
    this.isLoading.set(true);

    this.authService.login(credentials).pipe(
      finalize(() => this.isLoading.set(false))
    ).subscribe({
      next: (response) => {
        // Succes: Avem token si user
        // response.user vine din AuthController modificat
        const user = response.user;

        this.toastEvent.emit({
          message: `Autentificare reușită! Bine ai revenit, ${user.username}!`,
          type: 'success'
        });

        // Emitem userul catre parinte (daca e nevoie de redirect sau alte actiuni)
        this.authSuccess.emit(user);
      },
      error: (err) => {
        // Eroare 401 sau altele
        console.error('Login error:', err);
        this.toastEvent.emit({
          message: `Email sau parolă incorecte.`,
          type: 'error'
        });
      }
    });
  }
}
