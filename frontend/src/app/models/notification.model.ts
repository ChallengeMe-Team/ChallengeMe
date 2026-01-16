/**
 * This interface defines the payload structure for all system alerts.
 * It supports multi-channel notification types and includes flexibility
 * for handling various timestamp formats originating from the backend.
 */
export interface NotificationDTO {
  /** Unique identifier (UUID) used for marking specific notifications as read. */
  id: string;

  /** The ID of the recipient user. Used for security scoping and delivery. */
  userId: string;

  /** The human-readable text body of the alert (e.g., "User X invited you to a challenge"). */
  message: string;

  /** * Discriminator for notification category:
   * - CHALLENGE: Social invites or quest updates.
   * - BADGE: Alerts for newly unlocked achievements.
   * - SYSTEM: Global maintenance or security alerts.
   */
  type: 'CHALLENGE' | 'BADGE' | 'SYSTEM';

  /** State flag indicating if the user has interacted with/viewed the notification. */
  isRead: boolean;

  /** * The moment the notification was generated.
   * * Technical Note: Typed as 'any' to facilitate the parsing of Java
   * LocalDateTime arrays [YYYY, MM, DD, ...] without runtime errors.
   */
  timestamp: any;
}
