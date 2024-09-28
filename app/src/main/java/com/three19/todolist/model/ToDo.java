package com.three19.todolist.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ToDo {
    // Unique identifier for the ToDo item
    private int id;

    // Name/description of the ToDo item
    private String name;

    // Deadline for completing the ToDo item, formatted as "yyyy-MM-dd"
    private String deadline;

    // Priority level of the ToDo item (1 = Low, 2 = Medium, 3 = High)
    private int priority;

    // Getter for ID
    public int getId() {
        return id;
    }

    // Setter for ID
    public void setId(int value) {
        id = value;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String value) {
        name = value;
    }

    // Getter for deadline
    public String getDeadline() {
        return deadline;
    }

    // Setter for deadline
    public void setDeadline(String value) {
        deadline = value;
    }

    // Getter for priority
    public int getPriority() {
        return priority;
    }

    // Setter for priority
    public void setPriority(int value) {
        priority = value;
    }

    // New method to get the color based on priority
    public int getColor() {
        switch (priority) {
            case 1: return Color.GREEN;   // Low priority
            case 2: return Color.YELLOW;  // Medium priority
            case 3: return Color.RED;     // High priority
            default: return Color.TRANSPARENT; // Default color
        }
    }

    /**
     * Method to prepare ContentValues for adding a new ToDo item.
     * @return ContentValues object containing the details of the ToDo item.
     */
    public ContentValues getContentValuesToAdd() {
        ContentValues values = new ContentValues();
        values.put("name", getName());
        values.put("deadline", getDeadline());
        values.put("priority", getPriority());
        return values;
    }

    /**
     * Method to prepare ContentValues for updating an existing ToDo item.
     * @return ContentValues object containing the updated details of the ToDo item.
     */
    public ContentValues getContentValuesToUpdate() {
        ContentValues values = new ContentValues();
        values.put("id", getId());
        values.put("name", getName());
        values.put("deadline", getDeadline());
        values.put("priority", getPriority());
        return values;
    }

    /**
     * Parses the data from a Cursor to populate the ToDo object.
     * @param cursor Cursor pointing to the database row containing ToDo item details.
     */
    public void parse(Cursor cursor) {
        setId(cursor.getInt(cursor.getColumnIndex("id")));
        setName(cursor.getString(cursor.getColumnIndex("name")));
        setDeadline(cursor.getString(cursor.getColumnIndex("deadline")));
        setPriority(cursor.getInt(cursor.getColumnIndex("priority")));
    }

    /**
     * Compares two ToDo items by priority for sorting.
     * @param task1 The first ToDo item.
     * @param task2 The second ToDo item.
     * @return A negative integer, zero, or a positive integer as the first argument is less than,
     *         equal to, or greater than the second.
     */
    public static int compareByPriority(ToDo task1, ToDo task2) {
        return Integer.compare(task2.getPriority(), task1.getPriority()); // Sort in descending order
    }

    /**
     * Compares two ToDo items by deadline for sorting.
     * @param task1 The first ToDo item.
     * @param task2 The second ToDo item.
     * @return A negative integer, zero, or a positive integer as the first argument is less than,
     *         equal to, or greater than the second.
     */
    public static int compareByDeadline(ToDo task1, ToDo task2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date1 = sdf.parse(task1.getDeadline());
            Date date2 = sdf.parse(task2.getDeadline());
            return date1.compareTo(date2); // Sort in ascending order
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Return 0 in case of a parsing error
        }
    }
}
