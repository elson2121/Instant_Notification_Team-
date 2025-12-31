This README.md file summarizes your project’s architecture, security features, and database structure. It is designed to look professional for your teacher or a GitHub repository.

Instant Notification System (JavaFX & MySQL)
A professional desktop application designed for real-time internal communication. The system features separate User and Admin dashboards, allowing administrators to broadcast messages across multiple channels like Email and SMS.

🚀 Core Features
1. Multi-Channel Admin Dashboard
Dynamic Statistics: Displays real-time data for Total Users, Notifications Sent, and Delivery rates using a symmetrical GridPane layout.

Targeted Broadcasting: Admins can filter recipients by Department, Role, Gender, or Shift.

Channel Selection: Support for sending notifications via Email, SMS, Telegram, and Dashboard.

2. Intelligent User Profile Management
Role Selection: Integrated a ComboBox for roles (replacing static text) to ensure data integrity and a better user experience.

Live Validation: Registration inputs are validated in real-time. The Full Name field blocks numbers, and the Phone Number field enforces the +251 Ethiopian format with a 9-digit limit.

Visual Feedback: Implemented "Red Box" error highlighting for invalid inputs to guide the user during registration.

3. Notification & History Feed
Unseen Feed: Users receive instant alerts in a "New Notifications" section with high-contrast dark green text.

History Tracking: A dedicated History tab allows users to review past messages, categorized by the delivery channel used (e.g., Email tags).

🛠 Technical Stack
Frontend: JavaFX (FXML) with custom CSS for a corporate "Light Green" theme.

Backend: Java (JDBC) utilizing the Singleton Pattern for efficient database connections.

Database: MySQL with an optimized schema for tracking notification status and user metadata.

🔒 Security & Best Practices
Environment Configuration: Sensitive credentials (DB passwords, API keys) are stored in an external config.properties file.

Git Protection: A .gitignore file is used to prevent the exposure of private configuration files in public repositories.

Data Integrity: Used Incremental Database Migrations to expand the system's capabilities safely by adding boolean columns for new notification channels.

📊 Database Schema (Highlights)
The notifications table was expanded to support a multi-channel architecture:

SQL

ALTER TABLE notifications
ADD COLUMN send_email BOOLEAN DEFAULT FALSE,
ADD COLUMN send_sms BOOLEAN DEFAULT FALSE,
ADD COLUMN send_telegram BOOLEAN DEFAULT FALSE;
(Reference:)
