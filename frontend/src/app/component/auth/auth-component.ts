import { Component, ChangeDetectionStrategy, signal, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';

import { LoginFormComponent } from './login-form/login-form-component';
import { SignupFormComponent } from './signup-form/signup-form-component';

import { AuthService } from '../../services/auth.service';
import { LucideAngularModule, LUCIDE_ICONS, LucideIconProvider, Loader2 } from 'lucide-angular';
import {Router} from '@angular/router';

/**
 * Smart Component that orchestrates the entire Authentication workflow.
 * It serves as a container for Login and Signup forms, managing high-level logic,
 * API communication via AuthService, and navigation.
 * * * Key Architectural Patterns:
 * - Container-Presenter Pattern: Holds the state and logic for dumb form components.
 * - Angular Signals: Utilizes reactive signals for high-performance state management (loading, view modes).
 * - ChangeDetectionStrategy.OnPush: Optimizes performance by checking changes only when inputs or signals update.
 */
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
  /** Emits notifications to the global Toast system. */
  @Output() toastEvent = new EventEmitter<{ message: string, type: 'success' | 'error' }>();
  /** Emits the user object upon successful login for parent state hydration. */
  @Output() authSuccess = new EventEmitter<any>();

  /** Signal controlling the current view: true for Login, false for Signup. */
  isLoginMode = signal(true); // true = Login, false = Signup
  /** Signal tracking asynchronous API operations to trigger global loading overlays. */
  isLoading = signal(false);

  private authService = inject(AuthService);

  /** Switches between Login and Signup modes. */
  toggleMode() {
    this.isLoginMode.update(mode => !mode);
  }

  /**
   * Orchestrates the registration process.
   * On success: Notifies the user and switches view to Login mode.
   * On error: Extracts server error messages for toast notification.
   */
  handleSignup(data: any) {
    this.isLoading.set(true);

    this.authService.signup(data).pipe(
      finalize(() => this.isLoading.set(false))
    ).subscribe({
      next: (res) => {
        this.toastEvent.emit({
          message: 'Account created successfully! You can now log in.',
          type: 'success'
        });

        this.isLoginMode.set(true);
      },
      error: (err) => {
        const errorMessage = typeof err.error === 'string' ? err.error : 'Registration failed. Please check your data.';
        this.toastEvent.emit({ message: errorMessage, type: 'error' });
      }
    });
  }

  private router = inject(Router);

  /**
   * Orchestrates the login process.
   * On success: Hydrates Auth state, triggers success toast, and navigates to the dashboard.
   */
  handleLogin(credentials: any) {
    this.isLoading.set(true);

    this.authService.login(credentials).pipe(
      finalize(() => this.isLoading.set(false))
    ).subscribe({
      next: (response) => {
        const user = response.user;
        this.toastEvent.emit({
          message: `Login successful! Welcome back, ${response.user.username}!`,
          type: 'success'
        });

        this.router.navigate(['/']);

        this.authSuccess.emit(user);
      },
      error: (err) => {
        console.error('Login error:', err);
        this.toastEvent.emit({ message: 'Invalid email/username or password.', type: 'error' });
        }
    });
  }
}
