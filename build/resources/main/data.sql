-- Users
INSERT INTO users (id, name) 
VALUES (1, 'John Doe'),
       (2, 'Jane Smith'),
       (3, 'Mike Johnson'),
       (4, 'Sarah Williams'),
       (5, 'David Brown')
ON CONFLICT (id) DO NOTHING;

-- UserProfiles
INSERT INTO user_profiles (bio, profile_image, user_id) 
VALUES ('Software Developer from NYC', 'john.jpg', 1),
       ('UX Designer', 'jane.jpg', 2),
       ('Product Manager', 'mike.jpg', 3),
       ('Data Scientist', 'sarah.jpg', 4),
       ('Marketing Specialist', 'david.jpg', 5)
ON CONFLICT (user_id) DO NOTHING;

-- Posts
INSERT INTO posts (content, user_id) 
VALUES ('First post content', 1),
       ('Another interesting post', 2),
       ('Hello world!', 3),
       ('Learning Spring Boot', 1),
       ('JPA is awesome', 2)
ON CONFLICT DO NOTHING;

-- Contents
INSERT INTO contents (title, created_at, author_id) 
VALUES ('Spring Boot Tutorial', CURRENT_TIMESTAMP, 1),
       ('JPA Best Practices', CURRENT_TIMESTAMP, 2),
       ('Microservices Architecture', CURRENT_TIMESTAMP, 3)
ON CONFLICT DO NOTHING;

-- Articles
INSERT INTO articles (id, body, category) 
VALUES (1, 'Detailed Spring Boot tutorial content...', 'Technology'),
       (2, 'Best practices for JPA implementation...', 'Programming'),
       (3, 'Understanding microservices architecture...', 'Architecture')
ON CONFLICT (id) DO NOTHING;

-- Videos
INSERT INTO contents (title, created_at, author_id) 
VALUES ('Funny Cat Video', CURRENT_TIMESTAMP, 4),
       ('Travel Vlog', CURRENT_TIMESTAMP, 5)
ON CONFLICT DO NOTHING;

INSERT INTO videos (id, url, duration) 
VALUES (4, 'https://example.com/cat-video', 180),
       (5, 'https://example.com/travel-vlog', 600)
ON CONFLICT (id) DO NOTHING;

-- Comments
INSERT INTO comments (content, post_id, user_id, parent_id) 
VALUES ('Great post!', 1, 2, NULL),
       ('Thanks!', 1, 1, 1),
       ('I agree with this', 1, 3, 1),
       ('Nice tutorial', 2, 4, NULL),
       ('Very helpful', 2, 5, NULL)
ON CONFLICT DO NOTHING;
