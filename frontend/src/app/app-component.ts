import { Component, ChangeDetectionStrategy, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './component/navbar/navbar-component';
import { ChallengeFormComponent } from './component/forms/challenge-form/challenge-form';
import { ToastComponent } from './shared/toast/toast-component';
import { AuthComponent } from './component/auth/auth-component';
import { AuthService } from './services/auth.service';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    ChallengeFormComponent,
    ToastComponent,
    RouterOutlet
  ],
  templateUrl: './app-component.html',
  styleUrls: ['./app-component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  isLoggedIn = computed(() => !!this.authService.currentUser());

  isFormVisible = false;
  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  openForm() {
    this.isFormVisible = true;
  }

  closeForm() {
    this.isFormVisible = false;
  }

  handleSubmit(challenge: any) {
    console.log('Challenge created:', challenge);
    this.showToast('Challenge created successfully!', 'success');
    this.closeForm();
  }

  showToast(message: string, type: 'success' | 'error') {
    this.toastMessage = message;
    this.toastType = type;
    this.toastVisible = true;
    setTimeout(() => this.toastVisible = false, 3000);
  }

  onActivate(componentRef: any) {
    if (componentRef instanceof AuthComponent) {
      if (componentRef.toastEvent) {
        componentRef.toastEvent.subscribe((event: { message: string; type: "success" | "error"; }) => {
          this.showToast(event.message, event.type);
        });
      }

      if (componentRef.authSuccess) {
        componentRef.authSuccess.subscribe(() => {
          // După login, rutăm către Home
          this.router.navigate(['/']);
        });
      }
    }
  }
}
