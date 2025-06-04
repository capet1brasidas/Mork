-- Enable foreign keys
PRAGMA foreign_keys = ON;

-- Create User table
CREATE TABLE user_table (
    email TEXT PRIMARY KEY NOT NULL,
    first_name TEXT,
    last_name TEXT,
    password TEXT,
    position TEXT,
    DOB INTEGER,
    image TEXT,
    joinDate INTEGER
);

-- Create Project table
CREATE TABLE project_table (
    projectId INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    startDate INTEGER NOT NULL,
    endDate INTEGER NOT NULL,
    createdAt INTEGER NOT NULL,
    status TEXT NOT NULL,
    createdBy TEXT NOT NULL
);

-- Create Task table
CREATE TABLE task_table (
    taskId INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    startDate INTEGER NOT NULL,
    endDate INTEGER NOT NULL,
    createdAt INTEGER NOT NULL,
    completedAt INTEGER,
    status TEXT NOT NULL,
    projectId INTEGER NOT NULL,  -- Changed back to NOT NULL
    email TEXT NOT NULL,
    FOREIGN KEY (projectId) REFERENCES project_table(projectId) ON DELETE CASCADE
);

-- Create ToDo table
CREATE TABLE todo_table (
    todoId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title TEXT NOT NULL,
    isCompleted INTEGER NOT NULL,
    completedAt INTEGER,
    createdAt INTEGER NOT NULL,
    taskId INTEGER NOT NULL,
    FOREIGN KEY (taskId) REFERENCES task_table(taskId) ON DELETE CASCADE
);

-- Create Tag table
CREATE TABLE tag_table (
    tagId INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    color TEXT NOT NULL
);

-- Create TaskTag junction table
CREATE TABLE task_tag_table (
    taskId INTEGER NOT NULL,
    tagId INTEGER NOT NULL,
    addedAt INTEGER NOT NULL,
    addedBy TEXT NOT NULL,
    PRIMARY KEY (taskId, tagId),
    FOREIGN KEY (taskId) REFERENCES task_table(taskId) ON DELETE CASCADE,
    FOREIGN KEY (tagId) REFERENCES tag_table(tagId) ON DELETE CASCADE
);

-- Create UserProject junction table (simplified to match entity)
CREATE TABLE user_project_table (
    projectId INTEGER NOT NULL,
    email TEXT NOT NULL,
    PRIMARY KEY (projectId, email),
    FOREIGN KEY (projectId) REFERENCES project_table(projectId) ON DELETE CASCADE
);

-- Insert sample data

-- Sample users
INSERT INTO user_table (email, first_name, last_name, password, position, DOB, joinDate) VALUES
('developer@example.com', 'Test', 'Developer', 'password123', 'Developer', strftime('%s', 'now') * 1000, strftime('%s', 'now') * 1000);

-- Sample projects
INSERT INTO project_table (projectId, name, startDate, endDate, createdAt, status, createdBy) VALUES
(1, 'Website Redesign', strftime('%s', 'now') * 1000, strftime('%s', 'now', '+30 days') * 1000, strftime('%s', 'now') * 1000, 'Active', 'admin@example.com'),
(2, 'Mobile App Development', strftime('%s', 'now') * 1000, strftime('%s', 'now', '+60 days') * 1000, strftime('%s', 'now') * 1000, 'Planning', 'admin@example.com'),
(3, 'Database Migration', strftime('%s', 'now') * 1000, strftime('%s', 'now', '+45 days') * 1000, strftime('%s', 'now') * 1000, 'Active', 'admin@example.com');

-- Sample tasks
INSERT INTO task_table (taskId, title, description, startDate, endDate, createdAt, status, projectId, email) VALUES
(1, 'Design Homepage', 'Create new homepage design with modern UI', strftime('%s', 'now') * 1000, strftime('%s', 'now', '+7 days') * 1000, strftime('%s', 'now') * 1000, 'To Do', 1, 'developer@example.com'),
(2, 'Implement User Authentication', 'Set up secure login system', strftime('%s', 'now') * 1000, strftime('%s', 'now', '+14 days') * 1000, strftime('%s', 'now') * 1000, 'In Progress', 2, 'developer@example.com'),
(3, 'Data Schema Design', 'Design new database schema', strftime('%s', 'now') * 1000, strftime('%s', 'now', '+10 days') * 1000, strftime('%s', 'now') * 1000, 'Done', 3, 'developer@example.com'),
(4, 'Implement Navigation', 'Add navigation menu and routing', strftime('%s', 'now') * 1000, strftime('%s', 'now', '+5 days') * 1000, strftime('%s', 'now') * 1000, 'In Progress', 1, 'developer@example.com'),
(5, 'Create API Endpoints', 'Develop REST API endpoints', strftime('%s', 'now') * 1000, strftime('%s', 'now', '+8 days') * 1000, strftime('%s', 'now') * 1000, 'To Do', 2, 'developer@example.com'),
(6, 'Setup Testing Environment', 'Configure testing framework', strftime('%s', 'now') * 1000, strftime('%s', 'now', '+3 days') * 1000, strftime('%s', 'now') * 1000, 'In Progress', 3, 'developer@example.com');


INSERT INTO task_table (taskId, title, description, startDate, endDate, createdAt, completedAt, status, projectId, email) VALUES
(1001, 'Project Planning', 'Outline project milestones and objectives', strftime('%s', '2023-12-01') * 1000, strftime('%s', '2024-01-01') * 1000, strftime('%s', '2023-11-01') * 1000, strftime('%s', '2024-01-10') * 1000, 'Done', 3, 'developer@example.com'),
(1002, 'Team Onboarding', 'Introduce team to project tools and processes', strftime('%s', '2023-12-15') * 1000, strftime('%s', '2024-01-15') * 1000, strftime('%s', '2023-12-01') * 1000, strftime('%s', '2024-02-05') * 1000, 'Done', 3, 'developer@example.com'),
(1003, 'Database Setup', 'Initialize database environment', strftime('%s', '2024-01-05') * 1000, strftime('%s', '2024-02-01') * 1000, strftime('%s', '2024-01-01') * 1000, strftime('%s', '2024-03-10') * 1000, 'Done', 3, 'developer@example.com'),
(1004, 'Schema Optimization', 'Refactor schema for better performance', strftime('%s', '2024-01-15') * 1000, strftime('%s', '2024-02-20') * 1000, strftime('%s', '2024-01-10') * 1000, strftime('%s', '2024-04-05') * 1000, 'Done', 3, 'developer@example.com'),
(1005, 'API Design', 'Draft RESTful API design for CRUD operations', strftime('%s', '2024-02-01') * 1000, strftime('%s', '2024-03-10') * 1000, strftime('%s', '2024-02-01') * 1000, strftime('%s', '2024-05-01') * 1000, 'Done', 3, 'developer@example.com'),
(1006, 'Frontend Layout', 'Create wireframes and base layouts', strftime('%s', '2024-02-15') * 1000, strftime('%s', '2024-03-15') * 1000, strftime('%s', '2024-02-10') * 1000, strftime('%s', '2024-06-01') * 1000, 'Done', 3, 'developer@example.com'),
(1007, 'Backend Logic', 'Implement core business logic in backend', strftime('%s', '2024-03-01') * 1000, strftime('%s', '2024-04-10') * 1000, strftime('%s', '2024-03-01') * 1000, strftime('%s', '2024-07-01') * 1000, 'Done', 3, 'developer@example.com'),
(1008, 'User Authentication', 'Add login, signup, and session features', strftime('%s', '2024-03-10') * 1000, strftime('%s', '2024-04-20') * 1000, strftime('%s', '2024-03-05') * 1000, strftime('%s', '2024-08-01') * 1000, 'Done', 3, 'developer@example.com'),
(1009, 'Role-Based Access', 'Implement RBAC for admin/user roles', strftime('%s', '2024-04-01') * 1000, strftime('%s', '2024-05-05') * 1000, strftime('%s', '2024-04-01') * 1000, strftime('%s', '2024-09-01') * 1000, 'Done', 3, 'developer@example.com'),
(1010, 'UI Components', 'Develop reusable UI components', strftime('%s', '2024-04-15') * 1000, strftime('%s', '2024-05-20') * 1000, strftime('%s', '2024-04-10') * 1000, strftime('%s', '2024-10-01') * 1000, 'Done', 3, 'developer@example.com'),
(1011, 'Form Validation', 'Add client-side and server-side validations', strftime('%s', '2024-05-01') * 1000, strftime('%s', '2024-06-01') * 1000, strftime('%s', '2024-05-01') * 1000, strftime('%s', '2024-11-01') * 1000, 'Done', 3, 'developer@example.com'),
(1012, 'Session Management', 'Handle user sessions and timeouts', strftime('%s', '2024-05-15') * 1000, strftime('%s', '2024-06-15') * 1000, strftime('%s', '2024-05-10') * 1000, strftime('%s', '2024-12-01') * 1000, 'Done', 3, 'developer@example.com'),
(1013, 'Email Integration', 'Set up email notifications', strftime('%s', '2024-06-01') * 1000, strftime('%s', '2024-07-01') * 1000, strftime('%s', '2024-06-01') * 1000, strftime('%s', '2025-01-01') * 1000, 'Done', 3, 'developer@example.com'),
(1014, 'Logging and Monitoring', 'Add logs and error tracking', strftime('%s', '2024-06-15') * 1000, strftime('%s', '2024-07-20') * 1000, strftime('%s', '2024-06-10') * 1000, strftime('%s', '2025-02-01') * 1000, 'Done', 3, 'developer@example.com'),
(1015, 'Performance Testing', 'Conduct load and stress tests', strftime('%s', '2024-07-01') * 1000, strftime('%s', '2024-08-01') * 1000, strftime('%s', '2024-07-01') * 1000, strftime('%s', '2025-03-01') * 1000, 'Done', 3, 'developer@example.com'),
(1016, 'Security Audit', 'Review app for common vulnerabilities', strftime('%s', '2024-07-15') * 1000, strftime('%s', '2024-08-20') * 1000, strftime('%s', '2024-07-10') * 1000, strftime('%s', '2025-04-01') * 1000, 'Done', 3, 'developer@example.com'),
(1017, 'UI Polish', 'Improve visual details and transitions', strftime('%s', '2024-08-01') * 1000, strftime('%s', '2024-09-01') * 1000, strftime('%s', '2024-08-01') * 1000, strftime('%s', '2025-05-01') * 1000, 'Done', 3, 'developer@example.com'),
(1018, 'Beta Release', 'Prepare app for beta testing', strftime('%s', '2024-08-15') * 1000, strftime('%s', '2024-09-15') * 1000, strftime('%s', '2024-08-10') * 1000, strftime('%s', '2025-06-01') * 1000, 'Done', 3, 'developer@example.com'),
(1019, 'Feedback Collection', 'Set up user feedback forms', strftime('%s', '2024-09-01') * 1000, strftime('%s', '2024-10-01') * 1000, strftime('%s', '2024-09-01') * 1000, strftime('%s', '2025-07-01') * 1000, 'Done', 3, 'developer@example.com'),
(1020, 'Accessibility Improvements', 'Ensure WCAG compliance', strftime('%s', '2024-09-15') * 1000, strftime('%s', '2024-10-15') * 1000, strftime('%s', '2024-09-10') * 1000, strftime('%s', '2025-08-01') * 1000, 'Done', 3, 'developer@example.com'),
(1021, 'Code Refactoring', 'Clean up codebase for maintainability', strftime('%s', '2024-10-01') * 1000, strftime('%s', '2024-11-01') * 1000, strftime('%s', '2024-10-01') * 1000, strftime('%s', '2025-09-01') * 1000, 'Done', 3, 'developer@example.com'),
(1022, 'CI/CD Pipeline', 'Set up GitHub Actions for CI/CD', strftime('%s', '2024-10-15') * 1000, strftime('%s', '2024-11-15') * 1000, strftime('%s', '2024-10-10') * 1000, strftime('%s', '2025-10-01') * 1000, 'Done', 3, 'developer@example.com'),
(1023, 'Release Notes Drafting', 'Document version changes', strftime('%s', '2024-11-01') * 1000, strftime('%s', '2024-12-01') * 1000, strftime('%s', '2024-11-01') * 1000, strftime('%s', '2025-11-01') * 1000, 'Done', 3, 'developer@example.com'),
(1024, 'Production Release', 'Deploy to production environment', strftime('%s', '2024-11-15') * 1000, strftime('%s', '2024-12-15') * 1000, strftime('%s', '2024-11-10') * 1000, strftime('%s', '2025-12-01') * 1000, 'Done', 3, 'developer@example.com'),
(1025, 'Post-launch Support', 'Address post-release bugs', strftime('%s', '2024-12-01') * 1000, strftime('%s', '2025-01-01') * 1000, strftime('%s', '2024-12-01') * 1000, strftime('%s', '2026-01-01') * 1000, 'Done', 3, 'developer@example.com'),
(1026, 'User Survey Analysis', 'Analyze feedback from users', strftime('%s', '2024-12-10') * 1000, strftime('%s', '2025-01-10') * 1000, strftime('%s', '2024-12-05') * 1000, strftime('%s', '2026-02-01') * 1000, 'Done', 3, 'developer@example.com'),
(1027, 'App Performance Review', 'Check analytics for performance metrics', strftime('%s', '2025-01-01') * 1000, strftime('%s', '2025-02-01') * 1000, strftime('%s', '2025-01-01') * 1000, strftime('%s', '2026-03-01') * 1000, 'Done', 3, 'developer@example.com'),
(1028, 'Feature Backlog Review', 'Plan next set of feature releases', strftime('%s', '2025-01-15') * 1000, strftime('%s', '2025-02-15') * 1000, strftime('%s', '2025-01-10') * 1000, strftime('%s', '2026-04-01') * 1000, 'Done', 3, 'developer@example.com'),
(1029, 'Scalability Audit', 'Assess system scalability', strftime('%s', '2025-02-01') * 1000, strftime('%s', '2025-03-01') * 1000, strftime('%s', '2025-02-01') * 1000, strftime('%s', '2026-05-01') * 1000, 'Done', 3, 'developer@example.com'),
(1030, 'Final Report', 'Compile final report for stakeholders', strftime('%s', '2025-02-15') * 1000, strftime('%s', '2025-03-15') * 1000, strftime('%s', '2025-02-10') * 1000, strftime('%s', '2026-06-01') * 1000, 'Done', 3, 'developer@example.com');
(1031, 'Cache Layer Design', 'Design Redis-based caching layer', strftime('%s', '2024-11-01') * 1000, strftime('%s', '2024-12-01') * 1000, strftime('%s', '2024-11-01') * 1000, strftime('%s', '2025-01-10') * 1000, 'Done', 3, 'developer@example.com'),
(1032, 'Service Monitoring', 'Integrate Prometheus and Grafana', strftime('%s', '2024-11-15') * 1000, strftime('%s', '2024-12-15') * 1000, strftime('%s', '2024-11-15') * 1000, strftime('%s', '2025-01-20') * 1000, 'Done', 3, 'developer@example.com'),
(1033, 'Unit Test Coverage', 'Achieve 90% test coverage', strftime('%s', '2024-12-01') * 1000, strftime('%s', '2025-01-01') * 1000, strftime('%s', '2024-12-01') * 1000, strftime('%s', '2025-02-01') * 1000, 'Done', 3, 'developer@example.com'),
(1034, 'Bug Bash Session', 'Team-wide bug fixing sprint', strftime('%s', '2024-12-10') * 1000, strftime('%s', '2025-01-10') * 1000, strftime('%s', '2024-12-05') * 1000, strftime('%s', '2025-02-15') * 1000, 'Done', 3, 'developer@example.com'),
(1035, 'Feedback Analysis', 'Compile and analyze user feedback', strftime('%s', '2025-01-01') * 1000, strftime('%s', '2025-02-01') * 1000, strftime('%s', '2025-01-01') * 1000, strftime('%s', '2025-03-01') * 1000, 'Done', 3, 'developer@example.com'),
(1036, 'Feature Freeze', 'Lock new features for final release', strftime('%s', '2025-01-15') * 1000, strftime('%s', '2025-02-15') * 1000, strftime('%s', '2025-01-10') * 1000, strftime('%s', '2025-03-15') * 1000, 'Done', 3, 'developer@example.com'),
(1037, 'Accessibility Testing', 'Verify compliance with accessibility guidelines', strftime('%s', '2025-02-01') * 1000, strftime('%s', '2025-03-01') * 1000, strftime('%s', '2025-02-01') * 1000, strftime('%s', '2025-04-01') * 1000, 'Done', 3, 'developer@example.com'),
(1038, 'Final UI Audit', 'Ensure pixel-perfect UI components', strftime('%s', '2025-02-15') * 1000, strftime('%s', '2025-03-15') * 1000, strftime('%s', '2025-02-10') * 1000, strftime('%s', '2025-04-10') * 1000, 'Done', 3, 'developer@example.com'),
(1039, 'Load Balancer Setup', 'Configure Nginx load balancing', strftime('%s', '2025-03-01') * 1000, strftime('%s', '2025-04-01') * 1000, strftime('%s', '2025-03-01') * 1000, strftime('%s', '2025-05-21') * 1000, 'Done', 3, 'developer@example.com'),
(1040, 'Deployment Automation', 'Automate staging and production deploys', strftime('%s', '2025-03-15') * 1000, strftime('%s', '2025-04-15') * 1000, strftime('%s', '2025-03-10') * 1000, strftime('%s', '2025-05-20') * 1000, 'Done', 3, 'developer@example.com');

-- Additional 15 tasks (2021-2025, various projects/statuses)
INSERT INTO task_table (taskId, title, description, startDate, endDate, createdAt, completedAt, status, projectId, email) VALUES
(2001, 'Legacy Data Migration', 'Migrate old system data', strftime('%s', '2021-05-10') * 1000, strftime('%s', '2021-06-01') * 1000, strftime('%s', '2021-05-01') * 1000, strftime('%s', '2021-06-01') * 1000, 'Done', 1, 'developer@example.com'),
(2002, 'Initial Wireframe', 'Draw base wireframes', strftime('%s', '2022-01-01') * 1000, strftime('%s', '2022-01-20') * 1000, strftime('%s', '2022-01-01') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(2003, 'Setup CI/CD', 'Pipeline config for auto deploy', strftime('%s', '2022-02-10') * 1000, strftime('%s', '2022-02-28') * 1000, strftime('%s', '2022-02-05') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(2004, 'Theme Customization', 'Design consistent theme', strftime('%s', '2023-01-01') * 1000, strftime('%s', '2023-01-20') * 1000, strftime('%s', '2023-01-01') * 1000, strftime('%s', '2023-01-20') * 1000, 'Done', 2, 'developer@example.com'),
(2005, 'Admin Module', 'Build admin features', strftime('%s', '2023-05-01') * 1000, strftime('%s', '2023-06-01') * 1000, strftime('%s', '2023-05-01') * 1000, NULL, 'To Do', 2, 'developer@example.com'),
(2006, 'Testing Strategy', 'Write test plans', strftime('%s', '2024-01-01') * 1000, strftime('%s', '2024-01-31') * 1000, strftime('%s', '2024-01-01') * 1000, NULL, 'In Progress', 3, 'developer@example.com'),
(2007, 'Demo Presentation', 'Prepare presentation deck', strftime('%s', '2024-02-15') * 1000, strftime('%s', '2024-03-01') * 1000, strftime('%s', '2024-02-10') * 1000, strftime('%s', '2024-03-01') * 1000, 'Done', 3, 'developer@example.com'),
(2008, 'Staging Setup', 'Deploy staging env', strftime('%s', '2025-01-10') * 1000, strftime('%s', '2025-01-25') * 1000, strftime('%s', '2025-01-01') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(2009, 'Mobile First Design', 'Responsive adjustments', strftime('%s', '2025-02-01') * 1000, strftime('%s', '2025-02-15') * 1000, strftime('%s', '2025-01-30') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(2010, 'Email Testing', 'Verify email deliverability', strftime('%s', '2025-03-01') * 1000, strftime('%s', '2025-03-10') * 1000, strftime('%s', '2025-03-01') * 1000, NULL, 'Done', 3, 'developer@example.com'),
(2011, 'Script Cleanup', 'Refactor and organize scripts', strftime('%s', '2025-03-15') * 1000, strftime('%s', '2025-03-25') * 1000, strftime('%s', '2025-03-10') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(2012, 'Linting Integration', 'Integrate eslint + formatter', strftime('%s', '2025-04-01') * 1000, strftime('%s', '2025-04-15') * 1000, strftime('%s', '2025-04-01') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(2013, 'Docker Container', 'Build & push container image', strftime('%s', '2025-04-20') * 1000, strftime('%s', '2025-05-05') * 1000, strftime('%s', '2025-04-18') * 1000, NULL, 'Done', 3, 'developer@example.com'),
(2014, 'Client Meeting', 'Schedule and prepare client demo', strftime('%s', '2025-05-01') * 1000, strftime('%s', '2025-05-07') * 1000, strftime('%s', '2025-05-01') * 1000, NULL, 'To Do', 2, 'developer@example.com'),
(2015, 'Documentation', 'Finalize API + Dev docs', strftime('%s', '2025-05-10') * 1000, strftime('%s', '2025-05-20') * 1000, strftime('%s', '2025-05-05') * 1000, NULL, 'In Progress', 3, 'developer@example.com');

-- 30 tasks for May 17–30, 2025, cycling status and projectId
INSERT INTO task_table (taskId, title, description, startDate, endDate, createdAt, completedAt, status, projectId, email) VALUES
(3001, 'Task 1', 'Auto generated task', strftime('%s', '2025-05-17') * 1000, strftime('%s', '2025-05-18') * 1000, strftime('%s', '2025-05-17') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(3002, 'Task 2', 'Auto generated task', strftime('%s', '2025-05-18') * 1000, strftime('%s', '2025-05-19') * 1000, strftime('%s', '2025-05-18') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(3003, 'Task 3', 'Auto generated task', strftime('%s', '2025-05-19') * 1000, strftime('%s', '2025-05-20') * 1000, strftime('%s', '2025-05-19') * 1000, strftime('%s', '2025-05-20') * 1000, 'Done', 3, 'developer@example.com'),
(3004, 'Task 4', 'Auto generated task', strftime('%s', '2025-05-20') * 1000, strftime('%s', '2025-05-21') * 1000, strftime('%s', '2025-05-20') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(3005, 'Task 5', 'Auto generated task', strftime('%s', '2025-05-21') * 1000, strftime('%s', '2025-05-22') * 1000, strftime('%s', '2025-05-21') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(3006, 'Task 6', 'Auto generated task', strftime('%s', '2025-05-22') * 1000, strftime('%s', '2025-05-23') * 1000, strftime('%s', '2025-05-22') * 1000, strftime('%s', '2025-05-23') * 1000, 'Done', 3, 'developer@example.com'),
(3007, 'Task 7', 'Auto generated task', strftime('%s', '2025-05-23') * 1000, strftime('%s', '2025-05-24') * 1000, strftime('%s', '2025-05-23') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(3008, 'Task 8', 'Auto generated task', strftime('%s', '2025-05-24') * 1000, strftime('%s', '2025-05-25') * 1000, strftime('%s', '2025-05-24') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(3009, 'Task 9', 'Auto generated task', strftime('%s', '2025-05-25') * 1000, strftime('%s', '2025-05-26') * 1000, strftime('%s', '2025-05-25') * 1000, strftime('%s', '2025-05-26') * 1000, 'Done', 3, 'developer@example.com'),
(3010, 'Task 10', 'Auto generated task', strftime('%s', '2025-05-26') * 1000, strftime('%s', '2025-05-27') * 1000, strftime('%s', '2025-05-26') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(3011, 'Task 11', 'Auto generated task', strftime('%s', '2025-05-27') * 1000, strftime('%s', '2025-05-28') * 1000, strftime('%s', '2025-05-27') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(3012, 'Task 12', 'Auto generated task', strftime('%s', '2025-05-28') * 1000, strftime('%s', '2025-05-29') * 1000, strftime('%s', '2025-05-28') * 1000, strftime('%s', '2025-05-29') * 1000, 'Done', 3, 'developer@example.com'),
(3013, 'Task 13', 'Auto generated task', strftime('%s', '2025-05-29') * 1000, strftime('%s', '2025-05-30') * 1000, strftime('%s', '2025-05-29') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(3014, 'Task 14', 'Auto generated task', strftime('%s', '2025-05-30') * 1000, strftime('%s', '2025-05-30') * 1000, strftime('%s', '2025-05-30') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(3015, 'Task 15', 'Auto generated task', strftime('%s', '2025-05-17') * 1000, strftime('%s', '2025-05-18') * 1000, strftime('%s', '2025-05-17') * 1000, strftime('%s', '2025-05-18') * 1000, 'Done', 3, 'developer@example.com'),
(3016, 'Task 16', 'Auto generated task', strftime('%s', '2025-05-18') * 1000, strftime('%s', '2025-05-19') * 1000, strftime('%s', '2025-05-18') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(3017, 'Task 17', 'Auto generated task', strftime('%s', '2025-05-19') * 1000, strftime('%s', '2025-05-20') * 1000, strftime('%s', '2025-05-19') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(3018, 'Task 18', 'Auto generated task', strftime('%s', '2025-05-20') * 1000, strftime('%s', '2025-05-21') * 1000, strftime('%s', '2025-05-20') * 1000, strftime('%s', '2025-05-21') * 1000, 'Done', 3, 'developer@example.com'),
(3019, 'Task 19', 'Auto generated task', strftime('%s', '2025-05-21') * 1000, strftime('%s', '2025-05-22') * 1000, strftime('%s', '2025-05-21') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(3020, 'Task 20', 'Auto generated task', strftime('%s', '2025-05-22') * 1000, strftime('%s', '2025-05-23') * 1000, strftime('%s', '2025-05-22') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(3021, 'Task 21', 'Auto generated task', strftime('%s', '2025-05-23') * 1000, strftime('%s', '2025-05-24') * 1000, strftime('%s', '2025-05-23') * 1000, strftime('%s', '2025-05-24') * 1000, 'Done', 3, 'developer@example.com'),
(3022, 'Task 22', 'Auto generated task', strftime('%s', '2025-05-24') * 1000, strftime('%s', '2025-05-25') * 1000, strftime('%s', '2025-05-24') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(3023, 'Task 23', 'Auto generated task', strftime('%s', '2025-05-25') * 1000, strftime('%s', '2025-05-26') * 1000, strftime('%s', '2025-05-25') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(3024, 'Task 24', 'Auto generated task', strftime('%s', '2025-05-26') * 1000, strftime('%s', '2025-05-27') * 1000, strftime('%s', '2025-05-26') * 1000, strftime('%s', '2025-05-27') * 1000, 'Done', 3, 'developer@example.com'),
(3025, 'Task 25', 'Auto generated task', strftime('%s', '2025-05-27') * 1000, strftime('%s', '2025-05-28') * 1000, strftime('%s', '2025-05-27') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(3026, 'Task 26', 'Auto generated task', strftime('%s', '2025-05-28') * 1000, strftime('%s', '2025-05-29') * 1000, strftime('%s', '2025-05-28') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(3027, 'Task 27', 'Auto generated task', strftime('%s', '2025-05-29') * 1000, strftime('%s', '2025-05-30') * 1000, strftime('%s', '2025-05-29') * 1000, strftime('%s', '2025-05-30') * 1000, 'Done', 3, 'developer@example.com'),
(3028, 'Task 28', 'Auto generated task', strftime('%s', '2025-05-30') * 1000, strftime('%s', '2025-05-30') * 1000, strftime('%s', '2025-05-30') * 1000, NULL, 'To Do', 1, 'developer@example.com'),
(3029, 'Task 29', 'Auto generated task', strftime('%s', '2025-05-17') * 1000, strftime('%s', '2025-05-18') * 1000, strftime('%s', '2025-05-17') * 1000, NULL, 'In Progress', 2, 'developer@example.com'),
(3030, 'Task 30', 'Auto generated task', strftime('%s', '2025-05-18') * 1000, strftime('%s', '2025-05-19') * 1000, strftime('%s', '2025-05-18') * 1000, strftime('%s', '2025-05-19') * 1000, 'Done', 3, 'developer@example.com');







-- Sample tags
INSERT INTO tag_table (tagId, name, color) VALUES
(1, 'UI/UX', '#FF0000'),
(2, 'Backend', '#00FF00'),
(3, 'Database', '#0000FF'),
(4, 'High Priority', '#FFFF00');

-- Task-tag relationships
INSERT INTO task_tag_table (taskId, tagId, addedAt, addedBy) VALUES
(1, 1, strftime('%s', 'now') * 1000, 'admin@example.com'),
(2, 2, strftime('%s', 'now') * 1000, 'admin@example.com'),
(3, 3, strftime('%s', 'now') * 1000, 'admin@example.com'),
(1, 4, strftime('%s', 'now') * 1000, 'admin@example.com'),
(2, 4, strftime('%s', 'now') * 1000, 'admin@example.com');

-- Sample todos
INSERT INTO todo_table (todoId, title, isCompleted, createdAt, taskId) VALUES
(1, 'Create wireframes', 0, strftime('%s', 'now') * 1000, 1),
(2, 'Design color scheme', 0, strftime('%s', 'now') * 1000, 1),
(3, 'Research authentication libraries', 0, strftime('%s', 'now') * 1000, 2),
(4, 'Document current schema', 1, strftime('%s', 'now') * 1000, 3),
(5, 'Plan migration steps', 1, strftime('%s', 'now') * 1000, 3),
(6, 'Design navigation layout', 0, strftime('%s', 'now') * 1000, 4),
(7, 'Implement menu components', 0, strftime('%s', 'now') * 1000, 4),
(8, 'Setup route configuration', 1, strftime('%s', 'now') * 1000, 4),
(9, 'Design API structure', 0, strftime('%s', 'now') * 1000, 5),
(10, 'Create endpoint documentation', 0, strftime('%s', 'now') * 1000, 5),
(11, 'Setup Jest framework', 0, strftime('%s', 'now') * 1000, 6),
(12, 'Write initial test cases', 0, strftime('%s', 'now') * 1000, 6),
(13, 'Configure CI pipeline', 0, strftime('%s', 'now') * 1000, 6);

-- Project-user relationships (simplified to match entity)
INSERT INTO user_project_table (projectId, email) VALUES
(1, 'developer@example.com'),
(2, 'developer@example.com'),
(3, 'developer@example.com');

INSERT INTO user_project_table (projectId, email) VALUES
(1, 'admin@example.com'),
(2, 'admin@example.com'),
(3, 'admin@example.com');
