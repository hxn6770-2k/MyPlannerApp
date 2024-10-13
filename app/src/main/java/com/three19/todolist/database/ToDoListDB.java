package com.three19.todolist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.three19.todolist.model.ToDo;

import java.util.ArrayList;
import java.util.List;

/**
 * Programmer: Vivian Nguyen
 * Contact: viviannguyen726@gmail.com
 * Date: October 2024
 * Version: 1.3
 *
 * Purpose: This class manages the SQLite database operations for the ToDo list application.
 * It handles the addition, updating, removal, and retrieval of ToDo items from the database.
 *
 * Enhancements:
 * - Provides functionality to add, update, and delete ToDo items efficiently.
 * - Implements logging to track database operations and identify potential issues.
 * - Sorting algorithms can be applied externally to the retrieved list for flexible task management.
 *
 * Issues:
 * - Ensure proper error handling in database operations to avoid crashes.
 * - Future enhancements should consider optimizing data retrieval methods for larger datasets.
 *
 * Efficiency Considerations:
 * - Database operations are performed using SQLite, where the time complexity for
 *   typical operations like insertions and deletions is O(1).
 * - The retrieval of ToDo items is efficient for small datasets but may require optimization
 *   through indexing as the dataset grows.
 */
public class ToDoListDB extends DBConnection {

    private static final String TAG = "ToDoListDB"; // Tag for logging

    public ToDoListDB(Context context) {
        super(context);
    }

    /**
     * Adds a new ToDo item to the database.
     *
     * @param toDo The ToDo object to be added.
     * @return The newly created ToDo object with its assigned ID. If insertion fails, the ID will be set to -1.
     */
    public ToDo add(ToDo toDo) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = toDo.getContentValuesToAdd(); // Prepare values for insertion
            long id = db.insert("todolist", null, values); // Insert the ToDo item into the database

            if (id != -1) { // Check if insertion was successful
                toDo.setId((int) id); // Set the generated ID to the ToDo object
                Log.i(TAG, "ToDo added successfully: " + toDo.getName());
            } else {
                Log.e(TAG, "Failed to add ToDo: " + toDo.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding ToDo: " + e.getMessage()); // Log any exceptions
        } finally {
            db.close(); // Ensure the database is closed to prevent memory leaks
        }

        return toDo; // Return the ToDo object with its ID
    }

    /**
     * Updates an existing ToDo item in the database.
     *
     * @param toDo The ToDo object containing updated values.
     */
    public void update(ToDo toDo) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = toDo.getContentValuesToUpdate(); // Prepare values for update
            int rowsAffected = db.update("todolist", values, "id = ?", new String[]{String.valueOf(toDo.getId())}); // Update the ToDo item

            if (rowsAffected > 0) {
                Log.i(TAG, "ToDo updated successfully: " + toDo.getName());
            } else {
                Log.e(TAG, "Failed to update ToDo: " + toDo.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating ToDo: " + e.getMessage()); // Log any exceptions
        } finally {
            db.close(); // Ensure the database is closed
        }
    }

    /**
     * Removes a ToDo item from the database by its ID.
     *
     * @param id The unique identifier of the ToDo item to remove.
     */
    public void remove(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            int rowsAffected = db.delete("todolist", "id = ?", new String[]{String.valueOf(id)}); // Delete the ToDo item
            if (rowsAffected > 0) {
                Log.i(TAG, "ToDo removed successfully with ID: " + id);
            } else {
                Log.e(TAG, "No ToDo found with ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error removing ToDo: " + e.getMessage()); // Log any exceptions
        } finally {
            db.close(); // Ensure the database is closed
        }
    }

    /**
     * Retrieves a list of all ToDo items from the database.
     *
     * @return A list of ToDo objects.
     */
    public List<ToDo> getList() {
        List<ToDo> toDoList = new ArrayList<>(); // List to hold retrieved ToDo items
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM todolist"; // Query to select all ToDo items

        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    ToDo toDo = new ToDo(); // Create a new ToDo object
                    toDo.parse(cursor); // Populate the ToDo object with data from the cursor
                    toDoList.add(toDo); // Add the ToDo object to the list
                } while (cursor.moveToNext());
                Log.i(TAG, "Retrieved " + toDoList.size() + " ToDo items."); // Log the number of items retrieved
            } else {
                Log.e(TAG, "No ToDo items found."); // Log if no items were found
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving ToDo list: " + e.getMessage()); // Log any exceptions
        } finally {
            db.close(); // Ensure the database is closed
        }

        return toDoList; // Return the list of ToDo items
    }
}
