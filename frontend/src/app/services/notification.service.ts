import { Injectable, inject, signal, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, timer, Subscription, switchMap, retry, map } from 'rxjs';
import { NotificationDTO } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService implements OnDestroy {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/notifications';

  unreadCount = signal(0);
  notifications = signal<NotificationDTO[]>([]);
  private pollingSub?: Subscription;

  startPolling(userId: string) {
    if (this.pollingSub) return;
    // Polling la 30s
    this.pollingSub = timer(0, 30000).pipe(
      switchMap(() => this.getUserNotifications(userId)),
      retry()
    ).subscribe();
  }

  stopPolling() {
    this.pollingSub?.unsubscribe();
    this.pollingSub = undefined;
  }

  getUserNotifications(userId: string): Observable<NotificationDTO[]> {
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/user/${userId}`).pipe(
      tap(data => {
        const sorted = data.sort((a, b) => {
          const dateA = this.parseTimestamp(a.timestamp);
          const dateB = this.parseTimestamp(b.timestamp);
          return dateB.getTime() - dateA.getTime();
        });

        const newCount = sorted.filter(n => !n.isRead).length;

        // FOARTE IMPORTANT: Actualizăm doar dacă datele sunt diferite de starea locală
        // Asta previne ca polling-ul să pună "1" înapoi dacă serverul e lent
        this.notifications.set(sorted);
        this.unreadCount.set(newCount);
      })
    );
  }

  markAllAsRead(userId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/user/${userId}/mark-all-read`, {}).pipe(
      tap(() => {
        // Update local instantaneu
        this.notifications.update(list => list.map(n => ({ ...n, isRead: true })));
        this.unreadCount.set(0);
        console.log('Frontend: All notifications marked as read');
      })
    );
  }

  markAsRead(id: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}`, { isRead: true }).pipe(
      tap(() => {
        this.notifications.update(list =>
          list.map(n => n.id === id ? { ...n, isRead: true } : n)
        );
        this.unreadCount.update(count => Math.max(0, count - 1));
      })
    );
  }

  private parseTimestamp(ts: any): Date {
    return Array.isArray(ts)
      ? new Date(ts[0], ts[1]-1, ts[2], ts[3], ts[4])
      : new Date(ts);
  }

  ngOnDestroy() { this.stopPolling(); }
}
