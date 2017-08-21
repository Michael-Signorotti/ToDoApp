package com.example.michael.todoapp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * This class is based of a tutorial located at the following link.
 * http://guides.codepath.com/android/Using-a-BaseAdapter-with-ListView#optimization-using-the-viewholder-pattern
 */
public class CustomListAdapter extends BaseAdapter {

    //the application context
    private Context context;
    //the adapter's data source
    private ArrayList<Task> items;

    /**
     * The class constructor.
     * @param context:  The application context.
     * @param items: The data source for the adapter.
     */
    public CustomListAdapter(Context context, ArrayList<Task> items) {
        this.context = context;
        this.items = items;
    }

    /**
     * A required method to override which returns the number of tasks.
     * @return
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /**
     * A required method to override which returns a task.
     * @param position:  The position in the data source.
     * @return: A task.
     */
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    /**
     * A required method which returns the position of an item.
     * @param position: The position of an item.
     * @return: The position.
     */
    @Override
    public long getItemId(int position) {

        return position;
    }

    /**
     * A required method that creates an updated view.
     * @param position: The position of the task to display.
     * @param convertView: A recycled view.
     * @param parent
     * @return:  An updated view.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.list_view_rows, parent, false);
        }

        // get current item to be displayed
        Task currentItem = (Task) getItem(position);

        // get the TextViews that should be updated
        TextView textViewItemName = (TextView)
                convertView.findViewById(R.id.tvTitle);
        TextView textViewItemPriority = (TextView)
                convertView.findViewById(R.id.tvPriority);
        TextView textViewItemDaysLeft = (TextView)
                convertView.findViewById(R.id.tvDaysLeft);

        //update the TextViews with the required text and color scheme
        textViewItemName.setText(currentItem.getTitle());
        int progress = currentItem.getPriority();
        if (progress < 34) {
            textViewItemPriority.setText(R.string.low);
            textViewItemPriority.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else if (progress < 67 && progress >=34) {
            textViewItemPriority.setText(R.string.medium);
            textViewItemPriority.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        } else {
            textViewItemPriority.setText(R.string.high);
            textViewItemPriority.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        Date date = null;
        try {
            String stringDate = currentItem.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
            date = sdf.parse(stringDate);

        } catch(ParseException e) {
            System.out.println(e.toString());
        }
        int numDaysLeft = daysBetween(new Date(), date);

        if (numDaysLeft < 0) {
            numDaysLeft = 0;
        }
        String daysLeft = numDaysLeft + " Days";
        textViewItemDaysLeft.setText(daysLeft);

        if (numDaysLeft == 0) {
            textViewItemDaysLeft.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else if (numDaysLeft > 0 && numDaysLeft < 4) {
            textViewItemDaysLeft.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        } else {
            textViewItemDaysLeft.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }

        // returns the view for the current row
        return convertView;
    }

    /**
     * This method calculated the number of days between two dates.
     * @param d1: The current date.
     * @param d2: A due date.
     * @return
     */
    public int daysBetween(Date d1, Date d2){
        long diff = d2.getTime() - d1.getTime();
        //need to convert to days because diff is in terms of milliseconds.
        int days = (int)(diff)/(1000 * 60 * 60 * 24);
        return days;
    }
}