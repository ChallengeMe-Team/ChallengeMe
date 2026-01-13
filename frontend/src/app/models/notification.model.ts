export interface NotificationDTO {
  id: string;
  userId: string;
  message: string;
  type: 'CHALLENGE' | 'BADGE' | 'SYSTEM';
  isRead: boolean;
  timestamp: any; // Folosim 'any' pentru a evita erorile de parsare de la Java array
}
