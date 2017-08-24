package com.example.michael.todoapp.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.michael.todoapp.interfaces.DeleteTaskDialogListener;


/**
 * This DialogFragment is used for prompting a user to confirm deletion of a task. It is created
 * when the following click listener is activated.
 * setOnItemLongClickListener
 */
public class DeleteTaskDialogFragment extends DialogFragment {

    /**
     * Empty constructor required for DialogFragment
     */
    public DeleteTaskDialogFragment() {

    }

    /**
     * This method creates a new DialogFragment and passes relevant arguments to a Bundle.
     *
     * @param title:        The title of a task
     * @param taskPosition: The position of the task in the ArrayList in MainActivity. This is
     *                      required so the correct task can be deleted.
     * @return
     */
    public static DeleteTaskDialogFragment newInstance(String title, int taskPosition) {
        DeleteTaskDialogFragment frag = new DeleteTaskDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("position", taskPosition);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Prepares the DialogFragment message, buttons, and click listeners.
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        final int position = getArguments().getInt("position");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Press OK to delete the task.");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteTaskDialogListener listener = (DeleteTaskDialogListener) getActivity();
                listener.onFinishDeleteTaskDialog(true, position);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    DeleteTaskDialogListener listener = (DeleteTaskDialogListener) getActivity();
                    listener.onFinishDeleteTaskDialog(false, position);
                    dialog.dismiss();
                }
            }
        });
        return alertDialogBuilder.create();
    }
}