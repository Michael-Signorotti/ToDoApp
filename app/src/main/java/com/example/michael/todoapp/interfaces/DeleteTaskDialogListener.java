package com.example.michael.todoapp.interfaces;

/**
 * This listener interface is used for passing data from the DeleteTaskDialogFragment back to the main
 * activity.
 */
public interface DeleteTaskDialogListener {
    void onFinishDeleteTaskDialog(boolean isDeleteSelected, int position);
}
