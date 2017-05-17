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
    VERSION 1.0

    {version number}  <author>  |date (format of MM/dd/yyyy)|
        [notes]
*/

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.lang.String;
import java.lang.*;

/*
    This is a class that enables the user to edit a chosen reminder in the database
     It has its own xml file in which the user can input data in and when the save
     button is pressed, the data is then gathered and save to the database.

     The user can get to here from MainActivity or ViewReminder classes
*/
public class Edit_Reminder extends AppCompatActivity {
    //Declaration of variables used

    public void fileWriter(ArrayList<String> array, String name, String amt, String desc, String date,int removeThis){
        int size = array.size();
        int i;
        int added = 0;
        ArrayList<String> newList = new ArrayList<String>();
        for(i=0;i<size;i++){
            newList.add(array.get(i));
        }
        String toAdd = name + "," + desc + "," + amt + "," + date;
        if(size==0){
            newList.add(toAdd);
        }
        else{
            for(i=0;i<newList.size();i++){
                String temp1 = newList.get(i);
                String[] parts1 = temp1.split(",");
                try{
                    Date date1 = new SimpleDateFormat("MM/dd/yyyy").parse(parts1[3]);
                    Date date2 = new SimpleDateFormat("MM/dd/yyyy").parse(date);
                    if(date1.getTime() > date2.getTime()){
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
    Button saveButton, cancelButton; //buttons for saving to the database and for returning to the main menu
    EditText nameField, amountField, dateField, descriptionField; //fields for the user to input data in

    TextView tv; //displays the * = required fields text for users to know what is required to be filled out

    String billName; //contains the name of the reminder to be added
    String billAmount; //contains the amount payable of the reminder to be added
    String billDate; //contains the date of the reminder to be added
    String billDescription; //contains the description of the reminder to be added
    private String comma = ","; //string which contains , used for file writing
    private String newline = "\n"; //string which contain a newline used for file writing
    private String file = "reminders.txt"; //string which contains the file name of the database

    Calendar myCalendar = Calendar.getInstance(); //creates a calendar instrance that enables the user to choose a date for their reminder


     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
          if (item.getItemId() == android.R.id.home) {
               onBackPressed();
               return true;
          }
          return false;
     }

    @Override
    public void onBackPressed(){
        DialogInterface.OnClickListener dialogClickListenerDelete=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getBaseContext(), "Editing Cancelled", Toast.LENGTH_SHORT).show(); //feedback for cancelling the add
                        finish(); //returns user to the main menu
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };
        android.app.AlertDialog.Builder confirmCancel = new android.app.AlertDialog.Builder(this);
        confirmCancel.setMessage("Are you sure you want to cancel editing?").setPositiveButton("Yes",dialogClickListenerDelete).setNegativeButton("No",dialogClickListenerDelete).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);

        //findViewById(R.id.activity_add__reminder).setOnTouchListener(this);

        //identification of xml elements

        Bundle b = this.getIntent().getExtras(); //creates a bundle to store the data given by MainActivity
        final String[] array = b.getStringArray("reminderArray"); //creates a String array to contain the data found in b
        final int editThis = b.getInt("editThis"); //creates an int variable to store the index of the reminder being edited
        final int currentSize = array.length; //creates a placeholder for the current number of reminders in the database
        final String toWriteSize = Integer.toString(currentSize); //converts the placeholder of the current number of reminders to a string for file writing purposes

        String[] dataReminder = array[editThis].split(",");
        dataReminder[0] = dataReminder[0].replace("comma0", "comma");
        dataReminder[0] = dataReminder[0].replace("comma1", ",");
        dataReminder[1] = dataReminder[1].replace("comma0", "comma");
        dataReminder[1] = dataReminder[1].replace("comma1", ",");

        saveButton = (Button) findViewById(R.id.button); //save button
        cancelButton = (Button) findViewById(R.id.button2); //cancel button
        nameField = (EditText) findViewById(R.id.billname); //reminder name
        nameField.setText(dataReminder[0]);
        amountField = (EditText) findViewById(R.id.billamount); //reminder amount
        amountField.setText(dataReminder[2]);
        dateField = (EditText) findViewById(R.id.billdue); //reminder due date
        dateField.setText(dataReminder[3]);
        descriptionField = (EditText) findViewById(R.id.billdesc); //reminder description
        descriptionField.setText(dataReminder[1]);
        tv = (TextView) findViewById(R.id.req); //* = required field

        /*
         Function that enables saving to the database. When the save button is pressed, data is gotten from each of the fields. Data is checked and an error
         message is given when there is something wrong with the data like when a field is empty. Once data checking is done, file writing is attempted. The
         current number of reminders is written first followed by the removal of the reminder with index editThis then writes all of the current reminders and finally 
         the newly added reminder. Once the reminders are successfully saved, the user is return to the main menu.
        */
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                billName = nameField.getText().toString();  //gets the content of the reminder name field and converts it to a string
                billDate = dateField.getText().toString(); //gets the content of the reminder due date field and converts it to a string
                billDescription = descriptionField.getText().toString(); //gets the content of the reminder description field and converts it to a string
                billAmount = amountField.getText().toString(); //gets the content of the reminder amount field and converts it to a string

                /*
                Check if any of the required fields are empty. If one of the fields are empty then we cannot proceed to file writing and an error
                message is displayed telling the user to fill out all of the required fields.
                 */
                if (TextUtils.isEmpty(billName) || TextUtils.isEmpty(billDate) || TextUtils.isEmpty(billAmount)) {
                    Toast.makeText(getBaseContext(), "Please fill out all of the required fields", Toast.LENGTH_SHORT).show(); //feedback for incomplete fields
                } else {
                    String newBillName = billName.trim(); //creates a placeholder to be used for file writing that holds the reminder name with all of its leading and trailing whitespaces removed
                    String newBillDescription = billDescription.trim(); //creates a placeholder to be used for file writing that holds the reminder description with all of its leading and trailing whitespaces removed
                    String formattedTemp;
                    String period = ".";
                    if(billAmount.compareTo(period) == 0){
                        formattedTemp = "0.00";
                    }
                    else{
                        double temp = Double.parseDouble(billAmount); //creates a placeholder for the reminder amount, the string gotten from the user is converted to a double
                        DecimalFormat format = new DecimalFormat("0.00");
                        formattedTemp = format.format(temp); //creates a placeholder for file writing purposes that formats the double to only be 2 decimal places
                    }

                    //this section deals with commas
                    //if the word "comma" exists in the string, it's replaced with "comma0"
                    //if an actual comma is seen, it's replaced with "comma1"
                    //they will later be dealt with when the file is read
                    newBillName = newBillName.replace("comma", "comma0");
                    newBillDescription = newBillDescription.replace("comma", "comma0");
                    newBillName = newBillName.replace(",", "comma1");
                    newBillDescription = newBillDescription.replace(",", "comma1");
                    ArrayList<String> newArray = new ArrayList<String>(Arrays.asList(array));
                    newArray.remove(editThis); //removes the *original* version of the reminder being edited
                    fileWriter(newArray,newBillName,formattedTemp,newBillDescription,billDate,editThis);
                    Toast.makeText(getBaseContext(), "Reminder Updated", Toast.LENGTH_SHORT).show(); //feedback for successfully adding to the database file

                    finish(); //returns user to the main menu
                }
            }
        });
        /*
        Button for when the user decides to not add a reminder. When clicked, returns to the main menu.
         */
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListenerDelete=new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Toast.makeText(getBaseContext(), "Editing Cancelled", Toast.LENGTH_SHORT).show(); //feedback for cancelling the add
                                finish(); //returns user to the main menu
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder confirmCancel = new AlertDialog.Builder(v.getContext());
                confirmCancel.setMessage("Are you sure you want to cancel?").setPositiveButton("Yes",dialogClickListenerDelete).setNegativeButton("No",dialogClickListenerDelete).show();
            }
        });

        /*
        Creates a date picker which allows the user to set the reminder due date via a widget instead of typing it
         */

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year); //sets the year by looking at the created calendar instance
                myCalendar.set(Calendar.MONTH, monthOfYear); //sets the month by looking at the created calender instance
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth); //sets the day by looking at the created calender instance
                updateLabel(); //updates the label of the date picker to reflect current date
            }
            /*
            Function for updating the date picker
             */
            private void updateLabel() {
                String myFormat = "MM/dd/yyyy"; //specifies a specific format to follow
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US); //creates a new date format given the format to follow
                Calendar c2 = Calendar.getInstance();
                c2.set(Calendar.HOUR_OF_DAY,0);
                c2.set(Calendar.MINUTE,0);
                c2.set(Calendar.SECOND,0);
                c2.add(Calendar.DATE,1);
                if(myCalendar.getTimeInMillis() < c2.getTimeInMillis()){
                    Toast.makeText(getBaseContext(), "You cannot choose past dates", Toast.LENGTH_SHORT).show(); //feedback
                    DatePickerDialog temp = new DatePickerDialog(Edit_Reminder.this, this, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)); //creates a date picker dialog
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY,0);
                    c.set(Calendar.MINUTE,0);
                    c.set(Calendar.SECOND,0);
                    c.add(Calendar.DATE,1);
                    temp.getDatePicker().setMinDate(c.getTimeInMillis()); //sets the minimum date that the user can choose so that past dates cannot be chosen
                    temp.show(); //shows the created date picker dialog
                }
                else{
                    dateField.setText(sdf.format(myCalendar.getTime())); //sets the date to display on the date picker
                }
            }

        };
        /*
        Function that enables the user to pick a date from the date picker by clicking on the date field
         */
        dateField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog temp = new DatePickerDialog(Edit_Reminder.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)); //creates a date picker dialog
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY,0);
                c.set(Calendar.MINUTE,0);
                c.set(Calendar.SECOND,0);
                c.add(Calendar.DATE,1);
                temp.getDatePicker().setMinDate(c.getTimeInMillis()); //sets the minimum date that the user can choose so that past dates cannot be chosen
                temp.show(); //shows the createde date picker dialog
            }
        });

    }
    /*public static void hideKeyboard(Activity activity){

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
    }*/
}

