/**
 * Purpose: Initial awarding of badges to test users.
 * Logic: Links specific users to the Badge catalog to verify the "Trophy Case"
 * functionality and the many-to-many relationship implementation.
 */

-- 1. Awarding 'Early Bird' to Emilia
-- Validates that the system can handle manual or automated badge assignment.
INSERT INTO user_badges (id, user_id, badge_id, date_awarded)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '11111111-aaaa-1111-aaaa-111111111111', NOW());

-- 2. Awarding 'Hydration Hero' to Emilia
-- Tests the ability of a single user to accumulate multiple distinct achievements.
INSERT INTO user_badges (id, user_id, badge_id, date_awarded)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '22222222-bbbb-2222-bbbb-222222222222', NOW());

-- 3. Awarding 'Social Butterfly' to Emilia
-- Provides data for the profile section to ensure the UI renders badge icons correctly.
INSERT INTO user_badges (id, user_id, badge_id, date_awarded)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '33333333-cccc-3333-cccc-333333333333', NOW());