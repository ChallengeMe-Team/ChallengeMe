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

  constructor() {
    // Folosim un effect pentru a monitoriza starea login-ului
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

  private cdr = inject(ChangeDetectorRef); // Adaugă această linie

  isLoggedIn = computed(() => !!this.authService.currentUser());

  showNavbar: boolean = false;

  isFormVisible = false;
  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  ngOnInit(): void {
    // Setăm starea inițială a Navbar-ului
    this.showNavbar = !this.router.url.startsWith('/auth');

    // Ascultă evenimentele de schimbare a rutei
    this.router.events.pipe(
      // Filtrăm doar evenimentele de finalizare a navigării
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      // Setează showNavbar la 'false' dacă ruta curentă începe cu '/auth'
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

  showToast(message: string, type: 'success' | 'error') {
    this.toastMessage = message;
    this.toastType = type;
    this.toastVisible = true;

    this.cdr.detectChanges(); // FORȚEAZĂ afișarea mesajului în UI

    setTimeout(() => {
      this.toastVisible = false;
      this.cdr.detectChanges(); // FORȚEAZĂ ascunderea mesajului
    }, 3000);
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
