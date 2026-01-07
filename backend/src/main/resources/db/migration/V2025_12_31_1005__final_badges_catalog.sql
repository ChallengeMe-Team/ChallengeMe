-- 1. Wipe existing data to prevent ID/Name conflicts and duplicates
DELETE FROM user_badges;
DELETE FROM badges;

-- 2. Insert the consolidated English catalog with EXACT paths and filenames
INSERT INTO badges (id, name, description, criteria, icon_url, points_reward) VALUES
                                                                                  (gen_random_uuid(), 'Marathoner', 'Complete 3 challenges from the Fitness category', '3 Fitness Challenges', 'assets/badges/marathon.png', 50),
                                                                                  (gen_random_uuid(), 'Weekend Chef', 'Cook a new recipe and share a photo', 'Complete Weekend Chef', 'assets/badges/chef.png', 30),
                                                                                  (gen_random_uuid(), 'Zen Master', 'Complete 5 challenges from the Mindfulness category', '5 Mindfulness Challenges', 'assets/badges/zen_master.png', 40),
                                                                                  (gen_random_uuid(), 'Extreme Hydration', 'Drink 2 liters of water 5 times', '5 Max Hydration completions', 'assets/badges/water_god.png', 25),
                                                                                  (gen_random_uuid(), 'Veteran', 'Maintain a streak of 7 consecutive days', '7 Day Streak', 'assets/badges/veteran.png', 100),
                                                                                  (gen_random_uuid(), 'First Step', 'Completed your first challenge', '1 Challenge', 'assets/badges/first-step.png', 10);