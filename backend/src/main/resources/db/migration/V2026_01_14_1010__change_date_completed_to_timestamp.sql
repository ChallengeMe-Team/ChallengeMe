/**
 * Purpose: Temporal precision upgrade for activity tracking.
 * Description: Changes 'date_completed' column type from DATE to TIMESTAMP.
 * Logic: Enables precise chronological sorting and detailed activity history
 * in the user profile (e.g., "Completed 5 minutes ago").
 */

-- Alters the 'challenge_users' table to support high-precision time data.
-- 'USING date_completed::timestamp' handles the safe conversion of existing records,
-- defaulting the time to 00:00:00 for previous entries.
ALTER TABLE challenge_users
ALTER COLUMN date_completed TYPE TIMESTAMP WITHOUT TIME ZONE
USING date_completed::timestamp;