import { Routes } from '@angular/router';
import { HomeComponent } from './component/pages/home/home-component';
import { ChallengesComponent } from './component/pages/challenges/challenges-component';
import { LeaderboardComponent } from './component/pages/leaderboard/leaderboard-component';
import {AuthContainerComponent} from './component/auth/auth-container.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },      // pagina principală
  { path: 'challenges', component: ChallengesComponent },
  { path: 'leaderboard', component: LeaderboardComponent },
  {path: 'auth', component: AuthContainerComponent},
  { path: '**', redirectTo: '' }                    // fallback pentru rute necunoscute
];


