INSERT INTO badges (id, name, description, criteria, icon_url, points_reward) VALUES
-- Pentru provocările de Coding
(gen_random_uuid(), 'Logic Wizard', 'Complete 5 Coding challenges', '5 Coding Challenges', 'assets/badges/logic_wizard.png', 120),
-- Pentru provocările de Education
(gen_random_uuid(), 'Eternal Student', 'Complete 5 Education challenges', '5 Education Challenges', 'assets/badges/student.png', 90),
-- Pentru perseverență (Redo)
(gen_random_uuid(), 'Habit Builder', 'Complete the same challenge 5 times', '5 Repetitions', 'assets/badges/habit.png', 150);