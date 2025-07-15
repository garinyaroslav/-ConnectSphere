INSERT INTO usr (id, password, username)
VALUES (1, '$2a$12$4Zqpfu3uN7CuO3cBjvYp0e82zhNTMYJnDc8FfcyL1NzoRiBURwit.', 'test_user1');
INSERT INTO usr (id, password, username)
VALUES (2, '$2a$12$4Zqpfu3uN7CuO3cBjvYp0e82zhNTMYJnDc8FfcyL1NzoRiBURwit.', 'test_user2');
INSERT INTO usr (id, password, username)
VALUES (3, '$2a$12$4Zqpfu3uN7CuO3cBjvYp0e82zhNTMYJnDc8FfcyL1NzoRiBURwit.', 'admin');

INSERT INTO user_roles (user_id, role)
VALUES (1, 'ROLE_USER');
INSERT INTO user_roles (user_id, role)
VALUES (2, 'ROLE_USER');
INSERT INTO user_roles (user_id, role)
VALUES (3, 'ROLE_ADMIN');


WITH seq_data AS (SELECT pg_get_serial_sequence('usr', 'id') AS seq_name)
SELECT setval(seq_name, 4, FALSE) FROM seq_data;

WITH seq_data AS (SELECT pg_get_serial_sequence('post', 'id') AS seq_name)
SELECT setval(seq_name, 1, FALSE) FROM seq_data;

WITH seq_data AS (SELECT pg_get_serial_sequence('subscription', 'id') AS seq_name)
SELECT setval(seq_name, 1, FALSE) FROM seq_data;