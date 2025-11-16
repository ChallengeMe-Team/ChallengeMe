TRUNCATE TABLE user_badges CASCADE;
TRUNCATE TABLE challenge_users CASCADE;
TRUNCATE TABLE leaderboard CASCADE;
TRUNCATE TABLE notifications CASCADE;
TRUNCATE TABLE badges CASCADE;
TRUNCATE TABLE challenges CASCADE;
TRUNCATE TABLE users CASCADE;

-- ===========================
-- Users
INSERT INTO users (id, username, email, password, points) VALUES
                                                              ('11111111-1111-1111-1111-111111111111', 'emilia', 'emilia@example.com', 'password123', 120),
                                                              ('22222222-2222-2222-2222-222222222222', 'roger', 'roger@example.com', 'password123', 200),
                                                              ('33333333-3333-3333-3333-333333333333', 'calin', 'calin@example.com', 'password123', 180),
                                                              ('44444444-4444-4444-4444-444444444444', 'alex', 'alex@example.com', 'password123', 90),
                                                              ('55555555-5555-5555-5555-555555555555', 'iustin', 'iustin@example.com', 'password123', 75),
                                                              ('66666666-6666-6666-6666-666666666666', 'emanuel', 'emanuel@example.com', 'password123', 130),
                                                              ('77777777-7777-7777-7777-777777777777', 'stefan', 'stefan@example.com', 'password123', 110),
                                                              ('88888888-8888-8888-8888-888888888888', 'tudor', 'tudor@example.com', 'password123', 95),
                                                              ('99999999-9999-9999-9999-999999999999', 'ana', 'ana@example.com', 'password123', 60),
                                                              ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'maria', 'maria@example.com', 'password123', 50),
                                                              ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'george', 'george@example.com', 'password123', 40);

-- ===========================
-- Badges
INSERT INTO badges (id, name, description, criteria) VALUES
                                                         ('11111111-aaaa-1111-aaaa-111111111111', 'First Challenge', 'Completed first challenge', 'Complete any challenge'),
                                                         ('22222222-bbbb-2222-bbbb-222222222222', 'Top Scorer', 'Achieved top score in leaderboard', 'Be in top 3'),
                                                         ('33333333-cccc-3333-cccc-333333333333', 'Marathon', 'Completed 5 challenges in a row', 'Complete 5 challenges'),
                                                         ('44444444-dddd-4444-dddd-444444444444', 'Quick Starter', 'Complete first challenge in less than 1 day', 'First challenge < 24h'),
                                                         ('55555555-eeee-5555-eeee-555555555555', 'Collector', 'Earn 3 badges', 'Have 3 badges');

-- ===========================
-- Challenges
INSERT INTO challenges (id, title, description, category, difficulty, points, created_by) VALUES
                                                                                              ('11111111-1111-aaaa-aaaa-111111111111', 'Angular Basics', 'Learn the basics of Angular', 'Web', 'EASY', 50, 'emilia'),
                                                                                              ('22222222-2222-bbbb-bbbb-222222222222', 'Spring Boot CRUD', 'Implement CRUD operations', 'Backend', 'MEDIUM', 100, 'roger'),
                                                                                              ('33333333-3333-cccc-cccc-333333333333', 'Docker Setup', 'Setup project with Docker', 'DevOps', 'HARD', 150, 'calin'),
                                                                                              ('44444444-4444-dddd-dddd-444444444444', 'Unit Testing', 'Write unit tests for services', 'Testing', 'MEDIUM', 80, 'alex'),
                                                                                              ('55555555-5555-eeee-eeee-555555555555', 'Frontend Styling', 'Implement CSS/SCSS layouts', 'Web', 'EASY', 40, 'iustin'),
                                                                                              ('66666666-6666-ffff-ffff-666666666666', 'Database Relationships', 'Create JPA entities', 'Backend', 'MEDIUM', 90, 'emanuel'),
                                                                                              ('77777777-7777-4444-4444-777777777777', 'Authentication Flow', 'Implement login/logout', 'Security', 'HARD', 120, 'stefan'),
                                                                                              ('88888888-8888-5555-5555-888888888888', 'API Documentation', 'Write Swagger docs', 'Documentation', 'EASY', 30, 'tudor'),
                                                                                              ('99999999-9999-6666-6666-999999999999', 'CI/CD Pipeline', 'Setup automated deployment', 'DevOps', 'HARD', 150, 'ana'),
                                                                                              ('aaaaaaaa-aaaa-7777-7777-aaaaaaaaaaaa', 'Leaderboard Feature', 'Implement ranking system', 'Backend', 'MEDIUM', 100, 'maria');

-- ===========================
-- User Badges
INSERT INTO user_badges (id, user_id, badge_id, date_awarded) VALUES
                                                                  ('11111111-aaaa-1111-aaaa-111111111111', '11111111-1111-1111-1111-111111111111', '11111111-aaaa-1111-aaaa-111111111111', CURRENT_DATE),
                                                                  ('22222222-bbbb-2222-bbbb-222222222222', '22222222-2222-2222-2222-222222222222', '22222222-bbbb-2222-bbbb-222222222222', CURRENT_DATE),
                                                                  ('33333333-cccc-3333-cccc-333333333333', '33333333-3333-3333-3333-333333333333', '33333333-cccc-3333-cccc-333333333333', CURRENT_DATE);

-- ===========================
-- Challenge Users
INSERT INTO challenge_users (id, user_id, challenge_id, status, date_accepted, date_completed) VALUES
                                                                                                   ('11111111-1111-aaaa-1111-111111111111', '11111111-1111-1111-1111-111111111111', '11111111-1111-aaaa-aaaa-111111111111', 'COMPLETED', CURRENT_DATE - 5, CURRENT_DATE - 1),
                                                                                                   ('22222222-2222-bbbb-bbbb-222222222222', '22222222-2222-2222-2222-222222222222', '22222222-2222-bbbb-bbbb-222222222222', 'ACCEPTED', CURRENT_DATE - 3, NULL),
                                                                                                   ('33333333-3333-cccc-cccc-333333333333', '33333333-3333-3333-3333-333333333333', '33333333-3333-cccc-cccc-333333333333', 'PENDING', NULL, NULL);

-- ===========================
-- Leaderboard
INSERT INTO leaderboard (id, user_id, total_points, rank) VALUES
                                                              ('11111111-aaaa-aaaa-1111-111111111111', '22222222-2222-2222-2222-222222222222', 200, 1),
                                                              ('22222222-bbbb-bbbb-2222-222222222222', '33333333-3333-3333-3333-333333333333', 180, 2),
                                                              ('33333333-cccc-cccc-3333-333333333333', '11111111-1111-1111-1111-111111111111', 120, 3),
                                                              ('44444444-dddd-dddd-4444-444444444444', '66666666-6666-6666-6666-666666666666', 130, 4),
                                                              ('55555555-eeee-eeee-5555-555555555555', '77777777-7777-7777-7777-777777777777', 110, 5),
                                                              ('66666666-ffff-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', 90, 6),
                                                              ('77777777-7777-5555-5555-777777777777', '88888888-8888-8888-8888-888888888888', 95, 7),
                                                              ('88888888-8888-6666-6666-888888888888', '55555555-5555-5555-5555-555555555555', 75, 8),
                                                              ('99999999-9999-7777-7777-999999999999', '99999999-9999-9999-9999-999999999999', 60, 9),
                                                              ('aaaaaaaa-aaaa-8888-8888-aaaaaaaaaaaa', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 50, 10),
                                                              ('bbbbbbbb-bbbb-9999-9999-bbbbbbbbbbbb', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 40, 11);

-- ===========================
-- Notifications
INSERT INTO notifications (id, user_id, message, type, timestamp, is_read) VALUES
                                                                               ('11111111-aaaa-1111-aaaa-111111111111', '11111111-1111-1111-1111-111111111111', 'Welcome to ChallengeMe!', 'SYSTEM', CURRENT_TIMESTAMP, false),
                                                                               ('22222222-bbbb-2222-bbbb-222222222222', '22222222-2222-2222-2222-222222222222', 'New challenge available!', 'CHALLENGE', CURRENT_TIMESTAMP, false),
                                                                               ('33333333-cccc-3333-cccc-333333333333', '33333333-3333-3333-3333-333333333333', 'You earned a badge!', 'BADGE', CURRENT_TIMESTAMP, false);
