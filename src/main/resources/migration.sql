SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO users (username, password, email, enabled)
VALUES ('default_user', '$2a$10$rRYJJKlNtpmuU6NIVf3MAO1hzStwdrlK0MbL0OgutEh2Q3P9JG5AO', 'default@example.com', TRUE)
ON DUPLICATE KEY UPDATE username = VALUES(username);

UPDATE weight_records
SET user_id = (SELECT id FROM users WHERE username = 'default_user')
WHERE user_id = 1;

SET FOREIGN_KEY_CHECKS = 1;