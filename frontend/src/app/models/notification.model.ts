export interface NotificationDTO {
  id: string;
  message: string;
  type: string; // 'CHALLENGE_REQ', 'FRIEND_REQ', etc.
  isRead: boolean;
  timestamp: string | number[]; // Backend-ul trimite LocalDateTime, aici ajunge string ISO
}
