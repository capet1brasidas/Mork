#!/bin/bash

#!/bin/bash

# Function to backup current database
backup_database() {
    echo "Backing up current database..."
    
    # Get timestamp for backup
    timestamp=$(date +%Y%m%d_%H%M%S)
    
    # Set backup directory
    BACKUP_DIR="app/src/main/java/com/example/fit5046a2/data/data_storage/backupDB"
    mkdir -p "$BACKUP_DIR"
    
    # Copy current database from assets to backup
    cp "app/src/main/assets/app_database.db" "$BACKUP_DIR/app_database_${timestamp}"
    
    if [ -f "$BACKUP_DIR/app_database_${timestamp}" ]; then
        echo "Database backed up successfully to: $BACKUP_DIR/app_database_${timestamp}"
        echo -e "\nPress Enter to return to menu..."
        read
        return 0
    else
        echo "Error: Backup failed!"
        echo -e "\nPress Enter to return to menu..."
        read
        return 1
    fi
}

# Function to create and reset database
create_and_reset_database() {
    echo "Creating new database and resetting app..."
    
    # Set paths
    ASSETS_DIR="app/src/main/assets"
    SQL_FILE="app/src/main/java/com/example/fit5046a2/data/data_storage/initial_data.sql"
    DB_FILE="$ASSETS_DIR/app_database.db"
    
    # Create assets directory if needed
    mkdir -p "$ASSETS_DIR"
    
    # Remove existing database if it exists
    [ -f "$DB_FILE" ] && rm "$DB_FILE"
    
    echo "Creating new database from SQL..."
    
    # Create new database from SQL with WAL mode disabled
    sqlite3 "$DB_FILE" <<EOF
.mode column
.headers on
PRAGMA journal_mode=DELETE;
PRAGMA foreign_keys=ON;
.read "$SQL_FILE"
.tables
EOF
    
    # Set proper permissions
    chmod 644 "$DB_FILE"
    
    if [ ! -f "$DB_FILE" ]; then
        echo "Error: Failed to create database!"
        return 1
    fi
    
    echo "Database created successfully in assets folder!"
    echo -e "\nResetting app database..."

    
    # Copy new database to app
    adb push "$DB_FILE" /data/local/tmp/
    adb shell "run-as com.example.fit5046a2 cp /data/local/tmp/app_database.db /data/data/com.example.fit5046a2/databases/app_database"
    
    if adb shell "run-as com.example.fit5046a2 test -f /data/data/com.example.fit5046a2/databases/app_database"; then
        echo "Database reset successful!"
        echo -e "\nNew database tables:"
        adb shell "run-as com.example.fit5046a2 sqlite3 /data/data/com.example.fit5046a2/databases/app_database '.tables'"
        return 0
    else
        echo "Error: Database reset failed!"
        return 1
    fi
}

# Main menu loop
while true; do
    clear
    echo "Database Management Tool"
    echo "1. Backup current database"
    echo "2. Create new database and reset app"
    echo "3. Exit"
    read -p "Choose an option (1-3): " choice

    case $choice in
        1)
            backup_database
            ;;
        2)
            create_and_reset_database
            ;;
        3)
            echo "Exiting..."
            exit 0
            ;;
        *)
            echo "Invalid option"
            echo -e "\nPress Enter to continue..."
            read
            ;;
    esac
done
