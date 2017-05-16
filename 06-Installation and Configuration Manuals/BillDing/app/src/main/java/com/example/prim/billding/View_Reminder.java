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
    VERSION 1.1

    {version number}  <author>  |date (format of MM/dd/yyyy)|
        [notes]

    {1.0} <Arthur Yiu> |01/29/2017|
    [Was able to create a complete and working add reminder activty class with UI]

    {1.1} <Arthur Yiu> |01/29/2017|
    [Made changes to remove leading and trailing whitespaces in reminder name and reminder description along with
    changes to the activity_add_reminder.xml to only allow the alphabet, digits, and a period for user input in the
    bill reminder name and bill reminder description]

    {2.0} <Paul Matthew Sason |02/05/2017|
    [to deal with commas messing up the csv, the way the database writes is now slightly modified,
    instead of trying to filter commas out in the xml (which caused a really annoying problem with the enter button)]
*/

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.String;
import java.lang.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
    This is a class that enables the user to add a reminder to the database
     It has its own xml file in which the user can input data in and when the save
     button is pressed, the data is then gathered and save to the database.
*/
public class View_Reminder extends AppCompatActivity {
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

    //Declaration of variables used
    TextView nameField, amountField, dateField, descriptionField, dueTag; //fields for the user to input data in
    ImageView alert;

    ArrayList<Reminder> reminderArray = new ArrayList<Reminder>();
    String fileName = "reminders.txt";
    ListView listView;
    ReminderListAdapter adapter;
    String [] remArr;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reminder);

        //findViewById(R.id.activity_add__reminder).setOnTouchListener(this);

        //identification of xml elements

    }
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_view_reminder);


        Bundle b = this.getIntent().getExtras(); //creates a bundle to store the data given by MainActivity
        final String array = b.getString("key"); //creates a String array to contain the data found in b
        final int position = b.getInt("position");
        pos = position;
        remArr=loadFile(fileName);
        String[] dataReminder = remArr[pos].split(",");
        dataReminder[0] = dataReminder[0].replace("comma0", "comma");
        dataReminder[0] = dataReminder[0].replace("comma1", ",");
        dataReminder[1] = dataReminder[1].replace("comma0", "comma");
        dataReminder[1] = dataReminder[1].replace("comma1", ",");

        nameField = (TextView) findViewById(R.id.editText); //reminder name
        nameField.setText(dataReminder[0]);
        nameField.setClickable(false);
        amountField = (TextView) findViewById(R.id.editText2); //reminder amount
        amountField.setText(dataReminder[2]);
        amountField.setClickable(false);
        dateField = (TextView) findViewById(R.id.editText3); //reminder due date
        dateField.setText(dataReminder[3]);
        dateField.setClickable(false);
        descriptionField = (TextView) findViewById(R.id.editText4); //reminder description
        descriptionField.setText(dataReminder[1]);
        //descriptionField.setClickable(false);
        descriptionField.setMovementMethod(new ScrollingMovementMethod());

        dueTag = (TextView) findViewById(R.id.alerttxt);
        alert = (ImageView) findViewById(R.id.alert);

		//the due date is parsed so that we can make a calendar instance of it
		String[] dateparts = dataReminder[3].split("/");
        Calendar c0 = Calendar.getInstance();
        c0.set(Calendar.MONTH, Integer.parseInt(dateparts[0])-1);
        c0.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateparts[1]));
        c0.set(Calendar.YEAR, Integer.parseInt(dateparts[2]));
        c0.set(Calendar.HOUR_OF_DAY,0);
        c0.set(Calendar.MINUTE, 0);
        c0.set(Calendar.SECOND, 0);
        c0.set(Calendar.MILLISECOND, 0);
        long dueDate = c0.getTimeInMillis();

		//a calendar instance of the date today, for comparison
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY,0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
        long currentDate = c1.getTimeInMillis();
		
		//this makes a colored alert icon on the view screen depending on its due date
		//similar to the reminderlist adapter, green = today, red = overdue, orange = within 3 days, transparent = non
        if(dueDate == currentDate){
            alert.setImageResource(R.drawable.green_alert);
            dueTag.setText("    This bill is due today!");
        }
        else if (dueDate < currentDate){
            alert.setImageResource(R.drawable.red_alert);
            dueTag.setText("    This bill is overdue!");
        }
        else if ((dueDate > currentDate) && (currentDate >= dueDate - 259200000)) {
            alert.setImageResource(R.drawable.oran_alert);
            dueTag.setText("    This bill is due within 3 days");
        }
        else{
            alert.setImageResource(R.drawable.trans_alert);
            dueTag.setText(" ");
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_options, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            //goes to Edit_Reminder activity by passing the reminderArray and the current position of the chosen reminder
            case R.id.edit:
                remArr = loadFile(fileName);
                Bundle b = new Bundle(); //creates a bundle that serves as a placeholder for the data that will be sent to edit_reminder.java
                b.putInt("editThis", pos); //will hold the current position of the chosen reminder in the database
                b.putStringArray("reminderArray", remArr); //string version of the database
                Intent intent = new Intent(this, Edit_Reminder.class); //creates an intent that enables the binding of the two activities
                intent.putExtras(b); //add the data to the intent for later accessing of the new started activity
                this.startActivity(intent); //starts the edit_reminder activity
                finish();
                return true;
            //deletes the chosen reminder from the database and goes back to the main menu if deleted
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
                                reminderArray = StringToReminder(remArr);
                                reminderArray.remove(pos);
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
                                finish();
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
            //writes the reminder_paid object into the history.txt database and returns to main menu if tagged as paid
            case R.id.paid:
                DialogInterface.OnClickListener dialogClickListenerPaid=new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Log.v("getID",Integer.toString(pos));
                                Log.v("getID",Integer.toString(reminderArray.size()));
                                String newline = "\n";
                                String comma = ",";
                                SimpleDateFormat dateFormat =  new SimpleDateFormat("MM/dd/yyyy");
                                reminderArray = StringToReminder(remArr);
                                writeToPaid(reminderArray,pos); //updates the history.txt database
                                reminderArray.remove(pos); //removes the chosen reminder in the reminderArray
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
                                        fOut.write(Double.toString(reminderArray.get(i).getAmt()).getBytes()); //writes the reminder amount
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
                                finish();
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

