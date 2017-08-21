package com.example.michael.todoapp;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * This class controls the logic behind the main activity for the Remind Me application.
 */
public class MainActivity extends AppCompatActivity {

    private TaskDatabaseHelper databaseHelper;
    private ArrayList<Task> toDoItems;
    private CustomListAdapter adapter;
    private ListView lvItems;
    private EditText etEditText;
    private Menu mMenu;

    private final int REQUEST_CODE = 1;
    private final int RESULT_OK = 1;

    /**
     * A required method that is called when the main activity is opened by a user.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = TaskDatabaseHelper.getInstance(this);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        populateArrayItems();
        lvItems.setAdapter(adapter);

        //Create a listener for removing task items.
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View item, int position, long id) {
                Task t = toDoItems.get(position);
                toDoItems.remove(position);
                adapter.notifyDataSetChanged();
                deleteTask(t);
                return true;
            }
        });
        //Create a listener for editing task items.
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.putExtra("task", toDoItems.get(position)); //Optional parameters
                intent.putExtra("position", position); //Optional parameters
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    /**
     * This method adds items to the action bar.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        //This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.miAdd);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQuery("Enter Title...", false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * A required method that controls logic for starting to create a task using SearchView.
             * The method is called when users press the confirm button on the keyboard.
             * @param query
             * @return
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                // avoids devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
                EditText et = (EditText) searchView.findViewById(searchEditId);
                String newTitle = et.getText().toString();
                et.setText("");
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.putExtra("position", -1);
                intent.putExtra("title", newTitle);
                startActivityForResult(intent, REQUEST_CODE);

                if (mMenu != null) {
                    (mMenu.findItem(R.id.miAdd)).collapseActionView();
                }

                return true;
            }

            /**
             * A required method to override.
             * @param newText
             * @return
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //this resets the action bar
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et.setHint("Enter Title...");

        return true;
    }

    /**
     * This method reads Tasks from SQLite and sets the CustomListAdapter.
     */
    public void populateArrayItems() {
        readItems();
        adapter = new CustomListAdapter(this, toDoItems);
    }

    /**
     * This method reads all tasks that need to be completed from SQLite and updates toDoItems with
     * a list of tasks.
     */
    public void readItems() {
        TaskDatabaseHelper databaseHelper = TaskDatabaseHelper.getInstance(this);
        toDoItems = databaseHelper.getAllTasks();
    }

    /**
     * This method updates the information for a particular task in the SQLite database.
     * @param t
     */
    public void updateItem(Task t) {
        TaskDatabaseHelper databaseHelper = TaskDatabaseHelper.getInstance(this);
        databaseHelper.updateTask(t);
    }

    /**
     * This method deletes a task from the database.
     * @param t
     */
    public void deleteTask(Task t) {
        TaskDatabaseHelper databaseHelper = TaskDatabaseHelper.getInstance(this);
        databaseHelper.deleteTask(t);
    }

    /**
     * This method marks a task as completed in the database.
     * @param t
     */
    public void markTaskCompleted(Task t) {
        TaskDatabaseHelper databaseHelper = TaskDatabaseHelper.getInstance(this);
        databaseHelper.markTaskCompleted(t);
    }

    /**
     * This method adds a task to the database.
     * @param t
     * @return
     */
    public long addTask(Task t) {
        TaskDatabaseHelper databaseHelper = TaskDatabaseHelper.getInstance(this);
        return databaseHelper.addTask(t);
    }


    /**
     * This method handles logic for updating task information after the edit item activity is completed.
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {


        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            int position = intent.getIntExtra("position", -1);
            Task t = (Task) intent.getSerializableExtra("task");

            //check if task is marked as completed
            if (t.isStatus() == 1) {
                //check if item needs to be removed from toDoItems
                if (position != -1) {
                    toDoItems.remove(position);
                    adapter.notifyDataSetChanged();
                    markTaskCompleted(t);
                } else {
                    long id = addTask(t);
                    t.setId(id);
                    markTaskCompleted(t);
                }
                Toast.makeText(this, "Task Completed Successfully", Toast.LENGTH_SHORT).show();
            } else {
                if (position == -1) {
                    long id = addTask(t);
                    t.setId(id);
                    toDoItems.add(t);
                    Toast.makeText(this, "Added Task Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    toDoItems.set(position, t);
                    updateItem(t);
                    Toast.makeText(this, "Saved Changes Successfully", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(this, "No Changes Were Made", Toast.LENGTH_SHORT).show();
        }
    }
}
