package com.example.prim.billding;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Arthur on 2/9/2017.
 */

public class ReminderService extends IntentService {
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
    //conversion sana ng date object to calendar object
    //for comparison of today and date in reminder database
    public static Calendar toCalendar(Date date){
        Calendar c=Calendar.getInstance();
        c.setTime(date);
        return c;
    }
    //populates a List<Integer> for overdue deadlines
    public List<Integer> ReminderDeadlineOver(ArrayList<Reminder> reminderArray){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long currentDate = c.getTimeInMillis();
        List<Integer> reminderNumber = new ArrayList<Integer>();
        for(int checkRem = 0; checkRem<reminderArray.size(); checkRem++){
            if(reminderArray.get(checkRem).getDate().getTime() < currentDate){
                reminderNumber.add(checkRem);
            }
        }
        return reminderNumber;
    }
    //function to populate a List<Integer> of the element number of reminderArray which are deadline today
    public List<Integer> ReminderDeadlineToday(ArrayList<Reminder> reminderArray){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long currentDate = c.getTimeInMillis();
        List<Integer> reminderNumber = new ArrayList<Integer>();
        for(int checkRem = 0; checkRem<reminderArray.size(); checkRem++){
            if(reminderArray.get(checkRem).getDate().getTime() == currentDate){
                reminderNumber.add(checkRem);
            }
        }
        return reminderNumber;
    }
    //populates a List<Integer> for upcoming deadlines
    public List<Integer> ReminderDeadlineSoon(ArrayList<Reminder> reminderArray){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long currentDate = c.getTimeInMillis();
        List<Integer> reminderNumber = new ArrayList<Integer>();
        for(int checkRem = 0; checkRem<reminderArray.size(); checkRem++){
            long threedaysago = reminderArray.get(checkRem).getDate().getTime() - 259200000;    //milliseconds in 3 days
            if(reminderArray.get(checkRem).getDate().getTime() > currentDate && currentDate >= threedaysago){
                reminderNumber.add(checkRem);
            }
        }
        return reminderNumber;
    }
    //reminder creation is now a function for cleaner code
    public void CreateNotif(int x, ArrayList<Reminder> remArr){
        String title = "";
        String texto = "";
        String thicc = "";
        List<Integer> type = null;

        //these will set the type list to be populated with the correct type of reminder we need
        //as well as strings that will appear for each reminder type
        if (x == 0) {
            type = ReminderDeadlineOver(remArr);
            title = "Bill-Ding!";
            texto = "You have overdue bills!";
            thicc = "Here are your overdue bills:";
        }
        else if (x == 1){
            type = ReminderDeadlineToday(remArr);
            title = "Bill-Ding!";
            texto = "You have bills due today!";
            thicc = "Here are your bills due today:";
        }
        else if (x == 2){
            type = ReminderDeadlineSoon(remArr);
            title = "Bill-Ding!";
            texto = "You have bills due within the next 3 days!";
            thicc = "Here are your bills due within 3 days:";
        }

        //if (type.size() == 0) { return; }  //ends the function prematurely if there aren't any reminders, so it won't make a notification
        if (type.size() > 0) {
            NotificationCompat.InboxStyle expand =
                    new NotificationCompat.InboxStyle();
            expand.setBigContentTitle(title);       //title for expanded view
            expand.addLine(thicc);                  //first line of expanded view text
            //for loop for expanded view items
            for (int i = 0; i < type.size(); i++) {
                expand.addLine("> " + remArr.get(type.get(i)).getName());
            }

            //this is what builds the notification
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.billding_ico)    //set the small icon
                            .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.billding_ico))   //set the large icon
                            .setContentTitle(title)    //set title of the notification
                            .setContentText(texto)     //set the contents
                            .setAutoCancel(true)       //destroys the notification when clicked
                            .setStyle(expand);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, x, notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(contentIntent);

            // Add a notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(x, builder.build());
        }
    }

    public ReminderService(){
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String fileName = "reminders.txt";
        //remArr will hold the data read from reminders.db
        final String[] remArr = loadFile(fileName);
        ArrayList<Reminder> reminderArray = new ArrayList<Reminder>();
        if (remArr.length > 0) {
            reminderArray = StringToReminder(remArr);
            //0 is for overdue bills, 1 is for today bills, 2 is for bills within 3 days
            CreateNotif(0, reminderArray);
            CreateNotif(1, reminderArray);
            CreateNotif(2, reminderArray);
        }
          /*NotificationCompat.Builder builder =
                  new NotificationCompat.Builder(this)
                          .setSmallIcon(R.mipmap.ic_launcher)
                          .setContentTitle("Bill Reminder")
                          .setContentText("You have a bill due today!");

          Intent notificationIntent = new Intent(this, MainActivity.class);
          PendingIntent contentIntent = PendingIntent.getActivity(this, counter, notificationIntent,
                  PendingIntent.FLAG_UPDATE_CURRENT);
          builder.setContentIntent(contentIntent);

          // Add a notification
          NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
          manager.notify(counter, builder.build());*/
    }

}