import { Component, ChangeDetectionStrategy, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { NavbarComponent, Page } from './component/navbar/navbar-component';
import { ChallengeFormComponent } from './component/forms/challenge-form/challenge-form';
import { ToastComponent } from './shared/toast/toast-component';
import { AuthService } from './services/auth.service';
import { AuthComponent } from './component/auth/auth-component';

@Component({
  selector: 'app-component',
  standalone: true,
  imports: [
    NavbarComponent,
    CommonModule,
    ChallengeFormComponent,
    ToastComponent,
    RouterModule
  ],
  templateUrl: './app-component.html',
  styleUrls: ['./app-component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  isLoggedIn = computed(() => !!this.authService.currentUser());
  currentPage: Page = 'home';
  isFormVisible = false;

  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  onNavigate(page: Page) {
    this.currentPage = page;

    if (page === 'home') this.router.navigate(['/']);
    else if (page === 'challenges') this.router.navigate(['/challenges']);
    else if (page === 'leaderboard') this.router.navigate(['/leaderboard']);
    else if (page === 'auth') {
      this.authService.logout();
      this.router.navigate(['/auth']);
    }
  }

  openForm() { this.isFormVisible = true; }
  closeForm() { this.isFormVisible = false; }

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
      componentRef.toastEvent.subscribe((event: { message: string; type: "success" | "error"; }) => {
        this.showToast(event.message, event.type);
      });
      componentRef.authSuccess.subscribe(() => {
        this.router.navigate(['/']);
      });
    }
  }
}
