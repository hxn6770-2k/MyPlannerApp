package com.three19.todolist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database connection class for managing the SQLite database.
 * This class extends SQLiteOpenHelper to facilitate database creation,
 * version management, and schema updates.
 */
public class DBConnection extends SQLiteOpenHelper {

    // Database version, increment this when schema changes
    private static final int DATABASE_VERSION = 4;

    // Database name
    private static final String DATABASE_NAME = "data";

    /**
     * Constructor for DBConnection.
     *
     * @param context the context in which the database is created or opened
     */
    public DBConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * This is where the database schema is defined.
     *
     * @param db the SQLite database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the todolist table
        String createTableSQL = "CREATE TABLE todolist ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  // Unique ID for each ToDo item
                + "name TEXT NOT NULL"                        // Name/description of the ToDo item
                + ");";
        db.execSQL(createTableSQL);
    }

    /**
     * Called when the database needs to be upgraded.
     * This method is responsible for handling schema changes.
     *
     * @param db   the SQLite database
     * @param oldVersion the old database version
     * @param newVersion the new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing table and create a new one
        db.execSQL("DROP TABLE IF EXISTS todolist");
        onCreate(db);
        // Consider using ALTER TABLE for future version upgrades to avoid data loss
    }

}
