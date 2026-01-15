import { ApplicationConfig, APP_INITIALIZER, importProvidersFrom } from '@angular/core';
import {provideRouter, withComponentInputBinding, withInMemoryScrolling} from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './auth.interceptor';
import { provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';

// --- IMPORT PENTRU COMPATIBILITATE LUCIDE ---
import { LucideAngularModule, Trophy, Award, Star, Shield, Medal } from 'lucide-angular';

import { AuthService } from './services/auth.service';

export function initializeApp(authService: AuthService): () => Promise<void> {
  return () => authService.initializeSession();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),

    // Configurarea routerului
    provideRouter(routes, withInMemoryScrolling({
      scrollPositionRestoration: 'enabled'
    })),

    // Configurarea clientului HTTP
    provideHttpClient(
      withFetch(),
      withInterceptors([authInterceptor])
    ),

    importProvidersFrom(
      LucideAngularModule.pick({ Trophy, Award, Star, Shield, Medal })
    ),

    {
      provide: APP_INITIALIZER,
      useFactory: initializeApp,
      deps: [AuthService],
      multi: true,
    }
  ]
};
