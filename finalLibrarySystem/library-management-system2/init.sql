-- Создание ролей
INSERT INTO roles (name) VALUES ('USER'), ('ADMIN') ON CONFLICT (name) DO NOTHING;

-- Создание администратора
INSERT INTO users (username, password, full_name, email, created_at, updated_at)
VALUES ('admin', 'admin123', 'Администратор', 'admin@library.ru', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (username) DO NOTHING;

-- Добавление роли ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
    ON CONFLICT (user_id, role_id) DO NOTHING;

-- Создание пользователя
INSERT INTO users (username, password, full_name, email, created_at, updated_at)
VALUES ('user1', 'user123', 'Иван Петров', 'user1@mail.ru', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'user1' AND r.name = 'USER'
    ON CONFLICT (user_id, role_id) DO NOTHING;