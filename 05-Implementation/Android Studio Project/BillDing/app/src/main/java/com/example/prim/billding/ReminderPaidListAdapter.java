package com.example.prim.billding;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Desktop on 3/16/2017.
 */

//This is the ReminderPaidList Adapter for printing the data in reminder_history
//code is based on the answer here:
//http://stackoverflow.com/questions/29517044/android-listview-with-subtext

//fix is based from here
//http://www.androidhive.info/2012/02/android-custom-listview-with-image-and-text/
public class ReminderPaidListAdapter extends BaseAdapter {
     private Activity activity;
     private List<ReminderPaid> reminderList;
     private LayoutInflater inflater;

     //this part used to pass the context in the first version, apparently you had to pass the activity itself
     //which is evident  in the original Adapter class that passes the activity too, in the form of "this"
     public ReminderPaidListAdapter(Activity a, List<ReminderPaid> reminderList) {
          activity = a;
          this.reminderList = reminderList;
          inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     }

     public int getCount() {
          return reminderList.size();
     }

     @Override
     public ReminderPaid getItem(int pos) {
          return reminderList.get(pos);
     }

     public long getItemId(int pos) {
          long dummy = 1;
          return dummy;
     }

     public View getView(int pos, View convView, ViewGroup parent) {
          ReminderPaidListAdapter.ViewHolder holder;
          View row = convView;
          if (row == null) {
               holder = new ReminderPaidListAdapter.ViewHolder();
               row  = inflater.inflate(R.layout.reminder_list, parent, false);
               holder.reminderName = (TextView) row.findViewById(R.id.reminderName);
               holder.reminderDate = (TextView) row.findViewById(R.id.reminderDate);
               holder.reminderPaidDate = (TextView) row.findViewById(R.id.reminderPaidDate);
               holder.reminderAmt = (TextView) row.findViewById(R.id.reminderAmt);

               row.setTag(holder);
          } else {
               holder = (ReminderPaidListAdapter.ViewHolder) row.getTag();
          }

          ReminderPaid rem = reminderList.get(pos);

          DateFormat fomat = new SimpleDateFormat("EEEE");
          String day = fomat.format(rem.getPaidDate());

          holder.reminderName.setText(rem.getName());
          holder.reminderDate.setText(DateFormat.getDateInstance().format(rem.getPaidDate()) + "\n" + day);
          //holder.reminderPaidDate.setText(DateFormat.getDateInstance().format(rem.getPaidDate()) + "\n" + day);
          //holder.reminderAmt.setText(Double.toString(rem.getAmt()));
          DecimalFormat format = new DecimalFormat("0.00");
          holder.reminderAmt.setText(format.format(rem.getAmt()));
          return row;
     }

     private class ViewHolder {
          TextView reminderName, reminderAmt, reminderDate, reminderPaidDate;
     }
}
