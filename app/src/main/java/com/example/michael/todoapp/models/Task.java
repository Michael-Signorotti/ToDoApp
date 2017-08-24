package com.example.michael.todoapp.models;

import java.io.Serializable;


public class Task implements Serializable {
    //the id assigned by SQLite
    private long id;
    //the title of the task
    private String title;
    //description of the task
    private String description;
    //priority of the task
    private int priority;
    //the task due date
    private String date;
    //the task status(true=completed)
    private int status;

    /**
     * The task constructor.
     *
     * @param id
     * @param title
     * @param description
     * @param priority
     * @param date
     * @param status
     */
    public Task(long id, String title, String description, int priority, String date, int status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.date = date;
        this.status = status;
    }

    /**
     * Returns the task id.
     *
     * @return
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the task title.
     *
     * @return
     */
    public String getTitle() {

        return title;
    }

    /**
     * Returns the task description.
     *
     * @return
     */
    public String getDescription() {

        return description;
    }

    /**
     * Returns the task priority.
     *
     * @return
     */
    public int getPriority() {

        return priority;
    }

    /**
     * Returns the task date.
     *
     * @return
     */
    public String getDate() {

        return date;
    }

    /**
     * Returns the task status.
     *
     * @return
     */
    public int isStatus() {

        return status;
    }

    /**
     * Sets the task id.
     *
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }
}
