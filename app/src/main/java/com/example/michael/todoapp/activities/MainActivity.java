package com.example.michael.todoapp.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
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

import com.example.michael.todoapp.R;
import com.example.michael.todoapp.fragments.DeleteTaskDialogFragment;
import com.example.michael.todoapp.interfaces.DeleteTaskDialogListener;
import com.example.michael.todoapp.models.Task;
import com.example.michael.todoapp.utils.TodoDatabaseHelper;
import com.example.michael.todoapp.adapters.TodoItemsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class controls the logic behind the main activity for the Remind Me application.
 */
public class MainActivity extends AppCompatActivity implements DeleteTaskDialogListener {

    private TodoDatabaseHelper databaseHelper;
    private ArrayList<Task> toDoItems;
    private TodoItemsAdapter adapter;
    private ListView lvItems;
    private Menu mMenu;
    private MenuItem miRestoreDefault;
    private MenuItem miShowCompleted;
    private MenuItem miSortLowToHigh;
    private MenuItem miSortHighToLow;
    private MenuItem miAddItem;

    private boolean showingCompleted;
    private final int REQUEST_CODE = 1;
    private final int RESULT_OK = 1;

    /**
     * A required method that is called when the main activity is opened by a user.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = TodoDatabaseHelper.getInstance(this);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        populateArrayItems();
        lvItems.setAdapter(adapter);
        showingCompleted = false;

        //Create a listener for removing task items.
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View item, int position, long id) {
                confirmTaskDeletion(toDoItems.get(position).getTitle(), position);
                return true;
            }
        });
        //Create a listener for editing task items.
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.putExtra("task", toDoItems.get(position)); //Optional parameters
                intent.putExtra("position", position); //Optional parameters
                intent.putExtra("showingCompleted", showingCompleted);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    /**
     * This method adds items to the action bar.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        //This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        miAddItem = menu.findItem(R.id.icAdd);
        miRestoreDefault = menu.findItem(R.id.restoreDefault);
        miShowCompleted = menu.findItem(R.id.showCompleted);
        miSortLowToHigh = menu.findItem(R.id.sortLowToHigh);
        miSortHighToLow = menu.findItem(R.id.sortHighToLow);
        miRestoreDefault.setVisible(false);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(miAddItem);
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
                intent.putExtra("showCompleted", showingCompleted);
                startActivityForResult(intent, REQUEST_CODE);

                if (mMenu != null) {
                    (mMenu.findItem(R.id.icAdd)).collapseActionView();
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
     * This method handles click events for action bar items.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sortLowToHigh) {
            sortLowToHigh();
        } else if (id == R.id.sortHighToLow) {
            sortHighToLow();
        } else if (id == R.id.showCompleted) {
            showingCompleted = true;
            miRestoreDefault.setVisible(true);
            miShowCompleted.setVisible(false);
            miSortLowToHigh.setVisible(false);
            miSortHighToLow.setVisible(false);
            miAddItem.setVisible(false);
            populateCompletedArrayItems();
        } else if (id == R.id.restoreDefault) {
            showingCompleted = false;
            miRestoreDefault.setVisible(false);
            miShowCompleted.setVisible(true);
            miSortLowToHigh.setVisible(true);
            miSortHighToLow.setVisible(true);
            miAddItem.setVisible(true);
            restoreArrayItems();
        }
        return true;
    }

    /**
     * This method sorts the ArrayList of tasks from the lowest priority score to the highest
     * priority score.
     */
    public void sortLowToHigh() {
        Collections.sort(toDoItems, new Comparator<Task>() {
            public int compare(Task t1, Task t2) {
                if (t1.getPriority() < t2.getPriority()) {
                    return -1;
                } else if (t1.getPriority() == t2.getPriority()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    /**
     * This method sorts the ArrayList of tasks from the highest priority score to the lowest
     * priority score.
     */
    public void sortHighToLow() {
        Collections.sort(toDoItems, new Comparator<Task>() {
            public int compare(Task t1, Task t2) {
                if (t1.getPriority() < t2.getPriority()) {
                    return 1;
                } else if (t1.getPriority() == t2.getPriority()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    /**
     * This method reads tasks from SQLite and sets the TodoItemsAdapter.
     */
    public void populateArrayItems() {
        readItems();
        adapter = new TodoItemsAdapter(this, toDoItems);
    }

    /**
     * This method reads completed tasks from SQLite.
     */
    public void populateCompletedArrayItems() {
        readCompletedItems();
        adapter.notifyDataSetChanged();
    }

    /**
     * This method restores tasks from SQLite. This method is called when switching from viewing
     * completed tasks to in progress tasks.
     */
    public void restoreArrayItems() {
        restoreItems();
        adapter.notifyDataSetChanged();
    }

    /**
     * This method reads all tasks that need to be completed from SQLite and updates toDoItems with
     * a list of tasks.
     */
    public void readItems() {
        toDoItems = databaseHelper.getAllTasks();
    }

    /**
     * This method reads all completed tasks from SQLite and updates toDoItems with
     * a list of the tasks.
     */
    public void readCompletedItems() {
        ArrayList<Task> newTaskList = databaseHelper.getAllCompletedTasks();
        toDoItems.clear();
        for (Task t : newTaskList) {
            toDoItems.add(t);
        }
    }

    /**
     * This method reads in progress tasks from SQLite and updates toDoItems with
     * a list of the tasks.
     */
    public void restoreItems() {
        ArrayList<Task> newTaskList = databaseHelper.getAllTasks();
        toDoItems.clear();
        for (Task t : newTaskList) {
            toDoItems.add(t);
        }
    }

    /**
     * This method updates the information for a particular task in the SQLite database.
     *
     * @param t
     */
    public void updateItem(Task t) {
        databaseHelper.updateTask(t);
    }

    /**
     * This method deletes a task from the database.
     *
     * @param t
     */
    public void deleteTask(Task t) {
        databaseHelper.deleteTask(t);
    }

    /**
     * This method marks a task as completed in the database.
     *
     * @param t
     */
    public void markTaskCompleted(Task t) {
        databaseHelper.markTaskCompleted(t);
    }

    /**
     * This method marks a task as not completed in the database.
     *
     * @param t
     */
    public void markTaskNotCompleted(Task t) {
        databaseHelper.markTaskNotCompleted(t);
    }

    /**
     * This method adds a task to the database.
     *
     * @param t
     * @return
     */
    public long addTask(Task t) {
        return databaseHelper.addTask(t);
    }


    /**
     * This method handles logic for updating task information after the edit item activity is completed.
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {


        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            int position = intent.getIntExtra("position", -1);
            Task t = (Task) intent.getSerializableExtra("task");
            showingCompleted = (boolean) intent.getBooleanExtra("showingCompleted", false);
            //check if task is marked as completed and whether showing completed tasks
            if (t.isStatus() == 1 && !showingCompleted) {
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
            } else if (t.isStatus() == 0 && showingCompleted) {
                markTaskNotCompleted(t);
                updateItem(t);
                toDoItems.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Restored Task Successfully", Toast.LENGTH_SHORT).show();
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


    /**
     * This is a required method from the custom interface, DeleteTaskDialogListener. It is called
     * each time the "ok" or "cancel" button is selected when a user is asked to confirm deletion
     * of a task.
     *
     * @param isDeleteSelected
     */
    @Override
    public void onFinishDeleteTaskDialog(boolean isDeleteSelected, int position) {
        if (isDeleteSelected) {
            Task t = toDoItems.get(position);
            String title = t.getTitle();
            toDoItems.remove(position);
            adapter.notifyDataSetChanged();
            deleteTask(t);
            Toast.makeText(this, "Deleted Task: " + isDeleteSelected, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Deletion Canceled", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This method creates the dialog fragment which asks a user to confirm deletion of a task.
     *
     * @param taskName
     */
    private void confirmTaskDeletion(String taskName, int taskPosition) {
        FragmentManager fm = getSupportFragmentManager();
        String taskTitle = "Delete " + taskName;
        DeleteTaskDialogFragment deleteTaskDialog = DeleteTaskDialogFragment.newInstance(taskTitle, taskPosition);
        deleteTaskDialog.show(fm, "fragment_alert");
    }

}
