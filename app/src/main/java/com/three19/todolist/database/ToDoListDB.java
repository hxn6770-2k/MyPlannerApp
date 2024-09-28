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
 * Database helper class for managing ToDo list operations in SQLite.
 */
public class ToDoListDB extends DBConnection {

    private static final String TAG = "ToDoListDB";

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
            ContentValues values = toDo.getContentValuesToAdd();
            long id = db.insert("todolist", null, values);

            if (id != -1) { // Check if insertion was successful
                toDo.setId((int) id);
                Log.i(TAG, "ToDo added successfully: " + toDo.getName());
            } else {
                Log.e(TAG, "Failed to add ToDo: " + toDo.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding ToDo: " + e.getMessage());
        } finally {
            db.close(); // Ensure the database is closed to prevent memory leaks
        }

        return toDo; // Return the ToDo object with its ID
    }

    /**
     * Updates an existing ToDo item in the database.
     * @param toDo The ToDo object containing updated values.
     */
    public void update(ToDo toDo) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = toDo.getContentValuesToUpdate();
            int rowsAffected = db.update("todolist", values, "id = ?", new String[]{String.valueOf(toDo.getId())});

            if (rowsAffected > 0) {
                Log.i(TAG, "ToDo updated successfully: " + toDo.getName());
            } else {
                Log.e(TAG, "Failed to update ToDo: " + toDo.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating ToDo: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    /**
     * Removes a ToDo item from the database by its ID.
     * @param id The unique identifier of the ToDo item to remove.
     */
    public void remove(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            int rowsAffected = db.delete("todolist", "id = ?", new String[]{String.valueOf(id)});
            if (rowsAffected > 0) {
                Log.i(TAG, "ToDo removed successfully with ID: " + id);
            } else {
                Log.e(TAG, "No ToDo found with ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error removing ToDo: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    /**
     * Retrieves a list of all ToDo items from the database.
     * @return A list of ToDo objects.
     */
    public List<ToDo> getList() {
        List<ToDo> toDoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM todolist";

        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    ToDo toDo = new ToDo();
                    toDo.parse(cursor);
                    toDoList.add(toDo);
                } while (cursor.moveToNext());
                Log.i(TAG, "Retrieved " + toDoList.size() + " ToDo items.");
            } else {
                Log.e(TAG, "No ToDo items found.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving ToDo list: " + e.getMessage());
        } finally {
            db.close();
        }

        return toDoList;
    }
}
