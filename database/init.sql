CREATE DATABASE IF NOT EXISTS internship_tracker_test;

USE internship_tracker_test;

CREATE TABLE IF NOT EXISTS internship_applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company VARCHAR(100),
    position VARCHAR(100),
    status VARCHAR(30),
    location VARCHAR(100)
    );
