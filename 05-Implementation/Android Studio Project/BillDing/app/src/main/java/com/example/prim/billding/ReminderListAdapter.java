package com.example.prim.billding;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Desktop on 2/16/2017.
 */

//This is the ReminderList Adapter for printing the data in reminder_list
//code is based on the answer here:
//http://stackoverflow.com/questions/29517044/android-listview-with-subtext

//fix is based from here
//http://www.androidhive.info/2012/02/android-custom-listview-with-image-and-text/

public class ReminderListAdapter extends BaseAdapter {
    private Activity activity;
    private List<Reminder> reminderList;
    private LayoutInflater inflater;

    //this part used to pass the context in the first version, apparently you had to pass the activity itself
    //which is evident  in the original Adapter class that passes the activity too, in the form of "this"
    public ReminderListAdapter(Activity a, List<Reminder> reminderList) {
        activity = a;
        this.reminderList = reminderList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return reminderList.size();
    }

    @Override
    public Reminder getItem(int pos) {
        return reminderList.get(pos);
    }

    public long getItemId(int pos) {
        long dummy = 1;
        return dummy;
    }

    public View getView(int pos, View convView, ViewGroup parent) {
        ViewHolder holder;
        View row = convView;
        if (row == null) {
            holder = new ViewHolder();
            row  = inflater.inflate(R.layout.reminder_list, parent, false);
            holder.reminderName = (TextView) row.findViewById(R.id.reminderName);
            holder.reminderDate = (TextView) row.findViewById(R.id.reminderDate);
            holder.reminderAmt = (TextView) row.findViewById(R.id.reminderAmt);
            holder.reminderTag = (ImageView) row.findViewById(R.id.reminderTag);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Reminder rem = reminderList.get(pos);

        DateFormat fomat = new SimpleDateFormat("EEEE");
        String day = fomat.format(rem.getDate());
		
		//sets the reminder name and date on their respective textviews
        holder.reminderName.setText(rem.getName());
        holder.reminderDate.setText(DateFormat.getDateInstance().format(rem.getDate()) + "\n" + day);

        //this calendar gets the current date, to compare it to the reminder's due date
		Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY,0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
        long currentDate = c1.getTimeInMillis();
		
		//a small imageview to the right (named 'reminderTag') will change colors depending on the due date
		//due today makes it green, overdue makes it red, due within 3 days makes it orange, otherwise it is transparent
        if(rem.getDate().getTime() == currentDate){
            holder.reminderTag.setImageResource(R.drawable.green_bar);
        }
        else if (rem.getDate().getTime() < currentDate){
            holder.reminderTag.setImageResource(R.drawable.red_bar);
        }
        else if ((rem.getDate().getTime() > currentDate) && (currentDate >= rem.getDate().getTime() - 259200000)) {
            holder.reminderTag.setImageResource(R.drawable.oran_bar);
        }
        else{
            holder.reminderTag.setImageResource(R.drawable.trans_bar);
        }
		
		//sets reminder amount with proper formatting
        holder.reminderAmt.setText(String.format("%1$,.2f", rem.getAmt()));
        return row;
    }
	
	//a class that holds all view elements that will be modified
    private class ViewHolder {
        TextView reminderName, reminderAmt, reminderDate;
        ImageView reminderTag;
    }
}
