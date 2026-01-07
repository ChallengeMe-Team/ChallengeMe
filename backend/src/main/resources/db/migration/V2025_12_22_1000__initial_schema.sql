-- Create Users Table
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       username VARCHAR(20) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(120) NOT NULL,
                       points INTEGER,
                       avatar VARCHAR(255),
                       role VARCHAR(255) DEFAULT 'user',
                       friend_ids UUID[] -- Suport pentru List<UUID> din Java
);

-- Create Badges Table
CREATE TABLE badges (
                        id UUID PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description VARCHAR(255) NOT NULL,
                        criteria VARCHAR(255)
);

-- Create Challenges Table
CREATE TABLE challenges (
                            id UUID PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            description TEXT,
                            category VARCHAR(255) NOT NULL,
                            difficulty VARCHAR(20) NOT NULL, -- Enum: EASY, MEDIUM, HARD
                            points INTEGER NOT NULL,
                            created_by VARCHAR(255) NOT NULL
);

-- Create Challenge_Users Table (Link Table)
CREATE TABLE challenge_users (
                                 id UUID PRIMARY KEY,
                                 user_id UUID NOT NULL REFERENCES users(id),
                                 challenge_id UUID NOT NULL REFERENCES challenges(id),
                                 status VARCHAR(20) NOT NULL, -- Enum: PENDING, ACCEPTED, COMPLETED, RECEIVED
                                 date_accepted DATE,
                                 date_completed DATE,
                                 start_date DATE,
                                 deadline DATE,
                                 assigned_by UUID
);

-- Create User_Badges Table
CREATE TABLE user_badges (
                             id UUID PRIMARY KEY,
                             user_id UUID NOT NULL REFERENCES users(id),
                             badge_id UUID NOT NULL REFERENCES badges(id),
                             date_awarded DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Create Notifications Table
CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,
                               message VARCHAR(255) NOT NULL,
                               type VARCHAR(20) NOT NULL, -- Enum: CHALLENGE, BADGE, SYSTEM
                               timestamp TIMESTAMP,
                               is_read BOOLEAN DEFAULT FALSE
);

-- Create Leaderboard Table
CREATE TABLE leaderboard (
                             id UUID PRIMARY KEY,
                             user_id UUID NOT NULL REFERENCES users(id),
                             total_points INTEGER NOT NULL,
                             rank INTEGER NOT NULL
);