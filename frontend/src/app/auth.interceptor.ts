// pune automat token-ul pe orice request

import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // --- DEBUGGING: Verificăm ce se întâmplă ---
  // console.log('Interceptor [DEBUG]: Request către:', req.url);
  // console.log('Interceptor [DEBUG]: Token găsit:', token);

  if (token) {
    // console.log('Interceptor [DEBUG]: Atașez header-ul Authorization!');
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }
  // else {
  //   console.warn('Interceptor [WARN]: Nu am găsit token! Trimit cererea fără auth.');
  // }

  return next(req);
};
