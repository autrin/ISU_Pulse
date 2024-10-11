1. Project Overview

The ISU Pulse application aims to provide Iowa State University (ISU) students, teachers, and administrators with a comprehensive platform to manage schedules, access campus information, and facilitate communication. The app integrates various functionalities such as class schedules, campus maps, real-time chat, and weather updates to enhance the campus experience for all users.
2. Team Members and Roles

    Autrin (Frontend Developer)
        Responsible for designing and implementing the user interface and user experience.
    Bach (Frontend Developer)
        Collaborates on UI/UX development and ensures responsiveness across devices.
    Minh (Backend Developer)
        Develops and maintains server-side logic, database structures, and API integrations.
    Chris (Backend Developer)
        Focuses on backend infrastructure, security, and data management.

3. Core Requirements Alignment
3.1. Different Categories of Users

    Administrators
        Manage student and teacher accounts.
        Edit and oversee class schedules.
        Control access to various app functionalities.
        Send system-wide announcements and urgent messages.
    Teachers/Managers
        Access and modify course schedules.
        Communicate with students through announcements and messages.
        Manage course-related materials and resources.
    Students/Users
        View and manage personal class schedules and calendars.
        Communicate with peers through real-time chat and study groups.
        Access campus maps, building information, bus schedules, and weather updates.
        Explore and review local amenities such as restaurants and entertainment centers.

3.2. Multi-User System

    Supports simultaneous access and interactions from multiple users across different roles.
    Implements efficient session management to handle concurrent operations smoothly.
    Real-time updates ensure that changes made by administrators and teachers reflect promptly for students.
    Enables collaborative features such as study groups and shared schedules.

3.3. Features & Technologies

    Campus Maps Integration
        Utilize Google Maps API to provide detailed maps of ISU buildings, including room numbers and operating hours.
        Offer navigation assistance with suggested routes and paths for new students.
        Include information and reviews for local restaurants and entertainment venues.
    Bus Schedules and Routes
        Fetch real-time bus schedules and route information using relevant transportation APIs.
        Provide suggestions for optimal routes to reach various campus locations.
    System Announcements
        Allow administrators and teachers to send timely and urgent messages to targeted user groups.
        Notifications for important events, deadlines, and campus alerts.
    Calendar and Scheduling
        Enable users to add, edit, and manage personal events and tasks.
        Support for recurring events, start and end times, and event details.
        Option to sync with external calendars and export schedules.
    Real-Time Chat and Study Groups
        Implement real-time, synchronous messaging using technologies like WebSocket.
        Facilitate the creation of study groups for collaborative learning and discussion.
        Allow users to add friends via contacts or NetID and view shared classes.
    Weather Information
        Integrate a weather API to provide up-to-date weather forecasts and conditions relevant to campus activities.

3.4. Complex Database Relationships

    User Management
        Tables for storing detailed information about students, teachers, and administrators.
        Relationships to manage permissions, access levels, and user interactions.
    Course and Schedule Management
        Database structures to handle courses, class sections, and scheduling details.
        Associations between students and enrolled courses, including shared schedules.
    Messaging and Communication
        Tables to store chat histories, group conversations, and announcements.
        Manage friend lists and contact information securely.
    External Data Integration
        Efficient storage and retrieval of data from external sources like Google Maps and weather services.

3.5. Significant Graphical User Interface (GUI)

    Administrator Dashboard
        Intuitive interface for managing users, courses, and system announcements.
        Visualizations for monitoring app usage and system performance.
    Teacher Interface
        Accessible tools for modifying course content, schedules, and communicating with students.
        Easy navigation between different classes and student groups.
    Student Interface
        User-friendly design for accessing schedules, maps, chats, and additional resources.
        Responsive layout optimized for various Android devices.
        Interactive elements for seamless navigation and user engagement.

3.6. Extra Components and Considerations

    AI Assistance
        Integrate GPT-based suggestions to help students choose suitable classes based on interests and degree requirements.
    Building Interior Maps
        Detailed floor plans for campus buildings to assist in locating specific rooms and facilities.
    Threading and Concurrency
        Implement multi-threading where necessary to handle simultaneous data fetches and updates without affecting performance.
    Security and Privacy
        Ensure secure authentication mechanisms, especially when integrating NetID for user identification.
        Adhere to data privacy standards for handling user information and communications.

4. Development Timeline (week)

Week
	

Tasks

1
	

- Set up development environment and tools.

- Finalize project requirements and specifications.

- Distribute initial tasks among team members.

2-3
	

- Develop basic UI layouts for all user roles.

- Set up backend infrastructure and database schemas.

- Begin implementing user authentication and management.

4-5
	

- Integrate calendar and scheduling functionalities.

- Implement course and schedule management systems.

- Develop real-time chat feature framework.

6-7
	

- Incorporate Google Maps and weather API integrations.

- Enhance UI/UX based on feedback and testing.

- Implement system announcement functionalities.

8-9
	

- Develop friend management and social features.

- Optimize database operations and relationships.

- Conduct thorough testing of implemented features.

10-11
	

- Integrate AI assistance for class suggestions.

- Implement security enhancements and privacy measures.

- Perform load and performance testing.

12
	

- Finalize all features and fix identified bugs.

- Prepare documentation and user guides.

- Conduct user acceptance testing and gather feedback.

13-14
	

- Optimize and polish application based on feedback.

- Prepare presentation materials and demo video.

- Deploy the application and perform final evaluations.
5. Expected Deliverables

    Functional Android Application
        A fully integrated and tested app ready for deployment and demonstration.
    Source Code Repository
        Organized and documented codebase maintained through GitLab, showcasing consistent updates and collaborative efforts.
    Technical Documentation
        Comprehensive documentation detailing system architecture, API integrations, database designs, and user guides.
    Demo Presentation
        A concise and informative presentation accompanied by a demo video highlighting key features and use cases.
    Testing Reports
        Detailed reports covering unit tests, integration tests, performance tests, and security assessments.

6. Conclusion

The ISU Pulse application aspires to enhance the academic and social experience of ISU students by consolidating essential campus services into a single, user-friendly platform. With a clear division of responsibilities and a structured development plan, our team is committed to delivering a high-quality application that meets and exceeds user expectations within the allocated semester timeline.

7. License
Shield: [![CC BY-NC-SA 4.0][cc-by-nc-sa-shield]][cc-by-nc-sa]

This work is licensed under a
[Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License][cc-by-nc-sa].

[![CC BY-NC-SA 4.0][cc-by-nc-sa-image]][cc-by-nc-sa]

[cc-by-nc-sa]: http://creativecommons.org/licenses/by-nc-sa/4.0/
[cc-by-nc-sa-image]: https://licensebuttons.net/l/by-nc-sa/4.0/88x31.png
[cc-by-nc-sa-shield]: https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg

The code is for educational purposes and should not be copied or reused by other students for academic work.
