# 3 Roy 2

1. Project Overview

The ISU Pulse application aims to provide Iowa State University (ISU) students, teachers, and administrators with a comprehensive platform to manage schedules, access campus information, and facilitate communication. The app integrates various functionalities such as class schedules, campus maps, real-time chat, and weather updates to enhance the campus experience for all users.
2. Team Members and Roles

    Autrin (Frontend Developer)
        Responsible for designing and implementing the user interface and user experience.
        May assist with backend tasks as needed to ensure seamless integration.
    Bach (Frontend Developer)
        Collaborates on UI/UX development and ensures responsiveness across devices.
        Available to support backend development when required.
    Minh (Backend Developer)
        Develops and maintains server-side logic, database structures, and API integrations.
        Assists frontend team with minor tasks and features to ensure cohesive functionality.
    Chris (Backend Developer)
        Focuses on backend infrastructure, security, and data management.
        Provides support to frontend development with minor tasks and feature implementations.

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

4. Additional Considerations and Open Questions

    Integration with Workday
        Question: Can the app sync class schedules directly with Workday, or should classes be added manually?
        Consideration: Integrating with Workday would enhance user convenience but may require compliance and access permissions. Alternatively, implementing a manual add feature initially with plans for future integration.
    Calendar Export and Subscription
        Question: Should the calendar be exportable and support subscription features?
        Consideration: Providing export and subscription capabilities increases flexibility for users to integrate schedules with other calendar applications.
    User Access Scope
        Question: Should the app be exclusive to ISU students, or open to a broader audience?
        Consideration: Limiting access to ISU students ensures focused functionality and security. Utilizing NetID for authentication reinforces this exclusivity.
    Map and Weather Feature Relevance
        Question: Do map and weather integrations significantly benefit the app's purpose?
        Consideration: Including these features enhances the overall utility by assisting with daily planning and navigation, especially beneficial for new students.
    Friend and Class Interaction
        Question: Is allowing friends to see each other's class schedules and inviting friends to classes beneficial?
        Consideration: These social features promote collaboration and community building among students, aligning with the app's goal to facilitate academic and social interactions.
    Invitation and Onboarding
        Question: Should there be functionality to invite others to the app?
        Consideration: Implementing an invitation system can aid in user acquisition and encourage widespread adoption among the student body.

5. Development Timeline (week)

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
6. Expected Deliverables

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

7. Conclusion

The ISU Pulse application aspires to enhance the academic and social experience of ISU students by consolidating essential campus services into a single, user-friendly platform. With a clear division of responsibilities and a structured development plan, our team is committed to delivering a high-quality application that meets and exceeds user expectations within the allocated semester timeline.

We welcome feedback and suggestions to further refine this proposal and ensure its alignment with project goals and stakeholder expectations


-------------------------------------------------------------------------------
## Getting started

To make it easy for you to get started with GitLab, here's a list of recommended next steps.

Already a pro? Just edit this README.md and make it your own. Want to make it easy? [Use the template at the bottom](#editing-this-readme)!

## Add your files

- [ ] [Create](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#create-a-file) or [upload](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#upload-a-file) files
- [ ] [Add files using the command line](https://docs.gitlab.com/ee/gitlab-basics/add-file.html#add-a-file-using-the-command-line) or push an existing Git repository with the following command:

```
cd existing_repo
git remote add origin https://git.las.iastate.edu/cs309/2024fall/3_roy_2.git
git branch -M main
git push -uf origin main
```

## Integrate with your tools

- [ ] [Set up project integrations](https://git.las.iastate.edu/cs309/2024fall/3_roy_2/-/settings/integrations)

## Collaborate with your team

- [ ] [Invite team members and collaborators](https://docs.gitlab.com/ee/user/project/members/)
- [ ] [Create a new merge request](https://docs.gitlab.com/ee/user/project/merge_requests/creating_merge_requests.html)
- [ ] [Automatically close issues from merge requests](https://docs.gitlab.com/ee/user/project/issues/managing_issues.html#closing-issues-automatically)
- [ ] [Enable merge request approvals](https://docs.gitlab.com/ee/user/project/merge_requests/approvals/)
- [ ] [Set auto-merge](https://docs.gitlab.com/ee/user/project/merge_requests/merge_when_pipeline_succeeds.html)

## Test and Deploy

Use the built-in continuous integration in GitLab.

- [ ] [Get started with GitLab CI/CD](https://docs.gitlab.com/ee/ci/quick_start/index.html)
- [ ] [Analyze your code for known vulnerabilities with Static Application Security Testing (SAST)](https://docs.gitlab.com/ee/user/application_security/sast/)
- [ ] [Deploy to Kubernetes, Amazon EC2, or Amazon ECS using Auto Deploy](https://docs.gitlab.com/ee/topics/autodevops/requirements.html)
- [ ] [Use pull-based deployments for improved Kubernetes management](https://docs.gitlab.com/ee/user/clusters/agent/)
- [ ] [Set up protected environments](https://docs.gitlab.com/ee/ci/environments/protected_environments.html)

***

# Editing this README

When you're ready to make this README your own, just edit this file and use the handy template below (or feel free to structure it however you want - this is just a starting point!). Thanks to [makeareadme.com](https://www.makeareadme.com/) for this template.

## Suggestions for a good README

Every project is different, so consider which of these sections apply to yours. The sections used in the template are suggestions for most open source projects. Also keep in mind that while a README can be too long and detailed, too long is better than too short. If you think your README is too long, consider utilizing another form of documentation rather than cutting out information.

## Name
Choose a self-explaining name for your project.

## Description
Let people know what your project can do specifically. Provide context and add a link to any reference visitors might be unfamiliar with. A list of Features or a Background subsection can also be added here. If there are alternatives to your project, this is a good place to list differentiating factors.

## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.
