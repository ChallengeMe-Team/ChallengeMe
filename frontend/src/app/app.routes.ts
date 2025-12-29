import {Routes} from '@angular/router';
import {HomeComponent} from './component/pages/home/home-component';
import {ChallengesComponent} from './component/pages/challenges/challenges-component';
import { MyChallengesComponent } from './component/pages/my-challenges/my-challenges-component';
import {LeaderboardComponent} from './component/pages/leaderboard/leaderboard-component';
import {AuthComponent} from './component/auth/auth-component';
import {authGuard} from './auth.guard';
import {inject} from '@angular/core';
import {AuthService} from './services/auth.service';
import {Router} from '@angular/router';
import {FriendsListComponent} from './component/pages/friends-list/friends-list.component';
import { SettingsComponent } from './component/pages/settings/settings-component';
import {ProfileComponent} from './component/pages/profile/profile.component';
import { BadgesPageComponent } from "./component/pages/badges-page/badges-page.component";

const guestGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  if (authService.isLoggedIn()) {
    return router.createUrlTree(['/']);
  }
  return true;
};
export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    canActivate: [authGuard] // Protejat: Dacă nu ești logat, te trimite la /auth
  },
  {
    path: 'challenges',
    component: ChallengesComponent,
    canActivate: [authGuard] // Protejat
  },
  { path: 'my-challenges',
    component: MyChallengesComponent,
    canActivate: [authGuard]
  },
  {
    path: 'leaderboard',
    component: LeaderboardComponent,
    canActivate: [authGuard] // Protejat
  },
  {
    path: 'auth',
    component: AuthComponent,
    canActivate: [guestGuard] // Accesibil doar dacă NU ești logat
  },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [authGuard]
  },
  {
    path: 'friends',
    component: FriendsListComponent,
    canActivate: [authGuard]
  },
  {
    path: 'settings',
    component: SettingsComponent,
    canActivate: [authGuard]
  },
  {
    path: 'home',
    redirectTo: '', // Te trimite la path: '' care este protejat de authGuard
    pathMatch: 'full'
  },
  {
    path: 'auth',
    component: AuthComponent,
    canActivate: [guestGuard]
  },
  { path: 'badges',
    component: BadgesPageComponent
  },
  {path: '**', redirectTo: ''} // Orice altă rută duce la Home (care va verifica authGuard)
];
