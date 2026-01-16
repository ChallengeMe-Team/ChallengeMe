/**
 * Purpose: Schema enhancement and data enrichment for the Gamification Module.
 * Description: Adds visual metadata (icons) and reward values (points) to existing badges.
 * Logic: Uses ALTER TABLE for schema updates and conditional UPDATEs for data consistency.
 */

-- 1. SCHEMA EVOLUTION
-- Adding new columns to the 'badges' table to support UI rendering and point rewarding.
-- 'icon_url' provides the path to frontend assets.
-- 'points_reward' defines the XP value granted when the badge is unlocked.
ALTER TABLE badges ADD COLUMN IF NOT EXISTS icon_url VARCHAR(255);
ALTER TABLE badges ADD COLUMN IF NOT EXISTS points_reward INT DEFAULT 0;

-- 2. DATA ENRICHMENT
-- Mapping high-quality assets and specific point rewards to established badge definitions.
-- This ensures a rich user experience (UX) in the "Trophy Case" section.

UPDATE badges SET icon_url = '/assets/badges/early-bird.png', points_reward = 15 WHERE name = 'Early Bird';
UPDATE badges SET icon_url = '/assets/badges/hydration.png', points_reward = 10 WHERE name = 'Hydration Hero';
UPDATE badges SET icon_url = '/assets/badges/social.png', points_reward = 50 WHERE name = 'Social Butterfly';
UPDATE badges SET icon_url = '/assets/badges/zen.png', points_reward = 30 WHERE name = 'Zen Master';
UPDATE badges SET icon_url = '/assets/badges/fitness.png', points_reward = 100 WHERE name = 'Fitness Beast';

-- 3. DATA INTEGRITY (FALLBACK)
-- Ensures that any badge added manually or missing an asset will display a default icon,
-- preventing broken image links in the frontend.
UPDATE badges SET icon_url = '/assets/badges/default.png' WHERE icon_url IS NULL;