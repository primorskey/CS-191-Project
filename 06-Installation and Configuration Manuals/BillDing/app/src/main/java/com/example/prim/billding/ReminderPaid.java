package com.example.prim.billding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Desktop on 3/16/2017.
 */

public class ReminderPaid {
     private String name;
     private String description = "";
     private double amountDue;
     private Date dateDue;
     private Date datePaid;

     //ReminderPaid instantiation, will set the input as the name of the object
     public ReminderPaid(String name) {
          this.name = name;
     }

     //Converts string input to Date format
     public Date stringToDate(String dateString) {
          SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
          Date convertedDate;
          try {
               convertedDate = format.parse(dateString);
          } catch (ParseException e) {
               System.out.println("Unparseable using " + format);
               convertedDate = new Date();
          }
          return convertedDate;
     }
     //sets input as reminder name
     public void setName(String nme) {
          name = nme;
     }

     //sets input as reminder description
     public void setDesc(String desc) {
          description = desc;
     }

     //sets input as reminder amount due
     public void setAmt(double amtDue) {
          amountDue = amtDue;
     }

     //sets input string as reminder due date, calls stringToDate function to convert the string to date
     public void setDate(String date) {
          dateDue = stringToDate(date);
     }

     //sets input string as reminder date paid, calls stringToDate function to convert the string to date
     public void setPaidDate(String date) {
          datePaid = stringToDate(date);
     }

     //returns name of the reminder
     public String getName() {
          return name;
     }

     //returns the description of the reminder
     public String getDesc() {
          return description;
     }

     //returns the amount due of the reminder
     public double getAmt() {
          return amountDue;
     }

     //returns the date due of the reminder
     public Date getDate() {
          return dateDue;
     }

     //returns the date paid of the reminder
     public Date getPaidDate() {
          return datePaid;
     }
}
