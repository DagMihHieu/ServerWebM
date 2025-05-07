-- Insert default roles
INSERT INTO roles (name, description) VALUES 
('ROLE_USER', 'Regular user role'),
('ROLE_ADMIN', 'Administrator role'),
('ROLE_MODERATOR', 'Moderator role');

-- Insert sample users (password for all users: password123)
INSERT INTO users (email, password, full_name, is_active, created_at, updated_at) VALUES 
('admin@example.com', '$2a$10$rDkPvvAFV6GgJkKq8K6UeOQZQZQZQZQZQZQZQZQZQZQZQZQZQZQ', 'Admin User', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('moderator@example.com', '$2a$10$rDkPvvAFV6GgJkKq8K6UeOQZQZQZQZQZQZQZQZQZQZQZQZQZQZQ', 'Moderator User', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user1@example.com', '$2a$10$rDkPvvAFV6GgJkKq8K6UeOQZQZQZQZQZQZQZQZQZQZQZQZQZQZQ', 'Regular User 1', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user2@example.com', '$2a$10$rDkPvvAFV6GgJkKq8K6UeOQZQZQZQZQZQZQZQZQZQZQZQZQZQZQ', 'Regular User 2', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign roles to users
-- Admin user gets ADMIN role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@example.com' AND r.name = 'ROLE_ADMIN';

-- Moderator user gets MODERATOR role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'moderator@example.com' AND r.name = 'ROLE_MODERATOR';

-- Regular users get USER role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email IN ('user1@example.com', 'user2@example.com')
AND r.name = 'ROLE_USER'; 