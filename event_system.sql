CREATE DATABASE IF NOT EXISTS event_system;
USE event_system;

CREATE TABLE IF NOT EXISTS events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    location VARCHAR(100),
    date DATE
);

CREATE TABLE IF NOT EXISTS attendees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS event_registrations (
    event_id INT,
    attendee_id INT,
    PRIMARY KEY(event_id, attendee_id),
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (attendee_id) REFERENCES attendees(id)
);
show tables
INSERT INTO events (name, location, date) VALUES
('Birthday', 'Pune', '2025-07-17'),
('Cultural Event', 'Talegaon', '2025-08-25');
INSERT INTO attendees (name, email) VALUES
('Tejas Dhule', 'tejasdhule@example.com'),
('Tejas Jagdle', 'tejasjagdle@example.com'),
('Jaydeep', 'jaydeep@example.com');
SELECT * FROM events;
SELECT * FROM attendees;
INSERT INTO event_registrations (event_id, attendee_id) VALUES
(1, 1),
(2, 2);
