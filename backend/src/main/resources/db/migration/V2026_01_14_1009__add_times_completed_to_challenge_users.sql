/**
 * Purpose: Evolution from one-time tasks to repeatable habits.
 * Description: Adds a counter to the 'challenge_users' junction table to track
 * how many times a user has successfully finished a specific challenge.
 * Logic: Supports gamification features like habit streaks and recurring XP rewards.
 */

-- Adds 'times_completed' to track recurring success on a per-challenge basis.
-- Default value of 0 ensures compatibility with legacy records and new assignments.
ALTER TABLE challenge_users ADD COLUMN times_completed INTEGER DEFAULT 0;