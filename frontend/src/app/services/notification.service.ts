import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { NotificationDTO } from '../models/notification.model';

// // Definim modelul direct aici pentru simplitate, sau într-un fișier separat
// export interface NotificationDTO {
//   id: string;
//   message: string;
//   type: string; // 'CHALLENGE_REQ', 'FRIEND_REQ', etc.
//   isRead: boolean;
//   createdAt: string;
//   relatedUserId?: string; // ID-ul celui care a trimis
//   relatedChallengeId?: string; // ID-ul provocării
// }

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/notifications';

  // Semnal pentru a ține count-ul actualizat în timp real în Navbar
  unreadCount = signal(0);

  // 1. Get User Notifications
  getUserNotifications(userId: string): Observable<NotificationDTO[]> {
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/user/${userId}`).pipe(
      tap(notifications => {
        // Actualizăm automat numărul de notificări necitite
        const count = notifications.filter(n => !n.isRead).length;
        this.unreadCount.set(count);
      })
    );
  }

  // 2. Mark as Read
  markAsRead(notificationId: string): Observable<NotificationDTO> {
    // Trimitem un obiect parțial, backend-ul se așteaptă la NotificationUpdateRequest
    return this.http.patch<NotificationDTO>(`${this.apiUrl}/${notificationId}`, { isRead: true }).pipe(
      tap(() => {
        // Scădem contorul local
        this.unreadCount.update(c => Math.max(0, c - 1));
      })
    );
  }
}
