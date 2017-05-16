package com.example.prim.billding;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.prim.billding.R.id.editText5;
import static com.example.prim.billding.R.id.editText6;

/**
 * Created by Desktop on 3/22/2017.
 */

//class to implement the spinner for choosing a time when the application will alarm
public class SpinnerView extends Activity implements AdapterView.OnItemSelectedListener {
     //reads the settings.txt database file to get the currently used settings
     //will return an integer array with int[0] as hours and int[1] as minutes
     public int[] readFile(String filename){
          int[] returnList = {0,0};
          try {
               int hour;
               int min;
               FileInputStream fin = openFileInput(filename);
               int c;
               String temp="";
               while( (c = fin.read()) != -1){
                    temp = temp + Character.toString((char)c);
               }
               String[] splitMe = temp.split(",");
               returnList[0] = Integer.parseInt(splitMe[0]);
               returnList[1] = Integer.parseInt(splitMe[1]);
          }
          catch(Exception e){
               e.printStackTrace();
          }
          return returnList;
     }
     String filename = "settings.txt"; //database filename where the time settings is saved
     int[] newTime = {0,0}; //declaration of the value that will hold the new time settings
     Spinner hours,mins; //declaration of the Spinners for hours and mins
     Button saveButton,cancelButton; //declaration of buttons
     @Override
     public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.spinner_layout);
          saveButton = (Button) findViewById(R.id.save); //save button
          cancelButton = (Button) findViewById(R.id.cancel); //cancel button
          int[] origSet = readFile(filename); //gets the original value of the settings database
          newTime = origSet; //passes the value of the origSet to the newTime

          hours = (Spinner) findViewById(R.id.hour);
          mins = (Spinner) findViewById(R.id.mins);

          // Spinner click listener
          hours.setOnItemSelectedListener(this);

          // Spinner Drop down elements
          //hour choices is 0-23 (military time)
          List<String> hourChoices = new ArrayList<String>();
          for(int i=0;i<24;i++){
               if(i < 10){
                    hourChoices.add("0"+Integer.toString(i));
               }
               else {
                    hourChoices.add(Integer.toString(i));
               }
          }
          // Creating adapter for spinner
          ArrayAdapter<String> hoursAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hourChoices);

          // Drop down layout style - list view with radio button
          hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          // attaching data adapter to spinner
          hours.setAdapter(hoursAdapter);
          hours.setSelection(origSet[0]);



          // Spinner click listener
          mins.setOnItemSelectedListener(this);

          // Spinner Drop down elements
          //minsChoices is 0-59
          List<String> minsChoices = new ArrayList<String>();
          for(int i=0;i<60;i++){
               if(i < 10){
                    minsChoices.add("0"+Integer.toString(i));
               }
               else {
                    minsChoices.add(Integer.toString(i));
               }
          }
          // Creating adapter for spinner
          ArrayAdapter<String> minsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, minsChoices);

          // Drop down layout style - list view with radio button
          minsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          // attaching data adapter to spinner
          mins.setAdapter(minsAdapter);
          mins.setSelection(origSet[1]);

          //called whenever the saveButton is pressed
          saveButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    String newHours = hours.getSelectedItem().toString();
                    String newMins = mins.getSelectedItem().toString();
                    try{
                         FileOutputStream fOut = openFileOutput(filename, Context.MODE_PRIVATE); //opens the database file in write mode
                         fOut.write(newHours.getBytes());
                         fOut.write(",".getBytes());
                         fOut.write(newMins.getBytes());
                         fOut.close();
                    }catch(Exception e){
                         e.printStackTrace();
                    }
                    Toast.makeText(getBaseContext(), "Settings Updated", Toast.LENGTH_SHORT).show();
                    finish();
               }
          });
          cancelButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    Toast.makeText(getBaseContext(), "Settings unchanged", Toast.LENGTH_SHORT).show();
                    finish();
               }
          });
     }

     @Override
     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          // On selecting a spinner item
          String item = parent.getItemAtPosition(position).toString();

          // Showing selected spinner item

     }
     public void onNothingSelected(AdapterView<?> arg0) {
          // TODO Auto-generated method stub
     }

}
