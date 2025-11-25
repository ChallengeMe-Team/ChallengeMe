import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './component/navbar/navbar-component';
import { HomeComponent } from './component/pages/home/home-component';
import { ChallengesComponent } from './component/pages/challenges/challenges-component';
import { LeaderboardComponent } from './component/pages/leaderboard/leaderboard-component';
import { ChallengeFormComponent } from './component/forms/challenge-form/challenge-form';
import { ToastComponent } from './shared/toast/toast-component';
import { AuthComponent } from './component/auth/auth-component';

// Definim tipul pentru paginile posibile
type Page = 'home' | 'challenges' | 'leaderboard' | 'create' | 'auth' | 'profile' | 'my-challenges' | 'friends' | 'settings';

@Component({
  selector: 'app-root', // Am păstrat app-root pentru a se potrivi cu index.html
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    HomeComponent,
    ChallengesComponent,
    LeaderboardComponent,
    ChallengeFormComponent,
    ToastComponent,
    AuthComponent
  ],
  templateUrl: './app-component.html',
  styleUrls: ['./app-component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
  currentPage: Page = 'auth'; // Pornim cu pagina de Auth
  isFormVisible = false;

  toastVisible = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  // Funcție pentru navigarea manuală (fără Router)
  onNavigate(page: Page) {
    this.currentPage = page;
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
    setTimeout(() => this.toastVisible = false, 3000);
  }
}
