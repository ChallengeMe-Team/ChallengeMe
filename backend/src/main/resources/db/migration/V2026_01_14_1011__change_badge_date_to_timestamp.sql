/**
 * Purpose: Synchronization of achievement timestamps with real-time events.
 * Description: Upgrades 'date_awarded' in the 'user_badges' table to TIMESTAMP.
 * Logic: Ensures that badge unlocks can be displayed in a unified chronological
 * activity feed alongside challenge completions.
 */

-- Converts the storage type for badge awarding events.
-- 'USING date_awarded::timestamp' migrates existing data by preserving the date
-- and initializing the time component to midnight (00:00:00).
ALTER TABLE user_badges
ALTER COLUMN date_awarded TYPE TIMESTAMP WITHOUT TIME ZONE
USING date_awarded::timestamp;