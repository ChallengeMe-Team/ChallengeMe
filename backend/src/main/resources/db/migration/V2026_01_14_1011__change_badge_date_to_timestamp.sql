ALTER TABLE user_badges
ALTER COLUMN date_awarded TYPE TIMESTAMP WITHOUT TIME ZONE
USING date_awarded::timestamp;