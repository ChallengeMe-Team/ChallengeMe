-- =============================================================
-- NEW CHALLENGES (Competitive & Diverse)
-- =============================================================

INSERT INTO challenges (id, title, description, category, difficulty, points, created_by) VALUES
-- Education & Skill Building
(gen_random_uuid(), 'Speed Learner', 'Complete a tutorial or read a technical article for 30 mins', 'Education', 'EASY', 40, 'emilia'),
(gen_random_uuid(), 'Language Sprint', 'Learn 10 new words in a foreign language', 'Education', 'MEDIUM', 70, 'calin'),

-- Creativity
(gen_random_uuid(), 'Digital Sketch', 'Draw something digital or on paper and share it', 'Creativity', 'MEDIUM', 90, 'maria'),
(gen_random_uuid(), 'Creative Writing', 'Write a short story or a poem of at least 200 words', 'Creativity', 'HARD', 130, 'alex'),

-- Coding & Logic
(gen_random_uuid(), 'Algorithm Master', 'Solve one Medium/Hard problem on LeetCode', 'Coding', 'HARD', 150, 'emanuel'),
(gen_random_uuid(), 'Bug Hunter', 'Find and fix a bug in a personal or open-source project', 'Coding', 'MEDIUM', 100, 'roger'),

-- Fitness (Advanced)
(gen_random_uuid(), 'Plank Challenge', 'Hold a plank position for 3 consecutive minutes', 'Fitness', 'HARD', 120, 'iustin');


-- =============================================================
-- NEW BADGES (Goal Oriented)
-- =============================================================

INSERT INTO badges (id, name, description, criteria, icon_url, points_reward) VALUES
-- Education
(gen_random_uuid(), 'Polyglot', 'Complete 3 language-related challenges', '3 Education Challenges', 'assets/badges/polyglot.png', 60),

-- Creativity
(gen_random_uuid(), 'Artisan', 'Unlock your creative side by finishing 3 Creativity challenges', '3 Creativity Challenges', 'assets/badges/artisan.png', 80),

-- Coding
(gen_random_uuid(), 'Code Ninja', 'Solve complex logic and coding problems', '3 Coding Challenges', 'assets/badges/code_ninja.png', 100),

-- Multi-Category Mastery
(gen_random_uuid(), 'Jack of All Trades', 'Complete at least one challenge in 5 different categories', '5 Different Categories', 'assets/badges/jack_of_all_trades.png', 150),

-- High Intensity
(gen_random_uuid(), 'Hardcore', 'Complete 5 challenges with HARD difficulty', '5 Hard Challenges', 'assets/badges/hardcore.png', 200);