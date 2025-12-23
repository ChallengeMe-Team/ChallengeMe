import { Injectable, inject, signal, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, timer, Subscription, switchMap, retry } from 'rxjs';
import { NotificationDTO } from '../models/notification.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService implements OnDestroy {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = 'http://localhost:8080/api/notifications';

  unreadCount = signal(0);
  private pollingSub?: Subscription;

  // 1. Pornim împrospătarea automată (ex: la fiecare 30 de secunde)
  startPolling(userId: string) {
    if (this.pollingSub) return; // Evităm dublarea dacă e deja pornit

    this.pollingSub = timer(0, 30000).pipe( // 0 = pornește imediat, 30000ms = interval
      switchMap(() => this.getUserNotifications(userId)),
      retry() // Continuă chiar dacă o cerere eșuează temporar
    ).subscribe();
  }

  // 2. Oprim polling-ul (util la Logout)
  stopPolling() {
    if (this.pollingSub) {
      this.pollingSub.unsubscribe();
      this.pollingSub = undefined;
    }
  }

  getUserNotifications(userId: string): Observable<NotificationDTO[]> {
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/user/${userId}`).pipe(
      tap(notifications => {
        const count = notifications.filter(n => !n.isRead).length;
        this.unreadCount.set(count);
      })
    );
  }

  markAsRead(notificationId: string): Observable<NotificationDTO> {
    return this.http.patch<NotificationDTO>(`${this.apiUrl}/${notificationId}`, { isRead: true }).pipe(
      tap(() => {
        this.unreadCount.update(c => Math.max(0, c - 1));
      })
    );
  }

  ngOnDestroy() {
    this.stopPolling();
  }
}
