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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/*
    This is a class that enables the user to add a reminder to the database
     It has its own xml file in which the user can input data in and when the save
     button is pressed, the data is then gathered and save to the database.
*/
public class View_ReminderPaid extends AppCompatActivity {
     public String[] loadFile(String fileName) {
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
               reminderStrings = loadFile(fileName);
          }
          return reminderStrings;
     }
     //parses history.txt and passes the returns the read data in the form of string array
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
     public ArrayList<ReminderPaid> StringToReminder(String[] dbStrings) {
          ArrayList<ReminderPaid> reminderList = new ArrayList<ReminderPaid>();
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
                 4 -> date when reminder was tagged as paid
              */
          //the created temporary Reminder object is now appended to the ArrayList
          //"comma0" is brought back as "comma" and "comma1" into an actual comma
          for (int i = 0; i < numRems; i++) {
               String[] choppedRem = dbStrings[i].split(",");
               choppedRem[0] = choppedRem[0].replace("comma0", "comma");
               choppedRem[0] = choppedRem[0].replace("comma1", ",");
               choppedRem[1] = choppedRem[1].replace("comma0", "comma");
               choppedRem[1] = choppedRem[1].replace("comma1", ",");

               ReminderPaid tempRem = new ReminderPaid(choppedRem[0]);
               tempRem.setDesc(choppedRem[1]);
               tempRem.setAmt(Double.parseDouble(choppedRem[2]));
               tempRem.setDate(choppedRem[3]);
               tempRem.setPaidDate(choppedRem[4]);
               reminderList.add(tempRem);
          }
          return reminderList;
     }

     //Declaration of variables used
     TextView nameField, amountField, dateField, descriptionField,datePaidField; //fields for the user to input data in

     ArrayList<ReminderPaid> reminderArray = new ArrayList<ReminderPaid>();
     String fileName = "history.txt";
     ListView listView;
     ReminderListAdapter adapter;
     String [] remArr;
     int pos;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_view_paid_reminder);
     }
     public void onResume() {
          super.onResume();
          setContentView(R.layout.activity_view_paid_reminder);


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
          datePaidField = (TextView) findViewById(R.id.editText4); //reminder paid date
          datePaidField.setText(dataReminder[4]);
          datePaidField.setClickable(false);
          descriptionField = (TextView) findViewById(R.id.editText5); //reminder description
          descriptionField.setText(dataReminder[1]);
          //descriptionField.setClickable(false);
          descriptionField.setMovementMethod(new ScrollingMovementMethod());


     }
}

