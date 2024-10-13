package com.three19.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.util.Log;

import com.three19.todolist.database.ToDoListDB;
import com.three19.todolist.model.ToDo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Programmer: Vivian Nguyen
 * Contact: viviannguyen726@gmail.com
 * Date: October 2024
 * Version: 1.3
 *
 * Purpose: This class displays all ToDo items and allows sorting them by different criteria.
 * It retrieves the ToDo items from the database and presents them in a ListView.
 * The user can also navigate back to the main activity.
 *
 * Enhancements:
 * - Added functionality to sort tasks by priority and deadline.
 * - Implemented a backup of the original ToDo list for restoring default sorting.
 *
 * Issues:
 * - Ensure proper error handling in sorting methods, especially when comparing deadlines.
 * - Future enhancements should consider providing a filtering option for ToDo items.
 *
 * Efficiency Considerations:
 * - The sorting mechanisms use Collections.sort, which has O(n log n) time complexity
 *   in typical sorting scenarios.
 * - Consider caching the results of frequent operations to improve performance.
 */
public class AllTasksActivity extends AppCompatActivity {

    // Database instance for managing ToDo items
    private ToDoListDB toDoListDB;
    // Adapter for displaying ToDo items in the ListView
    private ToDoListAdapter adapter;
    // List to hold ToDo items retrieved from the database
    private List<ToDo> arrayList;
    // Backup of the original ToDo list to restore when "default" is selected
    private List<ToDo> originalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for the activity
        setContentView(R.layout.activity_all_tasks);

        // Set the title of the activity
        setTitle("All Tasks");

        // Initialize the database instance to interact with ToDo items
        toDoListDB = new ToDoListDB(this);

        // Retrieve the list of ToDo items from the database
        arrayList = toDoListDB.getList();
        Log.d("DEBUG", "Number of ToDo items: " + arrayList.size());

        // Backup the original list for restoring when "default" is selected
        originalList = new ArrayList<>(arrayList);

        // Initialize the adapter with the list of ToDo items
        adapter = new ToDoListAdapter(this, (ArrayList<ToDo>) arrayList);

        // Find the ListView in the layout and set the adapter to it
        ListView listView = findViewById(R.id.lstView);
        listView.setAdapter(adapter);

        // Find the Back button in the layout
        Button backBtn = findViewById(R.id.btnBack);

        // Set an OnClickListener to handle the Back button click event
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create an intent to navigate back to the MainActivity
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent); // Start the MainActivity
            }
        });

        // Initialize the spinner for sorting options
        Spinner spinnerSort = findViewById(R.id.spinnerSort);
        // Create an ArrayAdapter for the spinner using sorting options from resources
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set the adapter to the spinner
        spinnerSort.setAdapter(spinnerAdapter);

        // Set the default selection to the first item ("default")
        spinnerSort.setSelection(0);

        // Handle selection of sorting options from the spinner
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Default - restore original order
                        arrayList.clear();
                        arrayList.addAll(originalList);
                        break;
                    case 1: // Sort by priority
                        Collections.sort(arrayList, new Comparator<ToDo>() {
                            @Override
                            public int compare(ToDo o1, ToDo o2) {
                                // Compare ToDo items by priority
                                return Integer.compare(o1.getPriority(), o2.getPriority());
                            }
                        });
                        break;
                    case 2: // Sort by deadline
                        Collections.sort(arrayList, new Comparator<ToDo>() {
                            @Override
                            public int compare(ToDo o1, ToDo o2) {
                                // Compare ToDo items by deadline
                                return o1.getDeadline().compareTo(o2.getDeadline());
                            }
                        });
                        break;
                }
                // Refresh the ListView to display the sorted list
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed when nothing is selected
            }
        });
    }
}
