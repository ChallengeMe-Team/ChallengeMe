/**
 * Description: Initial database schema for the ChallengeMe platform.
 */

-- 1. Entity: User Identity and Social Graph
-- Stores credentials and experience points (XP).
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       username VARCHAR(20) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(120) NOT NULL,
                       points INTEGER,
                       avatar VARCHAR(255),
                       role VARCHAR(255) DEFAULT 'user',
                       friend_ids UUID[] -- PostgreSQL native array for efficient social link storage
);

-- 2. Entity: Achievement Catalog
-- Definitions for unlockable badges within the platform.
CREATE TABLE badges (
                        id UUID PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description VARCHAR(255) NOT NULL,
                        criteria VARCHAR(255) -- Business rule description for unlocking
);

-- 3. Entity: Challenge Definitions
-- The global library of quests available to all users.
CREATE TABLE challenges (
                            id UUID PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            description TEXT,
                            category VARCHAR(255) NOT NULL,
                            difficulty VARCHAR(20) NOT NULL, -- Mapped to Difficulty Java Enum
                            points INTEGER NOT NULL,
                            created_by VARCHAR(255) NOT NULL -- Stores username for social attribution
);

-- 4. Junction Table: Quest Participation
-- Tracks the specific lifecycle of a challenge for a user (Assignment -> Acceptance -> Completion).
CREATE TABLE challenge_users (
                                 id UUID PRIMARY KEY,
                                 user_id UUID NOT NULL REFERENCES users(id),
                                 challenge_id UUID NOT NULL REFERENCES challenges(id),
                                 status VARCHAR(20) NOT NULL, --PENDING, ACCEPTED, COMPLETED, RECEIVED
                                 date_accepted DATE,
                                 date_completed DATE,
                                 start_date DATE,
                                 deadline DATE,
                                 assigned_by UUID -- Reference to the user who sent the challenge (if social)
);

-- 5. Junction Table: Achievement Unlocks
-- Records the specific moment a user fulfills badge criteria.
CREATE TABLE user_badges (
                             id UUID PRIMARY KEY,
                             user_id UUID NOT NULL REFERENCES users(id),
                             badge_id UUID NOT NULL REFERENCES badges(id),
                             date_awarded DATE NOT NULL DEFAULT CURRENT_DATE
);

-- 6. Entity: Communication & Alerts
-- Asynchronous notifications for gamification events.
CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,
                               message VARCHAR(255) NOT NULL,
                               type VARCHAR(20) NOT NULL, -- CHALLENGE, BADGE, SYSTEM
                               timestamp TIMESTAMP,
                               is_read BOOLEAN DEFAULT FALSE
);

-- 7. Entity: Performance Ranking
-- Persistent storage for calculated ranks to avoid heavy real-time aggregation.
CREATE TABLE leaderboard (
                             id UUID PRIMARY KEY,
                             user_id UUID NOT NULL REFERENCES users(id),
                             total_points INTEGER NOT NULL,
                             rank INTEGER NOT NULL
);