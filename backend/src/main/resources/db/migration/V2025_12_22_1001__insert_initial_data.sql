/**
 * Purpose: Populates the database with initial test data for development and demonstration.
 * Logic: Includes diverse user profiles, varied difficulty challenges, and a pre-configured social graph.
 */

-- ===========================
-- 1. USERS POPULATION
-- ===========================
-- Scenario: Creating 11 users with different XP levels to test the Leaderboard sorting.
-- Password for all users is 'Password_123' (bcrypt hashed).
INSERT INTO users (id, username, email, password, points, role, avatar) VALUES
                                                                            ('11111111-1111-1111-1111-111111111111', 'emilia', 'emilia@example.com', '{bcrypt}$2a$10$i3ULiPE1aqKwSu5e9ddyRuRna4pqqGG2vpTw65YJ52GwdOcwaybYC', 120, 'user', 'cat.png'),
                                                                            ('22222222-2222-2222-2222-222222222222', 'roger', 'roger@example.com', '{bcrypt}$2a$10$i3ULiPE1aqKwSu5e9ddyRuRna4pqqGG2vpTw65YJ52GwdOcwaybYC', 200, 'user', 'gamer.png'),
                                                                            ('33333333-3333-3333-3333-333333333333', 'calin', 'calin@example.com', '{bcrypt}$2a$10$i3ULiPE1aqKwSu5e9ddyRuRna4pqqGG2vpTw65YJ52GwdOcwaybYC', 180, 'user', 'dog.png'),
                                                                            ('44444444-4444-4444-4444-444444444444', 'alex', 'alex@example.com', '{bcrypt}$2a$10$i3ULiPE1aqKwSu5e9ddyRuRna4pqqGG2vpTw65YJ52GwdOcwaybYC', 90, 'user', 'monster.png'),
                                                                            ('55555555-5555-5555-5555-555555555555', 'iustin', 'iustin@example.com', '{bcrypt}$2a$10$i3ULiPE1aqKwSu5e9ddyRuRna4pqqGG2vpTw65YJ52GwdOcwaybYC', 75, 'user', 'ninja.png'),
                                                                            ('66666666-6666-6666-6666-666666666666', 'emanuel', 'emanuel@example.com', '{bcrypt}$2a$10$i3ULiPE1aqKwSu5e9ddyRuRna4pqqGG2vpTw65YJ52GwdOcwaybYC', 130, 'user', 'robot.png'),
                                                                            ('77777777-7777-7777-7777-777777777777', 'stefan', 'stefan@example.com', '{bcrypt}$2a$10$i3ULiPE1aqKwSu5e9ddyRuRna4pqqGG2vpTw65YJ52GwdOcwaybYC', 110, 'user', 'gamer.png'),
                                                                            ('88888888-8888-8888-8888-888888888888', 'tudor', 'tudor@example.com', '{bcrypt}$2a$10$i3ULiPE1aqKwSu5e9ddyRuRna4pqqGG2vpTw65YJ52GwdOcwaybYC', 95, 'user', 'dog.png'),
                                                                            ('99999999-9999-9999-9999-999999999999', 'ana', 'ana@example.com', '{bcrypt}$2a$10$i3ULiPE1aqKwSu5e9ddyRuRna4pqqGG2vpTw65YJ52GwdOcwaybYC', 60, 'user', 'cat.png'),
                                                                            ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'maria', 'maria@example.com', '{bcrypt}$2a$10$i3ULiPE1aqKwSu5e9ddyRuRna4pqqGG2vpTw65YJ52GwdOcwaybYC', 50, 'user', 'gamer.png'),
                                                                            ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'george', 'george@example.com', '{bcrypt}$2a$10$i3ULiPE1aqKwSu5e9ddyRuRna4pqqGG2vpTw65YJ52GwdOcwaybYC', 40, 'user', 'robot.png');

-- ===========================
-- 2. BADGES CATALOG
-- ===========================
-- Defines the rewards system for categorical achievements (Fitness, Mindfulness, etc.).
INSERT INTO badges (id, name, description, criteria) VALUES
                                                         ('11111111-aaaa-1111-aaaa-111111111111', 'Early Bird', 'Wake up before 7:00 AM', 'Complete a challenge before 08:00 AM'),
                                                         ('22222222-bbbb-2222-bbbb-222222222222', 'Hydration Hero', 'Stay hydrated, stay happy', 'Complete the Water Intake challenge 3 times'),
                                                         ('33333333-cccc-3333-cccc-333333333333', 'Social Butterfly', 'The soul of the party', 'Invite 5 friends to a challenge'),
                                                         ('44444444-dddd-4444-dddd-444444444444', 'Zen Master', 'A peaceful mind', 'Complete 5 Mindfulness challenges'),
                                                         ('55555555-eeee-5555-eeee-555555555555', 'Fitness Beast', 'Unstoppable energy', 'Walk a total of 50,000 steps');

-- ===========================
-- 3. GLOBAL CHALLENGES
-- ===========================
-- A set of 10 challenges covering different categories and difficulty levels for the public catalog.
INSERT INTO challenges (id, title, description, category, difficulty, points, created_by) VALUES
                                                                                              ('11111111-1111-aaaa-aaaa-111111111111', 'Step by Step', 'Walk 10,000 steps today', 'Fitness', 'EASY', 50, 'emilia'),
                                                                                              ('22222222-2222-bbbb-bbbb-222222222222', 'Weekend Chef', 'Cook a new recipe and share a photo', 'Food', 'MEDIUM', 100, 'roger'),
                                                                                              ('33333333-3333-cccc-cccc-333333333333', 'Digital Detox', 'No social media for 6 hours', 'Mindfulness', 'HARD', 150, 'calin'),
                                                                                              ('44444444-4444-dddd-dddd-444444444444', 'Max Hydration', 'Drink 2 liters of water', 'Health', 'EASY', 30, 'alex'),
                                                                                              ('55555555-5555-eeee-eeee-555555555555', 'Healthy Start', 'Eat fruits for breakfast', 'Health', 'EASY', 20, 'iustin'),
                                                                                              ('66666666-6666-ffff-ffff-666666666666', 'Board Game Night', 'Organize a game night with friends', 'Social', 'MEDIUM', 80, 'emanuel'),
                                                                                              ('77777777-7777-4444-4444-777777777777', 'No Phone at Dinner', 'Eat without checking your phone', 'Mindfulness', 'MEDIUM', 60, 'stefan'),
                                                                                              ('88888888-8888-5555-5555-888888888888', 'Outdoor Reading', 'Read 20 pages in the park', 'Lifestyle', 'EASY', 40, 'tudor'),
                                                                                              ('99999999-9999-6666-6666-999999999999', 'Cold Shower', 'Take a cold shower for 2 minutes', 'Health', 'HARD', 120, 'ana'),
                                                                                              ('aaaaaaaa-aaaa-7777-7777-aaaaaaaaaaaa', 'Gratitude Journal', 'Write down 3 things you are grateful for', 'Mindfulness', 'EASY', 50, 'maria');

-- ===========================
-- 4. SOCIAL GRAPH (Friendships)
-- ===========================
-- Establishing the friendship network. Demonstrates the use of PostgreSQL Arrays
-- for many-to-many relationship optimization.
UPDATE users SET friend_ids = '{77777777-7777-7777-7777-777777777777, 66666666-6666-6666-6666-666666666666, aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa, 33333333-3333-3333-3333-333333333333}' WHERE username = 'emilia';
UPDATE users SET friend_ids = '{33333333-3333-3333-3333-333333333333, 44444444-4444-4444-4444-444444444444, 88888888-8888-8888-8888-888888888888}' WHERE username = 'roger';
UPDATE users SET friend_ids = '{11111111-1111-1111-1111-111111111111, 22222222-2222-2222-2222-222222222222}' WHERE username = 'calin';
UPDATE users SET friend_ids = '{22222222-2222-2222-2222-222222222222, 99999999-9999-9999-9999-999999999999}' WHERE username = 'alex';
UPDATE users SET friend_ids = '{33333333-3333-3333-3333-333333333333, 66666666-6666-6666-6666-666666666666}' WHERE username = 'iustin';
UPDATE users SET friend_ids = '{11111111-1111-1111-1111-111111111111, aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa}' WHERE username = 'emanuel';
UPDATE users SET friend_ids = '{11111111-1111-1111-1111-111111111111, 88888888-8888-8888-8888-888888888888}' WHERE username = 'stefan';
UPDATE users SET friend_ids = '{22222222-2222-2222-2222-222222222222, 77777777-7777-7777-7777-777777777777}' WHERE username = 'tudor';
UPDATE users SET friend_ids = '{44444444-4444-4444-4444-444444444444, bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb}' WHERE username = 'ana';
UPDATE users SET friend_ids = '{11111111-1111-1111-1111-111111111111, 66666666-6666-6666-6666-666666666666}' WHERE username = 'maria';
UPDATE users SET friend_ids = '{44444444-4444-4444-4444-444444444444, aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa}' WHERE username = 'george';
