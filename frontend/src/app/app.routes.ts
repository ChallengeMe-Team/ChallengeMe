import { Routes } from '@angular/router';
import { HomeComponent } from './component/pages/home/home-component';
import { ChallengesComponent } from './component/pages/challenges/challenges-component';
import { LeaderboardComponent } from './component/pages/leaderboard/leaderboard-component';
import { AuthComponent } from './component/auth/auth-component';

export const routes: Routes = [
  // 1. When entering the site, automatically redirects to /auth
  { path: '', redirectTo: 'auth', pathMatch: 'full' },

  // 2. Definim ruta pentru Login
  { path: 'auth', component: AuthComponent },

  // 3. Definim ruta pentru Home (trebuie să aibă cale separată acum)
  { path: 'home', component: HomeComponent },

  { path: 'challenges', component: ChallengesComponent },
  { path: 'leaderboard', component: LeaderboardComponent },

  // Fallback
  { path: '**', redirectTo: 'auth' }
];
