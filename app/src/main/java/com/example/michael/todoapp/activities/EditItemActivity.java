package com.example.michael.todoapp.activities;

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

import com.example.michael.todoapp.R;
import com.example.michael.todoapp.fragments.DatePickerFragment;
import com.example.michael.todoapp.models.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class contains the logic behind the edit item activity. The edit item activity is used
 * for both new task submissions and existing tasks which are being edited.
 */
public class EditItemActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private EditText etTitle;
    private EditText etDescription;
    private SeekBar sbPriority;
    private EditText etDate;
    private ToggleButton tbtnStatus;
    private TextView tvPriorityTag;
    private TextView tvPriorityScore;
    private int position;
    private boolean showingCompleted;
    private final int RESULT_OK = 1;
    private final int RESULT_CANCELED = -1;
    private long id = -1;

    /**
     * A required method that is called when the edit item activity is opened by a user.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        getSupportActionBar().setTitle("Edit Item");

        etTitle = (EditText) findViewById(R.id.etTitle);
        etDescription = (EditText) findViewById(R.id.etDescription);
        sbPriority = (SeekBar) findViewById(R.id.sbPriority);
        etDate = (EditText) findViewById(R.id.etDate);
        tbtnStatus = (ToggleButton) findViewById(R.id.tbtnStatus);
        tvPriorityTag = (TextView) findViewById(R.id.tvPriorityTag);
        tvPriorityScore = (TextView) findViewById(R.id.tvPriorityScore);

        //get information passed from the main activity
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        showingCompleted = intent.getBooleanExtra("showingCompleted", false);

        //check if editing an existing task or creating a new one
        if (position == -1) {
            etTitle.setText(intent.getStringExtra("title"));
        } else {
            Task t = (Task) intent.getSerializableExtra("task");
            String title = t.getTitle();
            String description = t.getDescription();
            String date = t.getDate();
            int priority = t.getPriority();
            id = t.getId();
            etTitle.setText(title);
            etDescription.setText(description);
            etDate.setText(date);
            sbPriority.setProgress(priority);
            tbtnStatus.setChecked(t.isStatus() == 1);
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
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_main_menu, menu);
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
        if (id == R.id.icSave) {
            onSave();
        } else if (id == R.id.icCancel) {
            onCancel();
        }
        return true;
    }

    /**
     * This method is called when a user presses the save button.
     */
    public void onSave() {
        Intent intent = new Intent();
        String title = etTitle.getText().toString();
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
        intent.putExtra("showingCompleted", showingCompleted);
        //set result code and bundle data for response
        setResult(RESULT_OK, intent);
        //closes the activity and passes data to the parent
        finish();
    }

    /**
     * This method is called when a user cancels editing the item.
     */
    public void onCancel() {
        Intent intent = new Intent();
        //set the result code
        setResult(RESULT_CANCELED, intent);
        //closes the activity and passes data to the parent
        finish();
    }

    /**
     * This method shows the DatePicker for selecting task due dates.
     * The method is called when a user presses on etDate.
     *
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * A required method that contains logic for processing a date selected by a user in the DatePicker.
     *
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

}
