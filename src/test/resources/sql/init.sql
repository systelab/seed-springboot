DELETE FROM patientallergies;
DELETE FROM patients;
DELETE FROM allergies;
DELETE FROM users;

INSERT INTO users (id, user_surname, user_name, user_login, user_password, user_role, CREATED_BY, CREATION_TIME, MODIFIED_BY, MODIFICATION_TIME) VALUES (12345678123456781234567812345671, 'Chandler', 'Sharat', 'user','$2a$04$I9Q2sDc4QGGg5WNTLmsz0.fvGv3OjoZyj81PrSFyGOqMphqfS2qKu', 'USER', 'script', PARSEDATETIME ('31-12-18 11:34:24','dd-MM-yy hh:mm:ss'), 'script', PARSEDATETIME ('31-12-18 11:34:24','dd-MM-yy hh:mm:ss'));
INSERT INTO users (id, user_surname, user_name, user_login, user_password, user_role, CREATED_BY, CREATION_TIME, MODIFIED_BY, MODIFICATION_TIME) VALUES (12345678123456781234567812345672, 'Reinhold', 'Mark', 'admin','$2a$04$I9Q2sDc4QGGg5WNTLmsz0.fvGv3OjoZyj81PrSFyGOqMphqfS2qKu', 'ADMIN', 'script', PARSEDATETIME ('31-12-18 11:34:24','dd-MM-yy hh:mm:ss'), 'script', PARSEDATETIME ('31-12-18 11:34:24','dd-MM-yy hh:mm:ss'));
INSERT INTO users (id, user_surname, user_name, user_login, user_password, user_role, CREATED_BY, CREATION_TIME, MODIFIED_BY, MODIFICATION_TIME) VALUES (12345678123456781234567812345673, 'Systelab', 'Systelab', 'Systelab','$2a$10$9wXu9hshOrtZ7RopythgF.XP93XbKtISBJMpz4PFHG4zv6QjTGBzq', 'ADMIN', 'script',  PARSEDATETIME ('31-12-18 11:34:24','dd-MM-yy hh:mm:ss'), 'script', PARSEDATETIME ('31-12-18 11:34:24','dd-MM-yy hh:mm:ss'));