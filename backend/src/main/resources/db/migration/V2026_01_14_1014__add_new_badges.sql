/**
 * Purpose: Implementation of high-tier rewards for habit formation and skill mastery.
 * Description: Inserts advanced badges that track long-term commitment and
 * repetitive success, moving beyond simple one-time task completion.
 * Logic: Aligns with the Habitation Logic and Category-specific Rules Engine.
 */


INSERT INTO badges (id, name, description, criteria, icon_url, points_reward) VALUES
-- These reward deep dives into technical and educational fields.
-- 'Logic Wizard' and 'Eternal Student' encourage users to focus on self-improvement.
(gen_random_uuid(), 'Logic Wizard', 'Complete 5 Coding challenges', '5 Coding Challenges', 'assets/badges/logic_wizard.png', 120),
(gen_random_uuid(), 'Eternal Student', 'Complete 5 Education challenges', '5 Education Challenges', 'assets/badges/student.png', 90),
-- This is a key gamification milestone. It utilizes the 'times_completed' column
-- to reward users who turn a challenge into a daily habit.
-- This badge has one of the highest XP rewards (150) to reflect the effort required.
(gen_random_uuid(), 'Habit Builder', 'Complete the same challenge 5 times', '5 Repetitions', 'assets/badges/habit.png', 150);