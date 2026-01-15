INSERT INTO challenges (id, title, description, category, difficulty, points, created_by) VALUES
-- Necesar pentru badge-ul "Extreme Hydration"
(gen_random_uuid(), 'Water Intake', 'Drink 2 liters of water throughout the day', 'Health', 'EASY', 30, 'system'),
-- Provocări de Fitness variate pentru badge-ul "Marathoner"
(gen_random_uuid(), 'Morning Run', 'Run for 20 minutes in your neighborhood', 'Fitness', 'MEDIUM', 60, 'system'),
(gen_random_uuid(), 'Push-up Set', 'Complete 3 sets of 15 push-ups', 'Fitness', 'EASY', 40, 'system'),
-- Necesar pentru badge-ul "Weekend Chef"
(gen_random_uuid(), 'Weekend Chef Challenge', 'Cook a complex meal from scratch this weekend', 'Food', 'MEDIUM', 100, 'system');