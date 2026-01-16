/**
 * Purpose: Denormalization for performance optimization.
 * Description: Adds a persistent counter for total completed challenges directly to the 'users' table.
 * Logic: Allows instant retrieval of user progress without performing expensive aggregations
 * on the 'challenge_users' table.
 */

-- Adds the 'total_completed_challenges' column to store the global mission count.
-- Initialized with 0 to ensure consistency for new and existing users.
ALTER TABLE users
    ADD COLUMN total_completed_challenges INTEGER DEFAULT 0;