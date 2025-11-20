import { Component, ChangeDetectionStrategy, signal, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

import { LoginFormComponent } from './login-form/login-form.component';
import { AuthService } from '../../services/auth.service';
import {SignupFormComponent} from './signup-form/singup-form.component';
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
  templateUrl: './auth-container.component.html',
  styleUrl: './auth-common.css', // Presupunem path-ul corect relativ
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AuthContainerComponent {
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
      next: (user) => {
        this.toastEvent.emit({ message: `Cont creat! Bine ai venit, ${user.username}!`, type: 'success' });
        this.isLoginMode.set(true);
      },
      error: (err) => {
        const message = err.errors ? 'Verifică datele introduse. Username-ul sau email-ul pot fi deja folosite.' : (err.message || 'Eroare de server.');
        this.toastEvent.emit({ message: `Înregistrare eșuată: ${message}`, type: 'error' });
      }
    });
  }

  handleLogin(credentials: any) {
    this.isLoading.set(true);
    this.authService.login(credentials).pipe(
      finalize(() => this.isLoading.set(false))
    ).subscribe({
      next: (allUsers) => {
        try {
          const user = this.authService.simulateLoginCheck(credentials, allUsers);
          this.toastEvent.emit({ message: `Autentificare reușită! Bine ai revenit!`, type: 'success' });
          this.authSuccess.emit(user);
        } catch (error: any) {
          this.toastEvent.emit({ message: `Eșec autentificare: ${error.message}`, type: 'error' });
        }
      },
      error: (err) => {
        this.toastEvent.emit({ message: `Eroare de server la login.`, type: 'error' });
      }
    });
  }
}
