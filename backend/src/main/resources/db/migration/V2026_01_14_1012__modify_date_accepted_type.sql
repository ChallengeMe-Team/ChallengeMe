/**
 * Purpose: Global temporal alignment across the system.
 * Description: Upgrades 'date_accepted', 'date_completed', and 'notification.timestamp'
 * to high-precision TIMESTAMP types.
 * Logic: Ensures that the entire lifecycle of a challenge and all communication
 * events are tracked with sub-second accuracy.
 */

-- 1. Lifecycle Tracking: Upgrading Acceptance and Completion precision
-- Allows for detailed analytics on how long a user takes from accepting to finishing a quest.
ALTER TABLE challenge_users
ALTER COLUMN date_accepted TYPE TIMESTAMP
USING date_accepted::timestamp;

ALTER TABLE challenge_users
ALTER COLUMN date_completed TYPE TIMESTAMP
USING date_completed::timestamp;

-- 2. Communication Tracking: Upgrading Notification alerts
-- Vital for the 'Inbox' sorting logic to ensure alerts appear in the exact order they were sent.
ALTER TABLE notifications ALTER COLUMN timestamp TYPE TIMESTAMP;