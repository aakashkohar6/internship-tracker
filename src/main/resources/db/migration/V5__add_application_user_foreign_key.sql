ALTER TABLE internship_applications
ADD CONSTRAINT fk_application_user
FOREIGN KEY (user_id) REFERENCES users(id);