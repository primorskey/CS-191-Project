package com.example.prim.billding;
/*
     Program Name:
          BillDing!
     Author:
          Group SYD:
               Anthony Cornell M. Dacoco
               Paul Matthew L. Sason
               Arthur Kevin N. Yiu

     “This is a course requirement for CS 192 Software Engineering II under the
     supervision of Asst. Prof. Ma. Rowena C. Solamo of the Department of Computer
     Science, College of Engineering, University of the Philippines, Diliman
     for the AY 2015-2016”
*/
/*
    VERSION 1.7

    {version number}  <author>  |date (format of MM/dd/yyyy)|
        [notes]

    {1.0} <Anthony Cornell Dacoco> |01/29/2017|
        [tried implementing everything, however the only thing that i didnt manage to implement
        is the displaying of subtexts for listview, however displaying of only the reminder name
        would work]

    {1.1} <Paul Matthew Sason> [01/31/2017]
        [implemented listview that can show amount and date using the custom adapter, adjusted the UI]

    {1.2} <Anthony Cornell Dacoco> |02/01/2017|
        [made it so that when there are no reminders saved, will show that "there are no reminders
        to show" since the initial implementation of this message was from the standard ArrayAdapter
        and was using the non updated xml files]

    {1.3} <Arthur Yiu> [02/01/2017]
        [Linked the 2 activities together. (main_activity.java to add_reminder.java)]

    {1.4} <Anthony Cornell Dacoco> |02/02/2017|
        [moved the function calls to onResume from onCreate, changed the output format for the
        reminder amount due]

    {1.5} <Arthur Yiu> [02/02/2017]
        [Made changes to add_reminder to remove leading and trailing whitespaces in reminder name and reminder description fields.]

    {1.6} <Paul Matthew Sason> [02/02/2017]
        [adjusted UI even more, used specific width and height values for better alignment, added comments]

    {1.7} <Anthony Cornell Dacoco> [02/02/2017]
        [made the indention into 5 spaces, added more comments to the code, added the license,
        cleaned code from unneeded lines, comments and functions]

    {1.8} <Arthur Yiu> [02/02/2017]
        [Made changes to add_reminder to only allow the user to type in the alphabet along with digits in the reminder name
        and reminder description fields.]

    {2.0} <Paul Matthew Sason> [02/05/2017]
    [due to minor changes in file writing (with regards to commas), file reading was modified accordingly]
 */

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Activity;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    //function for loading the file
    //checks if reminder.txt exists
    //if it exists, read data
    //else create reminder.txt
    //returns an array of strings, each element is a reminder line in reminder.txt

    public String[] loadFile(String fileName) {
        String[] reminderStrings = {""};
        File file = getBaseContext().getFileStreamPath(fileName);
        if (file.exists()) {
            //if file exists, calls function Load to parse the data
            //passes the parsed data to reminderStrings array
            reminderStrings = Load(file);
        } else {
            try {
                //writes file named "reminders.db" with first line as "0"
                //the "0" is to represent there are 0 reminders in the file
                FileOutputStream newRemDB = openFileOutput(fileName, MODE_PRIVATE);
                OutputStreamWriter writer = new OutputStreamWriter(newRemDB);
                writer.write("0");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //calls loadFile again to read the written file
            reminderStrings = loadFile(fileName);
        }
        return reminderStrings;
    }

    //parses reminder.txt and passes the returns the read data in the form of string array
    public static String[] Load(File file) {
        FileInputStream inputStream = null;
        String[] reminderArray = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(in);
        try {
            String tempString = br.readLine();
            int numRems = Integer.parseInt(tempString);
            reminderArray = new String[numRems];
            for (int i = 0; i < numRems; i++) {
                tempString = br.readLine();
                reminderArray[i] = tempString;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reminderArray;
    }

    //converts read string array read from data into a list of Reminder objects
    public ArrayList<Reminder> StringToReminder(String[] dbStrings) {
        ArrayList<Reminder> reminderList = new ArrayList<Reminder>();
        int numRems = dbStrings.length;
        //this forloop splits each element of the read string array by the delimiter ","
        //each split part is now converted into Reminder attributes
        //the rules are the following:
        /*
            index number -> reminder attribute
            0 -> reminder name
            1 -> reminder description
            2 -> reminder amount due
            3 -> reminder date due
         */
        //the created temporary Reminder object is now appended to the ArrayList
        //"comma0" is brought back as "comma" and "comma1" into an actual comma
        for (int i = 0; i < numRems; i++) {
            String[] choppedRem = dbStrings[i].split(",");
            choppedRem[0] = choppedRem[0].replace("comma0", "comma");
            choppedRem[0] = choppedRem[0].replace("comma1", ",");
            choppedRem[1] = choppedRem[1].replace("comma0", "comma");
            choppedRem[1] = choppedRem[1].replace("comma1", ",");

            Reminder tempRem = new Reminder(choppedRem[0]);
            tempRem.setDesc(choppedRem[1]);
            tempRem.setAmt(Double.parseDouble(choppedRem[2]));
            tempRem.setDate(choppedRem[3]);
            reminderList.add(tempRem);
        }
        return reminderList;
    }

    //fileReading/writing for log
    public String[] loadPaidFile(String fileName) {
        String[] reminderStrings = {""};
        File file = getBaseContext().getFileStreamPath(fileName);
        if (file.exists()) {
            //if file exists, calls function Load to parse the data
            //passes the parsed data to reminderStrings array
            reminderStrings = Load(file);
        } else {
            try {
                //writes file named "history.txt" with first line as "0"
                //the "0" is to represent there are 0 reminders in the file
                FileOutputStream newRemDB = openFileOutput(fileName, MODE_PRIVATE);
                OutputStreamWriter writer = new OutputStreamWriter(newRemDB);
                writer.write("0");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //calls loadFile again to read the written file
            reminderStrings = loadPaidFile(fileName);
        }
        return reminderStrings;
    }
    //parses history.txt and passes the returns the read data in the form of string array
    public static String[] LoadPaid(File file) {
        FileInputStream inputStream = null;
        String[] reminderArray = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(in);
        try {
            String tempString = br.readLine();
            int numRems = Integer.parseInt(tempString);
            reminderArray = new String[numRems];
            for (int i = 0; i < numRems; i++) {
                tempString = br.readLine();
                reminderArray[i] = tempString;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reminderArray;
    }
    //function to update history.txt database file
    public void filePaidWriter(String[] array, String name, String amt, String desc, String date,String paidDate){
        int size = array.length;
        int i;
        int added = 0;
        String file = "history.txt";
        String newline = "\n";
        ArrayList<String> newList = new ArrayList<String>();
        for(i=0;i<size;i++){
            newList.add(array[i]);
        }
        String toAdd = name + "," + desc + "," + amt + "," + date + ","+ paidDate;
        if(size==0){
            newList.add(toAdd);
        }
        else{
            for(i=0;i<newList.size();i++){
                String temp1 = newList.get(i);
                String[] parts1 = temp1.split(",");
                try{
                    Date date1 = new SimpleDateFormat("MM/dd/yyyy").parse(parts1[4]);
                    Date date2 = new SimpleDateFormat("MM/dd/yyyy").parse(paidDate);
                    Date today = new Date();
                    if(today.getTime()-date1.getTime() > today.getTime()-date2.getTime()){
                        newList.set(i,toAdd);
                        added = 1;
                        while(i<newList.size()-1){
                            String temp2 = newList.get(i+1);
                            newList.set(i+1,temp1);
                            temp1 = temp2;
                            i++;
                        }
                        newList.add(temp1);
                        i=newList.size();
                    }
                } catch(ParseException e){
                    e.printStackTrace();
                }
            }
            if(added == 0){
                newList.add(toAdd);
            }
        }
        try{
            FileOutputStream fOut = openFileOutput(file, Context.MODE_PRIVATE); //opens the database file in write mode
            fOut.write(Integer.toString(newList.size()).getBytes());
            fOut.write(newline.getBytes());
            for(i=0;i<newList.size();i++){
                fOut.write(newList.get(i).getBytes());
                fOut.write(newline.getBytes());
            }
            fOut.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //function called whenever a reminder is tagged as paid, calls function filePaidWriter to updated the database
    public void writeToPaid(ArrayList<Reminder> reminderArray, int writeThis){
        String historyDB = "history.txt";
        String[] reminderPaidArray = loadPaidFile(historyDB);
        DateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
        Date today = new Date();
        String paidName = reminderArray.get(writeThis).getName();
        String paidAmt =  Double.toString(reminderArray.get(writeThis).getAmt());
        String paidDesc =  reminderArray.get(writeThis).getDesc();
        String dueDate =  dateformat.format(reminderArray.get(writeThis).getDate());
        String paidDate =  dateformat.format(today);
        filePaidWriter(reminderPaidArray,paidName,paidAmt,paidDesc,dueDate,paidDate);

    }

    //file reading for settings
    public boolean fileExistence(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }
    //file reading for setting up when the notification will be sent
    public void readFile(String fname){
         //default values for hour and min (7:00)
        int hour = 7;
        int min = 0;
         //check if file exists
        if(fileExistence(fname)){
             //if it exists, try to open it and read its contents
            try {
                FileInputStream fin = openFileInput(fname);
                int c;
                String temp="";
                while( (c = fin.read()) != -1){
                    temp = temp + Character.toString((char)c);
                }
                String[] splitMe = temp.split(",");
                hour = Integer.parseInt(splitMe[0]);
                min = Integer.parseInt(splitMe[1]);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
             //if it doesn't exist, create a new file and write the default notification time
            try{
                FileOutputStream fOut = openFileOutput(fname, Context.MODE_PRIVATE); //opens the database file in write mode
                String toWrite = "7,00";
                fOut.write(toWrite.getBytes());
                fOut.close();
                readFile(fname);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
             //creation of alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Add_Reminder.ALARM_SERVICE);    //alarmManager manages the alarm service
         // Get a calendar instance and set its date to the values of hour and min. These 2 variables will either have the read from file value or the default value.
        Calendar now = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY,hour);
        calendar2.set(Calendar.MINUTE,min);
        calendar2.set(Calendar.SECOND,0);
        long alarmTime=0;     //variable placeholder for comparing time values of calendar dates
         //check whether a notification was already made or not
        if(calendar2.getTimeInMillis()<=now.getTimeInMillis()){
            alarmTime = calendar2.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);
        }
        else{
            alarmTime = calendar2.getTimeInMillis();
        }
          //setting up of actual alarm: create the intent and set an alarm that repeats everyday
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, new Intent(getApplicationContext(),ReminderService.class),PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC,alarmTime,AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    ArrayList<Reminder> reminderArray = new ArrayList<Reminder>();
    String fileName = "reminders.txt";
    ListView listView;
    ReminderListAdapter adapter;
    String [] remArr;

     // Overriding of what the application does when it is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        readFile("settings.txt"); //call the function that sets the alarm
    }

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //remArr will hold the data read from reminders.db
        remArr = loadFile(fileName);
        //if remArr returns with length 0, meaning there are no reminders
        //display that there are no reminders
        if (remArr.length == 0) {
            //String [] emptyDB = {"There are no reminders to show"};
            TextView textView = (TextView) findViewById(R.id.error_emptyRem);
            textView.setText("There are no reminders to show");
        } else {
            //remArr is now converted to List of Reminder objects
            reminderArray = StringToReminder(remArr);
            adapter = new ReminderListAdapter(this, reminderArray);
            listView = (ListView) findViewById(R.id.reminder_list);
            listView.setAdapter(adapter);
            registerForContextMenu(listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    remArr = loadFile(fileName);
                    Bundle b = new Bundle(); //creates a bundle that serves as a placeholder for the data that will be sent to View_Reminder.java
                    b.putInt("position", position); //places "position" in bundle which serves as an identifier for View_Reminder.java and position which holds the position of the chosen Reminder object
                    b.putString("key", remArr[position]); //places "key" in bundle which serves as an identifier for View_Reminder.java abd renArr which contains the data to be passed
                    Intent intent = new Intent(view.getContext(), View_Reminder.class); //creates an intent that enables the binding of the two activities
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //to make the user return to the MainActivity even if the new Activity creates a sub Activity
                    intent.putExtras(b); //add the data to the intent for later accessing of the new started activity
                    startActivity(intent); //starts the View_Reminder activity
                    adapter.notifyDataSetChanged(); //handles the update of the UI if a change in the database would happen (through edit,deletion or tagging as paid through View_Reminder activity)
                }
            });
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); //creates a floating action action for the user to press when they want to add a new reminder
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remArr = loadFile(fileName);
                Bundle b = new Bundle(); //creates a bundle that serves as a placeholder for the data that will be sent to add_reminder.java
                b.putStringArray("key", remArr); //places "key" in bundle which serves as an identifier for add_reminder.java abd renArr which contains the data to be passed
                Intent intent = new Intent(view.getContext(), Add_Reminder.class); //creates an intent that enables the binding of the two activities
                intent.putExtras(b); //add the data to the intent for later accessing of the new started activity
                startActivity(intent); //starts the add_reminder activity
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SpinnerView.class);
            startActivity(intent);
            return true;
        }
        // if the user clicked on the help icon
        if (id == R.id.action_help) {
             //create a dialog filled with text views that teach the user how to use the application as well as developer information
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.how_to_layout);
            dialog.setTitle("Help");
            TextView textView3 = (TextView) dialog.findViewById(R.id.textView3);
            textView3.setText("BillDing! is a reminder application for your bills!");
            TextView textView2 = (TextView) dialog.findViewById(R.id.textView2);
            textView2.setText("BillDing! notifies you when you have a bill that is due within 3 days. Notifications are sent at 7 a.m. by default. You can change when you would like to receive notifications by pressing the settings icon.");
            TextView howto1 = (TextView) dialog.findViewById(R.id.howto1);
            howto1.setText("Press the + button to add a reminder.");
            TextView howto2 = (TextView) dialog.findViewById(R.id.howto2);
            howto2.setText("Tap a reminder to see more information about it. Here the selected reminder can be edited, deleted, or tagged as paid by pressing their respective icons. Alternatively, you can hold a reminder to edit, delete, or tag it as paid.");
            TextView howto3 = (TextView) dialog.findViewById(R.id.howto3);
            howto3.setText("Tap the log history icon to show bills that have been paid within the last 30 days.");
            TextView howto4 = (TextView) dialog.findViewById(R.id.howto4);
            howto4.setText("About us\nBillDing was developed by Paul Sason, Arthur Yiu, and Anthony Dacoco. (SYD)");
            dialog.show();
            return true;
        }
        if (id==R.id.log_history){
            Bundle b = new Bundle(); //creates a bundle that serves as a placeholder for the data that will be sent to add_reminder.java
            Intent intent = new Intent(this, ReminderHistory.class); //creates an intent that enables the binding of the two activities
            this.startActivity(intent); //starts the add_reminder activity
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.action_settings);
        //item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    //function to show reminder_options menu when a reminder is held long enough
    //the menu has edit,delete or tag as paid as its choices
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if (v.getId()==R.id.reminder_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            super.onCreateContextMenu(menu,v,menuInfo);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.reminder_options,menu);
            menu.setHeaderTitle(reminderArray.get(info.position).getName());
        }
    }
    //function to catch the result of picking any of the choices from reminder_options menu and to follow through
    @Override
    public boolean onContextItemSelected(MenuItem item){
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getItemId()){
            //goes to Edit_Reminder activity by passing the reminderArray and the current position of the chosen reminder
            case R.id.edit:
                remArr = loadFile(fileName);
                Bundle b = new Bundle(); //creates a bundle that serves as a placeholder for the data that will be sent to edit_reminder.java
                b.putInt("editThis", info.position); //will hold the current position of the chosen reminder in the database
                b.putStringArray("reminderArray", remArr); //string version of the database
                Intent intent = new Intent(this, Edit_Reminder.class); //creates an intent that enables the binding of the two activities
                intent.putExtras(b); //add the data to the intent for later accessing of the new started activity
                this.startActivity(intent); //starts the edit_reminder activity
                return true;
            //deletes the chosen reminder from the database and updates the activity adaptor
            case R.id.delete:
                //confirmation dialog for deletion
                DialogInterface.OnClickListener dialogClickListenerDelete=new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                String newline = "\n";
                                String comma = ",";
                                SimpleDateFormat dateFormat =  new SimpleDateFormat("MM/dd/yyyy");
                                reminderArray.remove(info.position);
                                try {
                                    FileOutputStream fOut = openFileOutput(fileName, Context.MODE_PRIVATE); //opens the database file in write mode
                                    fOut.write(Integer.toString(reminderArray.size()).getBytes()); //writes the current number of reminders
                                    fOut.write(newline.getBytes()); //writes a newline
                                    for(int i=0;i<reminderArray.size();i++){
                                        fOut.write(reminderArray.get(i).getName().getBytes()); //writes the reminder name
                                        fOut.write(comma.getBytes()); //writes a comma
                                        fOut.write(reminderArray.get(i).getDesc().getBytes()); //writes the reminder description if any
                                        fOut.write(comma.getBytes()); //writes a comma
                                        fOut.write(Double.toString(reminderArray.get(i).getAmt()).getBytes()); //writes the reminder amount
                                        fOut.write(comma.getBytes()); //writes a comma
                                        String date = dateFormat.format(reminderArray.get(i).getDate());
                                        fOut.write(date.getBytes()); //writes the reminder due date
                                        fOut.write(newline.getBytes()); //writes a newline

                                    }
                                    fOut.close(); //closes the database file
                                    Toast.makeText(getBaseContext(), "Reminder Deleted", Toast.LENGTH_SHORT).show(); //feedback for successfully adding to the database file
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (reminderArray.size() == 0) {
                                    //String [] emptyDB = {"There are no reminders to show"};
                                    TextView textView = (TextView) findViewById(R.id.error_emptyRem);
                                    textView.setText("There are no reminders to show");
                                }
                                adapter.notifyDataSetChanged();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(getBaseContext(), "Deletion Cancelled", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder confirmDelete = new AlertDialog.Builder(this);
                confirmDelete.setMessage("Are you sure you want to delete this bill?").setPositiveButton("Yes",dialogClickListenerDelete).setNegativeButton("No",dialogClickListenerDelete).show();
                return true;
            //converts the reminder object into a reminder_paid objects, deletes the reminder from the reminders.txt database and
            //writes the reminder_paid object into the history.txt database
            case R.id.paid:
                //confirmation of tagging as paid
                DialogInterface.OnClickListener dialogClickListenerPaid=new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Log.v("getID",Integer.toString(info.position));
                                Log.v("getID",Integer.toString(reminderArray.size()));
                                String newline = "\n";
                                String comma = ",";
                                SimpleDateFormat dateFormat =  new SimpleDateFormat("MM/dd/yyyy");
                                writeToPaid(reminderArray,info.position); //updates the history.txt database
                                reminderArray.remove(info.position); //removes the chosen reminder in the reminderArray
                                //updates the reminder.txt database with the removed reminder
                                try {
                                    FileOutputStream fOut = openFileOutput(fileName, Context.MODE_PRIVATE); //opens the database file in write mode
                                    fOut.write(Integer.toString(reminderArray.size()).getBytes()); //writes the current number of reminders
                                    fOut.write(newline.getBytes()); //writes a newline
                                    for(int i=0;i<reminderArray.size();i++){
                                        fOut.write(reminderArray.get(i).getName().getBytes()); //writes the reminder name
                                        fOut.write(comma.getBytes()); //writes a comma
                                        fOut.write(reminderArray.get(i).getDesc().getBytes()); //writes the reminder description if any
                                        fOut.write(comma.getBytes()); //writes a comma
                                        DecimalFormat format = new DecimalFormat("0.00");
                                        fOut.write(format.format(reminderArray.get(i).getAmt()).getBytes()); //writes the reminder amount
                                        fOut.write(comma.getBytes()); //writes a comma
                                        String date = dateFormat.format(reminderArray.get(i).getDate());
                                        fOut.write(date.getBytes()); //writes the reminder due date
                                        fOut.write(newline.getBytes()); //writes a newline
                                    }
                                    fOut.close(); //closes the database file
                                    Toast.makeText(getBaseContext(), "Reminder Tagged as Paid", Toast.LENGTH_SHORT).show(); //feedback for successfully adding to the database file
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (reminderArray.size() == 0) {
                                    //String [] emptyDB = {"There are no reminders to show"};
                                    TextView textView = (TextView) findViewById(R.id.error_emptyRem);
                                    textView.setText("There are no reminders to show");
                                }
                                adapter.notifyDataSetChanged();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(getBaseContext(), "Paid Confirmation Cancelled", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder confirmPaid = new AlertDialog.Builder(this);
                confirmPaid.setMessage("Are you sure this bill is paid?").setPositiveButton("Yes",dialogClickListenerPaid).setNegativeButton("No",dialogClickListenerPaid).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}


