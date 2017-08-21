package com.example.michael.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * This class contains methods for updating the SQLite database.
 */
public class TaskDatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "taskDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_TASKS = "tasks";

    // Post Table Columns
    private static final String KEY_TASK_ID = "id";
    private static final String KEY_TASK_TITLE = "title";
    private static final String KEY_TASK_DESCRIPTION = "description";
    private static final String KEY_TASK_DATE = "date";
    private static final String KEY_TASK_PRIORITY = "priority";
    private static final String KEY_TASK_STATUS = "status";


    private static TaskDatabaseHelper sInstance;

    /**
     * Use the application context, which will ensure that you don't accidentally
     * leak an Activity's context.
     * See this article for more information: http://bit.ly/6LRzfx
     * @param context
     * @return
     */
    public static synchronized TaskDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TaskDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database connection is being configured.
     * Configure database settings for things like foreign key support, write-ahead logging, etc.
     * @param db
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Called when the database is created for the FIRST time.
     * If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS +
                "(" +
                KEY_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                KEY_TASK_TITLE + " TEXT," +
                KEY_TASK_DESCRIPTION + " TEXT," +
                KEY_TASK_DATE + " TEXT," +
                KEY_TASK_PRIORITY + " INTEGER, " +
                KEY_TASK_STATUS + " INTEGER" +
                ")";

        db.execSQL(CREATE_TASKS_TABLE);
    }

    /**
     * Called when the database needs to be upgraded.
     * This method will only be called if a database already exists on disk with the same DATABASE_NAME,
     * but the DATABASE_VERSION is different than the version of the database that exists on disk.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
            onCreate(db);
        }
    }


    /**
     * This method adds a task to the database.
     * @param task
     * @return
     */
    public long addTask(Task task) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(KEY_TASK_TITLE, task.getTitle());
        values.put(KEY_TASK_DESCRIPTION, task.getDescription());
        values.put(KEY_TASK_DATE, task.getDate());
        values.put(KEY_TASK_PRIORITY, task.getPriority());
        values.put(KEY_TASK_STATUS, task.isStatus());

        long id = -1;
        try {
            id = db.insert(TABLE_TASKS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error adding task to the database");
            Log.d(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
        return id;
    }


    /**
     * This method deletes a task from the database.
     * @param task
     */
    public void deleteTask(Task task) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();

        try {
            String DELETE_TASK_RECORD = "DELETE FROM " + TABLE_TASKS + " WHERE " +
                    KEY_TASK_ID + " = " + task.getId();
            db.execSQL(DELETE_TASK_RECORD);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error deleting from the database");
            Log.d(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }


    /**
     * This method updates a task in the database.
     * @param task
     */
    public void updateTask(Task task) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_TASK_TITLE, task.getTitle());
            values.put(KEY_TASK_DESCRIPTION, task.getDescription());
            values.put(KEY_TASK_DATE, task.getDate());
            values.put(KEY_TASK_PRIORITY, task.getPriority());
            values.put(KEY_TASK_STATUS, task.isStatus());
            db.update(TABLE_TASKS, values, KEY_TASK_ID + "=" + task.getId(), null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error updating a record");
            Log.d(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }


    /**
     * This method marks a task as completed in the database.
     * @param task
     */
    public void markTaskCompleted(Task task) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            String UPDATE_TASK_RECORD = "UPDATE " + TABLE_TASKS +
                    " SET " +
                    KEY_TASK_STATUS + " = 1" +
                    " WHERE " +
                    KEY_TASK_ID + "=" + task.getId();
            db.execSQL(UPDATE_TASK_RECORD);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error updating the status as completed");
            Log.d(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }


    /**
     * This method gets all tasks from the database that are not yet completed.
     * @return
     */
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();
        SQLiteDatabase db = getReadableDatabase();

        String TASKS_SELECT_QUERY = "SELECT * FROM " + TABLE_TASKS + " WHERE " + KEY_TASK_STATUS + " != 1";


        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(KEY_TASK_ID));
                    String title = cursor.getString(cursor.getColumnIndex(KEY_TASK_TITLE));
                    String description = cursor.getString(cursor.getColumnIndex(KEY_TASK_DESCRIPTION));
                    String date = cursor.getString(cursor.getColumnIndex(KEY_TASK_DATE));
                    int priority = cursor.getInt(cursor.getColumnIndex(KEY_TASK_PRIORITY));
                    int status = cursor.getInt(cursor.getColumnIndex(KEY_TASK_STATUS));

                    Task task = new Task(id, title, description, priority, date, status);
                    tasks.add(task);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error getting list of all tasks from the database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return tasks;
    }
}