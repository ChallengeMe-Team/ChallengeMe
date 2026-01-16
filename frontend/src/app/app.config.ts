import { ApplicationConfig, APP_INITIALIZER, importProvidersFrom } from '@angular/core';
import {provideRouter, withComponentInputBinding, withInMemoryScrolling} from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './auth.interceptor';
import { provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';

import { LucideAngularModule, Trophy, Award, Star, Shield, Medal } from 'lucide-angular';

import { AuthService } from './services/auth.service';

/**
 * Implementation: Modular Provider Factory.
 * Core Focus: Synchronizing session state before UI rendering and configuring
 * global HTTP and Routing behaviors.
 */

/**
 * A critical pre-loading factory. It ensures that the application does not
 * render the first frame until the user's authentication session is verified.
 *
 * @param authService The service responsible for JWT verification.
 * @returns A function returning a Promise to the APP_INITIALIZER.
 */
export function initializeApp(authService: AuthService): () => Promise<void> {
  return () => authService.initializeSession();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),

    /** * Optimization: Zone Change Detection
     * Reduces the number of change detection cycles by coalescing events (e.g.,
     * multiple clicks in a single tick), improving performance.
     */
    provideZoneChangeDetection({ eventCoalescing: true }),

    /** * Routing Engine:
     * - withInMemoryScrolling: Restores the user's scroll position when
     * navigating back, improving UX in long lists.
     */
    provideRouter(routes, withInMemoryScrolling({
      scrollPositionRestoration: 'enabled'
    })),

    /** * HTTP Client Configuration:
     * - withFetch: Enables the modern Browser Fetch API for better performance.
     * - withInterceptors: Injects the 'authInterceptor' to automatically
     * attach JWT tokens to every outgoing request.
     */
    provideHttpClient(
      withFetch(),
      withInterceptors([authInterceptor])
    ),

    /** * Iconography Bundle:
     * Selective icon loading (Tree-shaking) to keep the initial bundle small
     * by only picking specific trophy/medal icons.
     */
    importProvidersFrom(
      LucideAngularModule.pick({ Trophy, Award, Star, Shield, Medal })
    ),

    /** * Iconography Bundle:
     * Selective icon loading (Tree-shaking) to keep the initial bundle small
     * by only picking specific trophy/medal icons.
     */
    {
      provide: APP_INITIALIZER,
      useFactory: initializeApp,
      deps: [AuthService],
      multi: true,
    }
  ]
};
