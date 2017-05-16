package com.example.prim.billding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Desktop on 3/16/2017.
 */

public class ReminderHistory extends AppCompatActivity {
     //function for loading the file
     //checks if history.txt exists
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
     //converts read string array read from data into a list of ReminderPaid objects
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
               Date today = new Date();
               if(today.getTime()-tempRem.getPaidDate().getTime() < TimeUnit.DAYS.toMillis(30)){
                    reminderList.add(tempRem);
               }
          }
          return reminderList;
     }
     //updates the history.txt database whenever something is changed
     public void updateFile(String filename,ArrayList<ReminderPaid> reminders){
          try{
               DecimalFormat format = new DecimalFormat("0.00");
               FileOutputStream fOut = openFileOutput(filename, Context.MODE_PRIVATE); //opens the database file in write mode
               fOut.write(Integer.toString(reminders.size()).getBytes());
               fOut.write("\n".getBytes());
               for(int i=0;i<reminders.size();i++){
                    String toWrite="";
                    String remName = reminders.get(i).getName().replace("comma", "comma0");
                    String remDesc = reminders.get(i).getDesc().replace("comma", "comma0");
                    remName = remName.replace(",", "comma1");
                    remDesc = remDesc.replace(",", "comma1");
                    toWrite = remName+","+remDesc+","+format.format(reminders.get(i).getAmt())+",";
                    toWrite += new SimpleDateFormat("MM/dd/yyyy").format(reminders.get(i).getDate())+",";
                    toWrite += new SimpleDateFormat("MM/dd/yyyy").format(reminders.get(i).getPaidDate());
                    fOut.write(toWrite.getBytes());
                    fOut.write("\n".getBytes());
               }
               fOut.close();
          }catch(Exception e){
               e.printStackTrace();
          }
     }
     String[] reminderPaid; //String array to hold the parsed history.txt database
     String filename = "history.txt"; //filename of the history database
     ArrayList<ReminderPaid> reminderArray; //ArrayList of type ReminderPaid to hold the contents of history.txt for writing and showing through the adaptor
     ReminderPaidListAdapter adapter; //adapter to show the reminderArray
     ListView listView; //declaration of listview
     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_view_log);
          reminderPaid = loadFile(filename); //loads the history.txt database and anchors it to reminderPaid string array
          if (reminderPaid.length == 0) {
               //String [] emptyDB = {"There are no reminders to show"};
               TextView textView = (TextView) findViewById(R.id.error_emptyRem);
               textView.setText("There are no reminders to show");
          } else {
               //remArr is now converted to List of ReminderPaid objects
               reminderArray = StringToReminder(reminderPaid);
               updateFile(filename,reminderArray);
               adapter = new ReminderPaidListAdapter(this, reminderArray);
               listView = (ListView) findViewById(R.id.reminder_history);
               listView.setAdapter(adapter);
               registerForContextMenu(listView);
               //item click listener to send the user to View_ReminderPaid activity whenever a ReminderPaid is pressed
               listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                         reminderPaid = loadFile(filename);
                         Bundle b = new Bundle(); //creates a bundle that serves as a placeholder for the data that will be sent to View_ReminderPaid.java
                         b.putInt("position", position); //places "position" in bundle which serves as an identifier for View_ReminderPaid.java and position which contains the position of the chosen reminderpaid object
                         b.putString("key", reminderPaid[position]); //places "key" in bundle which serves as an identifier for View_ReminderPaid.java and remArr which contains the data to be passed
                         Intent intent = new Intent(view.getContext(), View_ReminderPaid.class); //creates an intent that enables the binding of the two activities
                         intent.putExtras(b); //add the data to the intent for later accessing of the new started activity
                         startActivity(intent); //starts the View_ReminderPaid activity
                    }
               });
          }
     }
}
