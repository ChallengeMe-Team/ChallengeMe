/**
 * Purpose: Final alignment of the Badge Catalog with Frontend assets and Service logic.
 * Logic: Performs a "Clean & Load" strategy to ensure no orphaned or redundant badge records exist.
 * Impact: Synchronizes badge names with the Rules Engine in UserService.java.
 */

-- 1. DATA CLEANUP
-- Removes existing records to prevent unique constraint violations or ID conflicts.
-- Cascading rules (ON DELETE CASCADE) ensure 'user_badges' remain consistent.
DELETE FROM user_badges;
DELETE FROM badges;

-- 2. CONSOLIDATED CATALOG LOAD
-- Inserts the definitive list of achievements with exact asset paths.
-- The names (e.g., 'Marathoner', 'Zen Master') must match the strings checked
-- in UserService.generateBadges() for the automation to work.
INSERT INTO badges (id, name, description, criteria, icon_url, points_reward) VALUES
                                                                                  (gen_random_uuid(), 'Marathoner', 'Complete 3 challenges from the Fitness category', '3 Fitness Challenges', 'assets/badges/marathon.png', 50),
                                                                                  (gen_random_uuid(), 'Weekend Chef', 'Cook a new recipe and share a photo', 'Complete Weekend Chef', 'assets/badges/chef.png', 30),
                                                                                  (gen_random_uuid(), 'Zen Master', 'Complete 5 challenges from the Mindfulness category', '5 Mindfulness Challenges', 'assets/badges/zen_master.png', 40),
                                                                                  (gen_random_uuid(), 'Extreme Hydration', 'Drink 2 liters of water 5 times', '5 Max Hydration completions', 'assets/badges/water_god.png', 25),
                                                                                  (gen_random_uuid(), 'Veteran', 'Maintain a streak of 7 consecutive days', '7 Day Streak', 'assets/badges/veteran.png', 100),
                                                                                  (gen_random_uuid(), 'First Step', 'Completed your first challenge', '1 Challenge', 'assets/badges/first-step.png', 10);