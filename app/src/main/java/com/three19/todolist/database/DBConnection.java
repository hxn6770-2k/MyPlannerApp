package com.three19.todolist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Programmer: Vivian Nguyen
 * Contact: viviannguyen726@gmail.com
 * Date: October 2024
 * Version: 1.2
 *
 * Purpose: This class manages the SQLite database connection for the ToDo list app.
 * It is responsible for creating, upgrading, and handling the database schema.
 * The database stores ToDo items, including their name, deadline, and priority.
 *
 * Enhancement: Added columns for deadline and priority to the ToDo items in the schema.
 *
 * Future improvements should focus on implementing sorting mechanisms to manage tasks
 * effectively based on deadlines and priority levels.
 *
 * Efficiency Considerations:
 * - The database operations are performed using SQL commands, where the time complexity
 *   of typical operations like table creation and deletion is O(1).
 * - The database's schema creation on first launch is efficient for small datasets but
 *   may require optimization with indexing as the dataset grows.
 */
public class DBConnection extends SQLiteOpenHelper {

    // Database version, increment this when schema changes (version management)
    private static final int DATABASE_VERSION = 5; // Versioning for upgrades
    // Name of the SQLite database file
    private static final String DATABASE_NAME = "data"; // Short, descriptive database name

    /**
     * Constructor for DBConnection.
     *
     * @param context the context in which the database is created or opened
     * Purpose: This constructor is responsible for setting up the database with the
     *          specified name and version.
     */
    public DBConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * This method defines the schema of the "todolist" table, which includes columns for
     * task ID, name, deadline, and priority.
     *
     * Time Complexity: O(1) for table creation. The create table operation is typically
     * a constant-time operation since the schema is predefined.
     *
     * @param db the SQLite database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create the "todolist" table. The table includes:
        // - A primary key (id) which is automatically incremented
        // - A name for the task (cannot be null)
        // - A deadline for the task (cannot be null)
        // - A priority level, which is optional and can store integer values (NULL allowed)
        String createTableSQL = "CREATE TABLE todolist ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  // Unique ID for each ToDo item
                + "name TEXT NOT NULL, "                      // Name/description of the ToDo item
                + "deadline TEXT NOT NULL, "                  // Deadline for the ToDo item
                + "priority INTEGER"                          // Priority of the ToDo item (optional)
                + ");";
        // Executes the SQL to create the table
        db.execSQL(createTableSQL);
    }

    /**
     * Called when the database needs to be upgraded. This method is triggered if the
     * database version changes (from DATABASE_VERSION).
     *
     * Time Complexity: O(1) for dropping and recreating the table. However,
     * future versions should use ALTER TABLE to prevent data loss, as dropping the table
     * leads to a complete loss of stored data.
     *
     * @param db the SQLite database
     * @param oldVersion the old database version
     * @param newVersion the new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing table if it exists, which will result in data loss.
        db.execSQL("DROP TABLE IF EXISTS todolist");

        // Create a new version of the table with updated schema if necessary
        onCreate(db);
    }
}
