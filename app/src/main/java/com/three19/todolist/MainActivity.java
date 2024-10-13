package com.three19.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RadioGroup;

import com.three19.todolist.database.ToDoListDB;
import com.three19.todolist.model.ToDo;

import java.util.ArrayList;
import java.util.List;

/**
 * Programmer: Vivian Nguyen
 * Contact: viviannguyen726@gmail.com
 * Date: October 2024
 * Version: 1.3
 *
 * MainActivity class that handles the user interface and interactions
 * for the To-Do list application. It allows users to add, update,
 * and remove tasks from the list.
 *
 * Enhancements:
 * - Provides a user-friendly interface for managing ToDo items.
 * - Implements functionality to add, update, and delete ToDo items efficiently.
 * - Allows users to select task priority and deadline during item creation and updates.
 * - Utilizes a custom adapter to manage the display of ToDo items in a ListView.
 * - Displays confirmation dialogs before deleting items to prevent accidental removals.
 *
 * Issues:
 * - Ensure proper error handling for user input to avoid potential crashes.
 * - Future enhancements may include implementing filtering options for the task list.
 *
 * Efficiency Considerations:
 * - The application uses an ArrayList to store ToDo items, providing O(1) access time.
 * - The ListView is updated efficiently using the adapter's notifyDataSetChanged method
 *   to refresh the UI after data modifications.
 */
public class MainActivity extends AppCompatActivity {

    // Instance variables
    ToDoListDB toDoListDB;  // Database helper
    List<ToDo> arrayList;   // List to hold ToDo items
    ToDoListAdapter adapter; // Adapter for the ListView
    ToDo selectedToDo;      // Currently selected ToDo item for editing
    int selectedPosition;    // Position of the selected item in the list
    EditText txtName;       // EditText for entering task name
    EditText txtDeadline;
    RadioGroup radioGroupPriority;
    Button addBtn;          // Button to add or update tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        txtName = findViewById(R.id.txtName);
        txtDeadline = findViewById(R.id.txtDeadline);
        radioGroupPriority = findViewById(R.id.radioGroupPriority);
        addBtn = findViewById(R.id.btnAdd);

        // Initialize the database and retrieve existing tasks
        toDoListDB = new ToDoListDB(this);
        arrayList = toDoListDB.getList();

        // Set up the adapter for the ListView
        adapter = new ToDoListAdapter(this, (ArrayList<ToDo>) arrayList);
        ListView listView = findViewById(R.id.lstView);
        listView.setAdapter(adapter);

        // Set up listeners for long-click and click events on the ListView
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                removeItemFromList(position); // Call method to remove item
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedToDo = arrayList.get(position); // Get the selected ToDo item
                selectedPosition = position;            // Store its position
                txtName.setText(selectedToDo.getName()); // Display the name in the EditText

                // Change button from Add to Update
                addBtn.setText("Update");
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = txtName.getText().toString();
                        String deadline = txtDeadline.getText().toString(); // Get the deadline input

                        // Get the selected priority from the RadioGroup
                        int selectedId = radioGroupPriority.getCheckedRadioButtonId();
                        int priority = 0; // Default priority
                        if (selectedId == R.id.radioLow) {
                            priority = 1; // Low
                        } else if (selectedId == R.id.radioMedium) {
                            priority = 2; // Medium
                        } else if (selectedId == R.id.radioHigh) {
                            priority = 3; // High
                        }

                        updateItem(name, deadline, priority); // Pass all parameters
                    }
                });
            }
        });

        // Set up listener for the Add button
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = txtName.getText().toString();
                String deadline = txtDeadline.getText().toString(); // Get the deadline input

                // Get the selected priority from the RadioGroup
                int selectedId = radioGroupPriority.getCheckedRadioButtonId();
                int priority = 0; // Default priority
                if (selectedId == R.id.radioLow) {
                    priority = 1; // Low
                } else if (selectedId == R.id.radioMedium) {
                    priority = 2; // Medium
                } else if (selectedId == R.id.radioHigh) {
                    priority = 3; // High
                }

                if (addBtn.getText().toString().equalsIgnoreCase("Add")) {
                    // Add new ToDo item to the database and list
                    ToDo toDo = new ToDo();
                    toDo.setName(name);
                    toDo.setDeadline(deadline);
                    toDo.setPriority(priority);


                    // Assuming `toDoListDB` has an appropriate method to add a ToDo object
                    toDoListDB.add(toDo); // Update this line to match your DB method
                    arrayList.add(toDo);
                    adapter.notifyDataSetChanged(); // Refresh the ListView

                    // Clear input fields
                    txtName.setText("");
                    txtDeadline.setText(""); // Clear the deadline input
                    radioGroupPriority.clearCheck(); // Deselect any selected radio buttons
                } else {
                    // Update existing ToDo item
                    updateItem(name, deadline, priority); // Ensure updateItem method is updated to accept deadline and priority
                }
            }
        });


        // Set up listener for the Clear button
        Button clearBtn = findViewById(R.id.btnClear);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reset(); // Call method to reset input fields
            }
        });

        // Set up listener for the All Tasks button
        Button allBtn = findViewById(R.id.btnAll);
        allBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Start AllTasksActivity to view all tasks
                Intent intent = new Intent(getBaseContext(), AllTasksActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Removes the item from the list after confirming with the user.
     *
     * @param position The position of the item to be removed
     */
    protected void removeItemFromList(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Delete");
        alert.setMessage("Do you want to delete this item?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToDo toDo = arrayList.get(position); // Get the ToDo item to delete
                arrayList.remove(position);           // Remove it from the list
                adapter.notifyDataSetChanged();       // Refresh the ListView
                toDoListDB.remove(toDo.getId());     // Remove it from the database
                reset();                              // Reset UI
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Dismiss the dialog
            }
        });
        alert.show(); // Show the alert dialog
    }

    /**
     * Resets the UI components to their default state.
     */
    protected void reset() {
        txtName.setText("");        // Clear the EditText
        addBtn.setText("Add");      // Reset button text to "Add"
        selectedToDo = null;        // Clear selected ToDo
        selectedPosition = -1;       // Reset selected position
    }

    /**
     * Updates the selected ToDo item with the new name.
     *
     * @param name The new name for the ToDo item
     */
    private void updateItem(String name, String deadline, int priority) {
        // Check if a ToDo item is selected for updating
        if (selectedToDo != null) {
            // Update the properties of the selected ToDo object with new values
            selectedToDo.setName(name);           // Set the new name for the ToDo item
            selectedToDo.setDeadline(deadline);   // Set the new deadline for the ToDo item
            selectedToDo.setPriority(priority);    // Set the new priority for the ToDo item

            // Update the list in memory and refresh the ListView to reflect changes
            arrayList.set(selectedPosition, selectedToDo); // Replace the old item in the list
            adapter.notifyDataSetChanged();        // Notify the adapter to refresh the ListView

            // Update the ToDo item in the database
            toDoListDB.update(selectedToDo);       // Persist the updated item in the database

            // Reset UI components to their default state for user input
            txtName.setText("");                   // Clear the name input field
            txtDeadline.setText("");               // Clear the deadline input field

            // Reset the priority selection in the RadioGroup
            radioGroupPriority.clearCheck(); // Clear the selected RadioButton

            addBtn.setText("Add");                 // Change the button text back to "Add" for new entries
            selectedToDo = null;                   // Clear the reference to the selected ToDo item
            selectedPosition = -1;                 // Reset the selected position index
        }
    }

}

/**
 * ToDoListAdapter class is responsible for managing the display of ToDo items
 * in a ListView. This class utilizes an ArrayAdapter to handle the dynamic
 * population of the ListView with the task data stored in an ArrayList of
 * ToDo objects. It efficiently handles the display by implementing the
 * ViewHolder pattern, which optimizes memory usage and enhances performance
 * when rendering ListView items.
 *
 * Programmer: Vivian Nguyen
 * Contact: viviannguyen726@gmail.com
 * Date: October 2024
 * Version: 1.3
 *
 * Purpose: This class inflates the ListView with the ToDo items and displays
 * their names. It also assigns a background color to each ListView item
 * based on the task's priority.
 *
 * Enhancements:
 * - Added ViewHolder pattern to improve performance by avoiding redundant
 *   view inflation and lookup calls, optimizing memory usage, and speeding
 *   up list scrolling.
 * - Implemented dynamic background color assignment based on task priority.
 *
 * Issues:
 * - Ensure that all ToDo objects in the list contain valid data to avoid
 *   potential NullPointerExceptions.
 *
 * Efficiency Considerations:
 * - The ViewHolder pattern is applied to ensure that views are reused where
 *   possible, which reduces the number of calls to findViewById and improves
 *   ListView rendering efficiency.
 * - The ArrayAdapter is used in conjunction with the ViewHolder to manage
 *   the view creation process. This approach provides an O(1) time complexity
 *   for accessing list items once they are loaded into memory, resulting in
 *   efficient list performance.
 */

class ToDoListAdapter extends ArrayAdapter<ToDo> {

    public ToDoListAdapter(Context context, ArrayList<ToDo> toDoList) {
        super(context, 0, toDoList); // Call the super constructor
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder pattern for better performance
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(android.R.id.text1); // Initialize TextView
            convertView.setTag(viewHolder); // Set the ViewHolder as a tag
        } else {
            viewHolder = (ViewHolder) convertView.getTag(); // Retrieve the ViewHolder
        }

        ToDo toDo = getItem(position); // Get the ToDo item for this position

        // Check for null to avoid NullPointerException
        if (toDo != null) {
            viewHolder.name.setText(toDo.getName()); // Set the task name

            // Set the background color based on priority using the new method
            convertView.setBackgroundColor(toDo.getColor());
        }

        return convertView; // Return the populated view
    }

    // ViewHolder class to hold references to the views
    static class ViewHolder {
        TextView name; // Reference to the TextView for task name
    }
}
