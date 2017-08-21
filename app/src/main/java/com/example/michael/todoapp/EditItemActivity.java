package com.example.michael.todoapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class contains the logic behind the edit item activity. The edit item activity is used
 * for both new task submissions and existing tasks which are being edited.
 */
public class EditItemActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private EditText etEditItem;
    private TextView tvTitle;
    private TextView tvDescription;
    private EditText etDescription;
    private SeekBar sbPriority;
    private TextView tvPriority;
    private EditText etDate;
    private TextView tvDate;
    private TextView tvStatus;
    private ToggleButton tbtnStatus;
    private TextView tvPriorityTag;
    private TextView tvPriorityScore;
    private int position;
    private final int RESULT_OK = 1;
    private final int RESULT_CANCELED = -1;
    private long id = -1;

    /**
     * A required method that is called when the edit item activity is opened by a user.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        getSupportActionBar().setTitle("Edit Item");

        etEditItem = (EditText) findViewById(R.id.etEditItem);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        etDescription = (EditText) findViewById(R.id.etDescription);
        sbPriority = (SeekBar) findViewById(R.id.sbPriority);
        tvPriority = (TextView) findViewById(R.id.tvPriority);
        etDate = (EditText) findViewById(R.id.etDate);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tbtnStatus = (ToggleButton) findViewById(R.id.tbtnStatus);
        tvPriorityTag = (TextView) findViewById(R.id.tvPriorityTag);
        tvPriorityScore = (TextView) findViewById(R.id.tvPriorityScore);

        //get information passed from the main activity
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);

        //check if editing an existing task or creating a new one
        if (position == -1) {
            etEditItem.setText(intent.getStringExtra("title"));
        } else {
            Task t = (Task) intent.getSerializableExtra("task");
            String title = t.getTitle();
            String description = t.getDescription();
            String date = t.getDate();
            int priority = t.getPriority();
            id = t.getId();
            etEditItem.setText(title);
            etDescription.setText(description);
            etDate.setText(date);
            sbPriority.setProgress(priority);
        }

        position = intent.getIntExtra("position", -1);

        //below is logic for the task priority SeekBar and its corresponding TextView tag and priority score.
        sbPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            /**
             * This method contains logic for updating the SeekBar's two TextViews which display the
             * general priority tag and the actual SeekBar value.
             * @param seekBar
             * @param progress
             * @param fromUser
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (progress < 34) {
                    tvPriorityTag.setText(R.string.low);
                    tvPriorityTag.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tvPriorityScore.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if (progress < 67 && progress >= 34) {
                    tvPriorityTag.setText(R.string.medium);
                    tvPriorityTag.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    tvPriorityScore.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    tvPriorityTag.setText(R.string.high);
                    tvPriorityTag.setTextColor(getResources().getColor(R.color.colorAccent));
                    tvPriorityScore.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                tvPriorityScore.setText(String.valueOf(progress));
            }

            /**
             * A required method to override.
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            /**
             * A required method to override.
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //The below logic updates the initial state of the SeekBar when a user loads the edit task screen.
        int sbProgressVal = sbPriority.getProgress();
        tvPriorityScore.setText(Integer.toString(sbProgressVal));
        if (sbProgressVal < 34) {
            tvPriorityTag.setText(R.string.low);
            tvPriorityTag.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvPriorityScore.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (sbProgressVal < 67 && sbProgressVal >= 34) {
            tvPriorityTag.setText(R.string.medium);
            tvPriorityTag.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            tvPriorityScore.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            tvPriorityTag.setText(R.string.high);
            tvPriorityTag.setTextColor(getResources().getColor(R.color.colorAccent));
            tvPriorityScore.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    /**
     * This method adds items to the action bar.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_main_menu, menu);
        return true;
    }

    /**
     * This method is called when a user presses the save button.
     * @param mi
     */
    public void onSave(MenuItem mi) {
        Intent intent = new Intent();
        String title = etEditItem.getText().toString();
        String description = etDescription.getText().toString();
        String date = etDate.getText().toString();
        int priority = sbPriority.getProgress();
        int status = 0;
        if (tbtnStatus.isChecked()) {
            status = 1;
        }
        Task t = new Task(id, title, description, priority, date, status);
        intent.putExtra("task", t);
        intent.putExtra("position", position);
        //set result code and bundle data for response
        setResult(RESULT_OK, intent);
        //closes the activity and passes data to the parent
        finish();
    }

    /**
     * This method is called when a user cancels editing the item.
     * @param mi
     */
    public void onCancel(MenuItem mi) {
        Intent intent = new Intent();
        //set the result code
        setResult(RESULT_CANCELED, intent);
        //closes the activity and passes data to the parent
        finish();
    }

    /**
     * This method shows the DatePicker for selecting task due dates.
     * The method is called when a user presses on etDate.
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * A required method that contains logic for processing a date selected by a user in the DatePicker.
     * @param view
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        etDate.setText(sdf.format(c.getTime()));
    }


    /**
     * Required for creating the DatePicker.
     */
    public static class DatePickerFragment extends DialogFragment {

        /**
         * This method contains logic for loading the DatePicker.
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
}
