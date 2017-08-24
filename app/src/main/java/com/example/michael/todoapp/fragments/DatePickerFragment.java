package com.example.michael.todoapp.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Required for creating the DatePicker.
 */
public class DatePickerFragment extends DialogFragment {

    /**
     * This method contains logic for loading the DatePicker.
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), listener, year, month, day);
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return dpd;
    }
}