-- 1. Early Bird
INSERT INTO user_badges (id, user_id, badge_id, date_awarded)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '11111111-aaaa-1111-aaaa-111111111111', NOW());

-- 2. Hydration Hero
INSERT INTO user_badges (id, user_id, badge_id, date_awarded)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '22222222-bbbb-2222-bbbb-222222222222', NOW());

-- 3. Social Butterfly
INSERT INTO user_badges (id, user_id, badge_id, date_awarded)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '33333333-cccc-3333-cccc-333333333333', NOW());