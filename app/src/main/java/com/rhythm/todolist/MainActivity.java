package com.rhythm.todolist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView todoListView;
    private ArrayList<String> listItems;
    private Button welcomeScreenBtn;
    private ArrayAdapter arrayAdapter;
    private RelativeLayout displayLayout, taskDoneLayout, addTaskLayout;
    private boolean isOptionMenuCreated = false;
    private int previousCount = 0;
    private ArrayList<Integer> selectedPositionsArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing all the IDs used in xml file
        Button addTaskBtn = findViewById(R.id.add_task_btn); // add task button
        welcomeScreenBtn = findViewById(R.id.welcome_screen_btn); // add new task button
        displayLayout = findViewById(R.id.welcome_layout); // layout to appear at the starting of the app.
        taskDoneLayout = findViewById(R.id.task_done_layout); // layout to appear, when list gets empty
        addTaskLayout = findViewById(R.id.add_task_relative_layout); // layout of edittext and button
        todoListView = findViewById(R.id.todo_lv); // listView
        addTaskBtn.setOnClickListener(this);
        welcomeScreenBtn.setOnClickListener(this);

        // setting adapter for the list
        setListData();
    }

    // setting adapter for the listView
    public void setListData() {
        listItems = new ArrayList<>(); // initializing list
       //The Adapter acts as a bridge between the UserInterface and the Data Source.
       //It converts data from the data sources into view items that can be displayed into the UI Component.
       //Data Source can be Arrays, HashMap, Database,ArrayList etc.
       //and UI Components can be ListView, GridView, Spinner,RecyclerView etc.
        arrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.list_item_layout, listItems);
        todoListView.setAdapter(arrayAdapter); // setting adapter

        todoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CheckedTextView tv = view.findViewById(R.id.checkedTv);
                if (tv.isChecked()){
                    selectedPositionsArray.add(position);
                }else if(!tv.isChecked()){
                    selectedPositionsArray.remove(position);
                }
            }
        });
    }

    //The intent of implementing this method is to populate the menu passed with the items you
    // define in the R.menu.menu layout file in resource package.
    //onCreateOptionsMenu() is called by the Android runtime when it need to create the option menu,
    // when creating activity, in order to show items to the app bar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // here if task list is not empty than we will display the delete icon on the app bar.
        if (listItems.size() != 0) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu, menu);

            // getting menu item, in our case delete button
            MenuItem getItem = menu.findItem(R.id.delete_task);
            if (getItem != null) {

                // setChoiceMode is a method of ListView which sets a value
                // indicating whether multiple items can be selected or not.
                // ListView.CHOICE_MODE_SINGLE(1) for single item selection.
                // ListView.CHOICE_MODE_MULTIPLE(2) for multiple item selection.
                // by default it is set to ListView.CHOICE_MODE_NONE, which means 0
                // when using SparseBooleanArray we need to set choice mode of the ListView, else
                // it will show null error on SparseBooleanArray object
                todoListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                // getting checked item in the list and storing it in SparseBooleanArray
                // as getCheckedItemPositions() return type is SparseBooleanArray
                //SparseBooleanArrays map integers(in our case item positions) to booleans.
                SparseBooleanArray selectedPositions = todoListView.getCheckedItemPositions();

                // menuItemClickListener when user click on delete action it invokes this method
                getItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int count = todoListView.getCount(); // gets total number of items added in the listview
                        // here we are iterating all the items of the list
                        // and checking if selectedPositions array contains
                        // the items or not, if true it will delete the item from the list.
                        if (selectedPositions.size() != 0) {
                            for (int i = count - 1; i >= 0; i--) {
                                if (selectedPositions.get(i)) {
                                    listItems.remove(i);
                                }
                            }
                            Toast.makeText(MainActivity.this, "Task(s) deleted", Toast.LENGTH_SHORT).show();

                            //Removes all values from selectedPositions
                            selectedPositions.clear();
                        }

                        if (listItems.size() != 0) {
                            // notifying adapter when some change occurs in the listView.
                            arrayAdapter.notifyDataSetChanged();
                        }else{
                            // here if list gets empty, we will setVisibility of layouts
                            displayLayout.setVisibility(View.INVISIBLE);
                            taskDoneLayout.setVisibility(View.VISIBLE);
                            todoListView.setVisibility(View.INVISIBLE);
                            welcomeScreenBtn.setVisibility(View.VISIBLE);
                            addTaskLayout.setVisibility(View.INVISIBLE);
                            isOptionMenuCreated = false;
                            // this will invalidate OptionsMenu and recall onCreateOptionMenu again
                            //here if list gets empty, we will not display the delete icon on the app bar.
                            invalidateOptionsMenu();
                        }
                        return true;
                    }
                });
            }
        }
        return true;
    }

    //onClick method of add buttons
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.welcome_screen_btn:
                // setting visibility of the task buttons
                welcomeScreenBtn.setVisibility(View.INVISIBLE);
                addTaskLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.add_task_btn:
                // getting new task, entered by the user and adding it to the list
                EditText addTaskEdt = findViewById(R.id.add_task_edt);
                String taskString = addTaskEdt.getText().toString();

                // checking if string is empty or not, empty string will not be added in the list
                if (!taskString.isEmpty()) {
                    displayLayout.setVisibility(View.INVISIBLE);
                    taskDoneLayout.setVisibility(View.INVISIBLE);
                    todoListView.setVisibility(View.VISIBLE);

                    // adding task into the list
                    listItems.add(taskString);
                    // setting edit text to empty when a task is added
                    addTaskEdt.setText("");
                    Toast.makeText(MainActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();

                    if (!isOptionMenuCreated){
                        // here we want invalidateOptionsMenu to run only once, when 1st item
                        // is inserted in the listview.
                        isOptionMenuCreated = true;
                        // this will invalidate OptionsMenu and recall onCreateOptionMenu again
                        //here when list item is added, we will display the delete icon on the menu.
                        invalidateOptionsMenu();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Enter a task", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}