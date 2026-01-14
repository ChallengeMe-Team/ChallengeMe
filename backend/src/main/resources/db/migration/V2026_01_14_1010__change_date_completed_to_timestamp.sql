-- Schimbăm tipul coloanei date_completed pentru a suporta ora exactă
ALTER TABLE challenge_users
ALTER COLUMN date_completed TYPE TIMESTAMP WITHOUT TIME ZONE
USING date_completed::timestamp;