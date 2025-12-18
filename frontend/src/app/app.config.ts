import { ApplicationConfig, APP_INITIALIZER } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './auth.interceptor'; // Asigură-te că path-ul este corect
import { provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';


// --- NOU: IMPORT PENTRU SERVICIUL DE AUTENTIFICARE ---
import { AuthService } from './services/auth.service';

// --- NOU: FUNCTIA FACTORY PENTRU INITIALIZARE ---
// Această funcție apelează logica de restaurare a sesiunii din AuthService și o returnează ca Promise.
export function initializeApp(authService: AuthService): () => Promise<void> {
  return () => authService.initializeSession();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),

    // Configurarea routerului
    provideRouter(routes, withComponentInputBinding()),

    // Configurarea clientului HTTP cu Interceptorul nostru
    provideHttpClient(
      withFetch(),
      withInterceptors([authInterceptor])
    ),

    // ----------------------------------------------------------------------
    // BLOCUL CRUCIAL: APP_INITIALIZER
    // Rulează initializeApp (care apelează initializeSession) înainte de a randa componentele
    // ----------------------------------------------------------------------
    {
      provide: APP_INITIALIZER,
      useFactory: initializeApp,
      deps: [AuthService], // Angular injectează automat AuthService
      multi: true,
    }
  ]
};
