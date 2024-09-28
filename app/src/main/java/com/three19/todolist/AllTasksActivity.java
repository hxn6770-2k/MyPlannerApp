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

import com.three19.todolist.database.ToDoListDB;
import com.three19.todolist.model.ToDo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllTasksActivity extends AppCompatActivity {

    // Database instance for managing ToDo items
    private ToDoListDB toDoListDB;
    // Adapter for displaying ToDo items in the ListView
    private ToDoListAdapter adapter;
    // List to hold ToDo items retrieved from the database
    private List<ToDo> arrayList;

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

        // Initialize the adapter with the list of ToDo items
        adapter = new ToDoListAdapter(this, (ArrayList<ToDo>) arrayList);

        // Find the ListView in the layout and set the adapter to it
        ListView listView = (ListView) findViewById(R.id.lstView);
        listView.setAdapter(adapter);

        // Find the Back button in the layout
        Button backBtn = (Button) findViewById(R.id.btnBack);

        // Set an OnClickListener to handle the Back button click event
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create an intent to navigate back to the MainActivity
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent); // Start the MainActivity
                finish(); // Finish the current activity
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

        // Handle selection of sorting options from the spinner
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Sort by priority
                        Collections.sort(arrayList, new Comparator<ToDo>() {
                            @Override
                            public int compare(ToDo o1, ToDo o2) {
                                // Compare ToDo items by priority
                                return Integer.compare(o1.getPriority(), o2.getPriority());
                            }
                        });
                        break;
                    case 1: // Sort by deadline
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
