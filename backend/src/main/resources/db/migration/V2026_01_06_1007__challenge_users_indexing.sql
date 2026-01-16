/**
 * Purpose: Performance tuning for the Gamification Engine.
 * Description: Creates a composite index on the 'challenge_users' table to accelerate
 * filtering and aggregation for rankings.
 * Impact: Reduces query execution time for Weekly, Monthly, and All-Time leaderboards.
 */

-- Create a composite index to optimize queries that filter by status (COMPLETED)
-- and sort or filter by completion date.
CREATE INDEX idx_challenge_user_ranking
    ON challenge_users (status, date_completed);