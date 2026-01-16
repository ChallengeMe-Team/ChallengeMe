import {Component, ChangeDetectionStrategy, inject, computed, ChangeDetectorRef, effect} from '@angular/core';
import { CommonModule } from '@angular/common';
import {NavigationEnd, Router, RouterOutlet} from '@angular/router';
import { NavbarComponent } from './component/navbar/navbar-component';
import { ChallengeFormComponent } from './component/forms/challenge-form/challenge-form';
import { ToastComponent } from './shared/toast/toast-component';
import { AuthComponent } from './component/auth/auth-component';
import { AuthService } from './services/auth.service';
import {filter} from 'rxjs';
import { NotificationService } from './services/notification.service';

/**
 * Implementation: Standalone Component with Reactive Signals and Effects.
 * Core Focus: Managing global UI layouts (Navbar), background synchronization
 * (Polling), and centralized notification delivery (Toasts).
 */
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
  private notificationService = inject(NotificationService);

  /** * Global Signal Listener:
   * Uses an 'effect' to monitor the authentication state.
   * - Login: Triggers background notification polling.
   * - Logout: Terminates polling to save resources.
   */
  constructor() {
    effect(() => {
      const user = this.authService.currentUser();
      if (user) {
        console.log('User logged in, starting notification polling...');
        this.notificationService.startPolling(user.id);
      } else {
        this.notificationService.stopPolling();
      }
    });
  }

  private router = inject(Router);

  private cdr = inject(ChangeDetectorRef);

  // --- STATE PROPERTIES ---
  isLoggedIn = computed(() => !!this.authService.currentUser());
  showNavbar: boolean = false;
  isFormVisible = false;
  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  /**
   * Manages layout visibility based on the active route.
   * Logic: Hides the Navbar when the user is on the '/auth' page to
   * maintain a clean login experience.
   */
  ngOnInit(): void {
    this.showNavbar = !this.router.url.startsWith('/auth');

    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.showNavbar = !event.urlAfterRedirects.startsWith('/auth');
    });
  }

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

  /**
   * Triggers the global feedback system.
   * Note: Manually invokes 'ChangeDetectorRef.detectChanges()' to ensure the
   * toast renders immediately within the OnPush change detection strategy.
   *
   */
  showToast(message: string, type: 'success' | 'error') {
    this.toastMessage = message;
    this.toastType = type;
    this.toastVisible = true;

    this.cdr.detectChanges();

    setTimeout(() => {
      this.toastVisible = false;
      this.cdr.detectChanges();
    }, 3000);
  }

  /**
   * Dynamically hooks into components loaded via the RouterOutlet.
   * Used specifically to bridge events from AuthComponent to the global
   * toast system.
   */
  onActivate(componentRef: any) {
    if (componentRef instanceof AuthComponent) {
      if (componentRef.toastEvent) {
        componentRef.toastEvent.subscribe((event: { message: string; type: "success" | "error"; }) => {
          this.showToast(event.message, event.type);
        });
      }

      if (componentRef.authSuccess) {
        componentRef.authSuccess.subscribe(() => {
          this.router.navigate(['/']);
        });
      }
    }
  }
}
