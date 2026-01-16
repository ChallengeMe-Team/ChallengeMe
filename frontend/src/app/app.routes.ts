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

/**
 * Implementation: Declarative Routing with Functional Guards.
 * Core Focus: Orchestrating user flow and enforcing Role-Based Access Control (RBAC)
 * via state-aware logic.
 */

/**
 * A reverse-security guard. It ensures that authenticated users cannot access
 * the login/signup pages (AuthComponent), redirecting them to the dashboard instead.
 *
 */
const guestGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  if (authService.isLoggedIn()) {
    return router.createUrlTree(['/']);
  }
  return true;
};
export const routes: Routes = [
  /**
   * Primary Dashboard (Root)
   * Protected by authGuard: Requires a valid JWT session.
   */
  {
    path: '',
    component: HomeComponent,
    canActivate: [authGuard]
  },

  /** Marketplace: View all available quests. */
  {
    path: 'challenges',
    component: ChallengesComponent,
    canActivate: [authGuard]
  },

  /** User Progression: Track personal active quests. */
  { path: 'my-challenges',
    component: MyChallengesComponent,
    canActivate: [authGuard]
  },

  /** Social Competitive: Global rankings. */
  {
    path: 'leaderboard',
    component: LeaderboardComponent,
    canActivate: [authGuard] // Protejat
  },

  /** Authentication Gateway: Accessible only to unauthenticated users. */
  {
    path: 'auth',
    component: AuthComponent,
    canActivate: [guestGuard]
  },

  /** Identity & Social Routes */
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
    redirectTo: '',
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

  /** Fallback: Redirects invalid paths to the Root/Home context. */
  {path: '**', redirectTo: ''}
];
