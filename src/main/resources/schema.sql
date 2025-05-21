CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    target_weight DOUBLE,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE weight_records
    MODIFY user_id INT NOT NULL,
    ADD CONSTRAINT fk_user_id 
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE;

-- 初期管理者ユーザーの作成（パスワードは 'admin' のハッシュ）
INSERT INTO users (username, password, email, enabled)
VALUES ('admin', '$2a$10$rRYJJKlNtpmuU6NIVf3MAO1hzStwdrlK0MbL0OgutEh2Q3P9JG5AO', 'admin@example.com', TRUE)
ON DUPLICATE KEY UPDATE username = VALUES(username);