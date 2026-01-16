/**
 * Purpose: Final fix for start_date precision.
 * Logic: This addresses the Hibernate validation error by aligning the DB column
 * type with the LocalDateTime entity field.
 */

ALTER TABLE challenge_users
ALTER COLUMN start_date TYPE TIMESTAMP WITHOUT TIME ZONE
USING start_date::timestamp;