# Database Management Guide for Developers

## Overview
This guide explains how to manage the application's database during development, including how to reset it to a known state using the provided script.

## Prerequisites

### 1. Android Debug Bridge (ADB) Setup

Set up ADB using either **Android Studio** or **manual download**:

---

### Option 1: Install via Android Studio

1. Open **Android Studio**
2. Go to:
   - `File > Settings` (Windows/Linux)
   - `Android Studio > Preferences` (macOS)
3. Navigate to:  
   `Appearance & Behavior > System Settings > Android SDK > SDK Tools`
4. Check **Android SDK Platform-Tools**
5. Click **Apply** to install (if not already installed)

---

### Option 2: Manual Download

1. Download from:  
   [https://developer.android.com/tools/releases/platform-tools](https://developer.android.com/tools/releases/platform-tools)
2. Extract the ZIP to a location like:  
   `C:\android-sdk\platform-tools`

---

### Add ADB to PATH (Required for Both Options)

1. Search for **"Environment Variables"** in the Start menu
2. Click **Environment Variables**
3. Under **User variables**, select `Path` and click **Edit**
4. Click **New** and add the full path to your `platform-tools` folder, e.g.  
   `C:\android-sdk\platform-tools`
5. Click **OK** on all windows
6. Restart your terminal or VS Code terminal

---

### Verify Installation

Run the following in a terminal or command prompt:

```bash
adb version

2. **Android Device/Emulator**
   - Or have an emulator running
   - Verify connection: run `adb devices`, your emulator should appear below

3. **App Setup**
   - App must be built in debug mode

4. **Git Bash**
   - Install Git for Windows (includes Git Bash)
   - Open Git Bash from project directory

### Prerequisites for update .db file 

- Use SQLite's Official Precompiled Binaries
- Go to: https://www.sqlite.org/download.html

- Under Precompiled Binaries for Windows, download:

- sqlite-tools-win32-x86-*.zip (contains sqlite3.exe)

- Unzip it.

- Move the sqlite3.exe to a folder like C:\sqlite or directly to your project folder.

- (Optional) Add that folder to your system PATH:

- So you can run sqlite3 from any terminal.
- After installation, restart your terminal and run:
```bash
sqlite3 --version
```



## Directory Structure

```
app/src/main/java/com/example/fit5046a2/data/
├── data_storage/
│   ├── initial_data.sql    # SQL script for database initialization
│   ├── app_database.db     # SQLite database file
│   └── backupDB/          # Directory for database backups
├── database/
│   └── AppDatabase.kt     # Room database configuration
└── entity/                # Entity classes for Room
```
## Database Management Tools

We provide several scripts to manage the database:

### db_manage.sh (Main Management Tool)

This is the primary tool for database management, offering a menu-driven interface:

```bash
./db_manage.sh
```

Options:
1. Create new database from SQL
   - Creates app_database.db from initial_data.sql
   - Places it in data_storage directory
   
2. Reset app database
   - Backs up current database to data_storage/backupDB/
   - Replaces app's database with fresh copy
   
3. Create and reset (both steps)
   - Combines options 1 and 2
   
4. Exit


## Common Tasks

### 1. Initial Setup
- in get bash

```bash
# Make script executable
chmod +x db_manage.sh

# Run the management tool
./db_manage.sh

# Choose option 1 to create initial database
```

### 2. Resetting the Database

```bash
./db_manage.sh

# Choose option 2 to reset
```

The old database will be backed up to:
`data_storage/backupDB/app_database_YYYYMMDD_HHMMSS`

### 3. Modifying Initial Data

1. Edit `initial_data.sql`
2. Run `db_manage.sh`
3. Choose option 1 to recreate database
4. Choose option 2 to reset app's database


## Troubleshooting

### Common Issues

1. "adb not recognized"
   - Solution: Add Android SDK Platform Tools to PATH
   - Verify with: `adb version`

2. No devices found
   - Check: `adb devices`
   - Ensure USB debugging is enabled
   - Reconnect device/restart emulator

3. Permission denied
   - Ensure app is debug build
   - Check package name matches
   - Verify ADB can access app data:
     ```
     adb shell "run-as com.example.fit5046a2 ls /data/data/com.example.fit5046a2/databases"
     ```

4. Database not copying
   - Verify initial_database.db exists in data folder
   - Check file permissions
   - Ensure app has write permissions

1. **SQLite not found**
   ```bash
   Error: sqlite3: command not found
   Solution: Install SQLite3 CLI
   ```

2. **ADB device not found**
   ```bash
   Error: no devices/emulators found
   Solution: Connect device and authorize USB debugging
   ```

3. **Permission denied**
   ```bash
   Error: Permission denied
   Solution: chmod +x db_manage.sh
   ```


## Best Practices
1. Always backup production data before testing
2. Use consistent test data across team
3. Document any changes to database schema
4. Version control your test database
5. Reset database before running critical tests

# Database Structure

## Core Entities

### 1. User (`user_table`)
```kotlin
User(
    email: String (PrimaryKey),           // User's email as unique identifier
    first_name: String?,                  // User's first name
    last_name: String?,                   // User's last name
    password: String?,                    // User's password
    position: String?,                    // User's position (default: "employee")
    DOB: Long?,                           // Date of birth (timestamp)
    image: String?,                       // Profile image URL
    joinDate: Long?                       // Join date (timestamp)
)
```

### 2. Project (`project_table`)
```kotlin
Project(
    projectId: Int? (PrimaryKey),        // Auto-generated project ID
    name: String,                        // Project name
    startDate: Long,                     // Project start date
    endDate: Long,                       // Project end date
    createdAt: Long,                     // Creation timestamp
    status: String,                      // Project status
    createdBy: String                    // References User.email
)
// Status options: ["Planning", "Active", "On Hold", "Completed", "Cancelled"]
```

### 3. Task (`task_table`)
```kotlin
Task(
    taskId: Int? (PrimaryKey),          // Auto-generated task ID
    title: String,                      // Task title
    description: String?,               // Task description
    startDate: Long,                    // Task start date
    endDate: Long,                      // Task end date
    createdAt: Long,                    // Creation timestamp
    completedAt: Long?,                 // Completion timestamp
    status: String,                     // Task status
    projectId: String,                  // References Project.projectId
    email: String                       // References User.email
)
// Status options: ["Pending", "In Progress", "Completed", "Overdue"]
// Priority options: ["Low", "Medium", "High"]
```

### 4. ToDo (`todo_table`)
```kotlin
ToDo(
    todoId: Int? (PrimaryKey),          // Auto-generated todo ID
    title: String,                      // Todo item title
    isCompleted: Boolean,               // Completion status
    completedAt: Long?,                 // Completion timestamp
    createdAt: Long,                    // Creation timestamp
    taskId: String                      // References Task.taskId
)
```

### 5. Tag (`tag_table`)
```kotlin
Tag(
    tagId: String (PrimaryKey),         // Tag identifier
    name: String,                       // Tag name
    tasks: List<Task>,                  // Associated tasks (stored as JSON)
    usageCount: Int,                    // Times tag was used
    lastUsedAt: Long                    // Last usage timestamp
)
```

## Relationship Tables

### 1. TaskTag (`task_tag_table`)
```kotlin
TaskTag(
    taskId: Int,                        // References Task.taskId
    tagId: Int,                         // References Tag.tagId
    addedAt: Long,                      // When tag was added
    addedBy: String                     // References User.email
)
// Composite primary key: [taskId, tagId]
```

## Relationships

1. Project & User:
   - Projects are created by users (createdBy -> User.email)
   - Users can be assigned to multiple projects

2. Task & Project:
   - Tasks belong to a project (projectId -> Project.projectId)
   - Projects can have multiple tasks
   - Tasks are assigned to users (email -> User.email)

3. ToDo & Task:
   - ToDos belong to a task (taskId -> Task.taskId)
   - Tasks can have multiple ToDos

4. Task & Tag:
   - Many-to-many relationship through TaskTag table
   - Tags track usage statistics
   - Tag assignments are tracked with timestamp and user

## Data Types
- Timestamps are stored as Long (milliseconds since epoch)
- Boolean for simple flags
- Nullable fields use appropriate nullable types (String?, Long?, Int?)
- Lists are stored as JSON strings using TypeConverters
- Foreign keys use CASCADE delete where appropriate

## Notes
- Email is used as the primary user identifier throughout the system
- Timestamps are used extensively for tracking creation, completion, and usage
- Status fields use predefined options for consistency
- Most entities track their creation timestamp
- Many-to-many relationships use junction tables with additional metadata


