-- src/main/resources/db/migration/V2026.01.14.1205__change_badge_date_to_timestamp.sql
ALTER TABLE user_badges
ALTER COLUMN date_awarded TYPE TIMESTAMP WITHOUT TIME ZONE
USING date_awarded::timestamp;