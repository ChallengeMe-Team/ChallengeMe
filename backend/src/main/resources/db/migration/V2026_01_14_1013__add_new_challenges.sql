/**
 * Purpose: Strategic seeding of challenges required for badge automation.
 * Description: Inserts the foundational quests that the 'Rules Engine' expects
 * to find when calculating category-based achievements.
 * Attribution: These are marked as 'system' created, representing official platform content.
 */

INSERT INTO challenges (id, title, description, category, difficulty, points, created_by) VALUES
-- Specifically required to trigger the 'Extreme Hydration' badge.
(gen_random_uuid(), 'Water Intake', 'Drink 2 liters of water throughout the day', 'Health', 'EASY', 30, 'system'),
-- These provide the necessary volume (Fitness category) to help users
-- work towards the 'Marathoner' badge.
(gen_random_uuid(), 'Morning Run', 'Run for 20 minutes in your neighborhood', 'Fitness', 'MEDIUM', 60, 'system'),
(gen_random_uuid(), 'Push-up Set', 'Complete 3 sets of 15 push-ups', 'Fitness', 'EASY', 40, 'system'),
-- Directly linked to the 'Weekend Chef' badge logic.
(gen_random_uuid(), 'Weekend Chef Challenge', 'Cook a complex meal from scratch this weekend', 'Food', 'MEDIUM', 100, 'system');