-- Modificăm ambele coloane pentru a păstra ora exactă
ALTER TABLE challenge_users
ALTER COLUMN date_accepted TYPE TIMESTAMP
USING date_accepted::timestamp;

ALTER TABLE challenge_users
ALTER COLUMN date_completed TYPE TIMESTAMP
USING date_completed::timestamp;


ALTER TABLE notifications ALTER COLUMN timestamp TYPE TIMESTAMP;