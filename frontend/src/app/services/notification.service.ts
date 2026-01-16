import { Injectable, inject, signal, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, timer, Subscription, switchMap, retry, map } from 'rxjs';
import { NotificationDTO } from '../models/notification.model';

/**
 * A singleton service managing the asynchronous delivery and state of user alerts.
 * It combines HTTP polling with Angular Signals for a reactive notification experience.
 *
 * * * Key Technical Aspects:
 * - Reactive Polling: Implements a 30-second interval using RxJS 'timer' and 'switchMap'
 * to fetch updates without manual refresh.
 * - State Management: Uses 'signal' to track the total unread count and the global
 * notification list, ensuring UI elements like badge counters stay synced.
 * - Data Normalization: Includes specialized timestamp parsing to handle Java
 * LocalDateTime arrays vs ISO standard strings.
 */
@Injectable({ providedIn: 'root' })
export class NotificationService implements OnDestroy {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/notifications';

  /** Signal representing the number of notifications currently flagged as !isRead. */
  unreadCount = signal(0);

  /** Signal containing the full list of notifications for the current user. */
  notifications = signal<NotificationDTO[]>([]);

  private pollingSub?: Subscription;

  /**
   * Initiates the background synchronization process.
   * - Uses 'timer(0, 30000)' for an immediate first call followed by 30s intervals.
   * - 'switchMap' ensures that if a previous request is still pending when a new one starts,
   * the older request is canceled.
   */
  startPolling(userId: string) {
    if (this.pollingSub) return;
    this.pollingSub = timer(0, 30000).pipe(
      switchMap(() => this.getUserNotifications(userId)),
      retry()
    ).subscribe();
  }

  stopPolling() {
    this.pollingSub?.unsubscribe();
    this.pollingSub = undefined;
  }

  /**
   * Retrieves alerts from the backend and performs post-processing:
   * 1. Chronological Sorting: Newest alerts appear at the top.
   * 2. Unread Calculation: Updates the global counter based on 'isRead' status.
   * 3. Signal Sync: Pushes sorted data to the application's reactive state.
   */
  getUserNotifications(userId: string): Observable<NotificationDTO[]> {
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/user/${userId}`).pipe(
      tap(data => {
        const sorted = data.sort((a, b) => {
          const dateA = this.parseTimestamp(a.timestamp);
          const dateB = this.parseTimestamp(b.timestamp);
          return dateB.getTime() - dateA.getTime();
        });

        const newCount = sorted.filter(n => !n.isRead).length;

        this.notifications.set(sorted);
        this.unreadCount.set(newCount);
      })
    );
  }

  /*** Performs a bulk update on the backend and optimistically resets all local
   * notification states to 'isRead: true'.
   */
  markAllAsRead(userId: string): Observable<void> {
    console.log('Serviciul execută POST către:', `${this.apiUrl}/user/${userId}/mark-all-read`);

    return this.http.post<void>(`${this.apiUrl}/user/${userId}/mark-all-read`, {}).pipe(
      tap(() => {
        this.notifications.update(list => list.map(n => ({ ...n, isRead: true })));
        this.unreadCount.set(0);
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

  /**
   * Utility to normalize Java backend date arrays [YYYY, MM, DD, ...] into
   * standard JavaScript Date objects for comparison and display.
   */
  private parseTimestamp(ts: any): Date {
    return Array.isArray(ts)
      ? new Date(ts[0], ts[1]-1, ts[2], ts[3], ts[4])
      : new Date(ts);
  }

  /** Ensures background timers are terminated when the component is destroyed to prevent memory leaks. */
  ngOnDestroy() { this.stopPolling(); }
}
