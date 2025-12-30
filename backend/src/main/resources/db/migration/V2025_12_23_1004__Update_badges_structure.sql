-- 1. Adăugăm coloanele noi la tabelul existent
ALTER TABLE badges ADD COLUMN IF NOT EXISTS icon_url VARCHAR(255);
ALTER TABLE badges ADD COLUMN IF NOT EXISTS points_reward INT DEFAULT 0;

-- 2. Actualizăm badge-urile existente (cele din scriptul 1001) cu iconițe și puncte
-- Nu facem INSERT, ci UPDATE, ca să nu duplicăm datele.

UPDATE badges SET icon_url = '/assets/badges/early-bird.png', points_reward = 15 WHERE name = 'Early Bird';
UPDATE badges SET icon_url = '/assets/badges/hydration.png', points_reward = 10 WHERE name = 'Hydration Hero';
UPDATE badges SET icon_url = '/assets/badges/social.png', points_reward = 50 WHERE name = 'Social Butterfly';
UPDATE badges SET icon_url = '/assets/badges/zen.png', points_reward = 30 WHERE name = 'Zen Master';
UPDATE badges SET icon_url = '/assets/badges/fitness.png', points_reward = 100 WHERE name = 'Fitness Beast';

-- Setăm un fallback pentru orice alt badge care ar putea exista fără icon
UPDATE badges SET icon_url = '/assets/badges/default.png' WHERE icon_url IS NULL;