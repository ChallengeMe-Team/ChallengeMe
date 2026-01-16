/**
 * Purpose: Ensures Referential Integrity and Symmetry for the Social Graph.
 * Logic: Updates the 'friend_ids' UUID arrays to ensure all friendship relations
 * are bidirectional (A -> B implies B -> A).
 * Impact: Prevents ghost connections and ensures consistency in the "Friends List" UI.
 */

-- Emilia's network: Symmetric links with Roger, Calin, Emanuel, Stefan, Maria
UPDATE users SET friend_ids = '{22222222-2222-2222-2222-222222222222, 33333333-3333-3333-3333-333333333333, 66666666-6666-6666-6666-666666666666, 77777777-7777-7777-7777-777777777777, aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa}' WHERE username = 'emilia';

-- Roger's network: Symmetric links with Emilia, Calin, Alex, Tudor
UPDATE users SET friend_ids = '{11111111-1111-1111-1111-111111111111, 33333333-3333-3333-3333-333333333333, 44444444-4444-4444-4444-444444444444, 88888888-8888-8888-8888-888888888888}' WHERE username = 'roger';

-- Calin's network: Symmetric links with Emilia, Roger, Alex, Iustin
UPDATE users SET friend_ids = '{11111111-1111-1111-1111-111111111111, 22222222-2222-2222-2222-222222222222, 44444444-4444-4444-4444-444444444444, 55555555-5555-5555-5555-555555555555}' WHERE username = 'calin';

-- Alex <-> Roger, Calin, Ana, George
UPDATE users SET friend_ids = '{22222222-2222-2222-2222-222222222222, 33333333-3333-3333-3333-333333333333, 99999999-9999-9999-9999-999999999999, bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb}' WHERE username = 'alex';

-- Iustin <-> Calin, Emanuel
UPDATE users SET friend_ids = '{33333333-3333-3333-3333-333333333333, 66666666-6666-6666-6666-666666666666}' WHERE username = 'iustin';

-- Emanuel <-> Emilia, Iustin, Maria
UPDATE users SET friend_ids = '{11111111-1111-1111-1111-111111111111, 55555555-5555-5555-5555-555555555555, aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa}' WHERE username = 'emanuel';

-- Stefan <-> Emilia, Tudor
UPDATE users SET friend_ids = '{11111111-1111-1111-1111-111111111111, 88888888-8888-8888-8888-888888888888}' WHERE username = 'stefan';

-- Tudor <-> Roger, Stefan
UPDATE users SET friend_ids = '{22222222-2222-2222-2222-222222222222, 77777777-7777-7777-7777-777777777777}' WHERE username = 'tudor';

-- Ana <-> Alex, George
UPDATE users SET friend_ids = '{44444444-4444-4444-4444-444444444444, bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb}' WHERE username = 'ana';

-- Maria <-> Emilia, Emanuel, George
UPDATE users SET friend_ids = '{11111111-1111-1111-1111-111111111111, 66666666-6666-6666-6666-666666666666, bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb}' WHERE username = 'maria';

-- George <-> Alex, Ana, Maria
UPDATE users SET friend_ids = '{44444444-4444-4444-4444-444444444444, 99999999-9999-9999-9999-999999999999, aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa}' WHERE username = 'george';