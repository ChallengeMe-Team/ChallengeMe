import { Component, ChangeDetectionStrategy, signal, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
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
        this.toastEvent.emit({ message: `Cont creat cu succes! Te poți autentifica.`, type: 'success' });
        this.isLoginMode.set(true);
      },
      error: (err) => {
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
        const user = response.user;
        this.toastEvent.emit({
          message: `Autentificare reușită! Bine ai revenit, ${user.username}!`,
          type: 'success'
        });
        this.authSuccess.emit(user);
      },
      error: (err) => {
        console.error('Login error:', err);
        this.toastEvent.emit({
          message: `Email sau parolă incorecte.`,
          type: 'error'
        });
      }
    });
  }
}
