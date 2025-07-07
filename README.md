# Event_Registration_System
A simple command-line-based Event Registration System built using **Java**, **JDBC**, and **MySQL**.  
This project demonstrates CRUD operations, database connectivity, and object-oriented programming concepts in a real-world context.

---

## 📌 Features

- ✅ Create and list events
- 👥 Register attendees for events
- 👀 View attendees per event
- ❌ Cancel attendee registration
- 💾 Uses MySQL with JDBC for persistent storage

---

## 🛠️ Tech Stack
- **Java** (Core)
- **JDBC** (Java Database Connectivity)
- **MySQL** (Relational Database)
- **VS Code** with Java Pack Extension

---

## 🗃️ Database Schema (MySQL)
Run the following SQL to set up the database:

```sql
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

## 📌  project Structure
EventRegistrationSystem/
│
├── EventRegistrationSystem.java       # All logic (main + DB + DAO)
├── event_system.sql                   # SQL script to create tables
├── lib/
│   └── mysql-connector-java-8.x.x.jar # MySQL JDBC driver
└── .vscode/
    ├── tasks.json                     # Build tasks
    └── launch.json                    # Run configuration

🚀 How to Run
1. Install Requirements
Java JDK 11+
MySQL Server
MySQL JDBC Driver (place .jar in /lib folder)
VS Code with Java Extension Pack

2. Set Up MySQL
Create the database and tables using event_system.sql
Update your MySQL credentials in DBConnection.getConnection() inside EventRegistrationSystem.java

3. Build and Run (VS Code)
To Compile:
Use Ctrl+Shift+B or run:
javac -cp "lib/*" EventRegistrationSystem.java


📚 Concepts Covered
Java OOP (Encapsulation, Abstraction)
JDBC (PreparedStatement, ResultSet)
SQL (DDL, DML, Joins, Constraints)
VS Code Java Debug and Tasks
Command-line Java app structure

📌 Future Improvements
Add GUI using JavaFX or Swing
Email confirmation using JavaMail
Admin dashboard for event overview
Export reports to CSV/PDF

🙋‍♂️ Author
Tejas Dhule
Feel free to fork, clone, and build upon it! Contributions welcome.
