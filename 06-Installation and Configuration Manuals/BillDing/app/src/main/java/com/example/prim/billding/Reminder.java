package com.example.prim.billding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Desktop on 2/16/2017.
 */

//Class for Reminder object
public class Reminder {
    private String name;
    private String description = "";
    private double amountDue;
    private Date dateDue;

    //Reminder instantiation, will set the input as the name of the object
    public Reminder(String name) {
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
}

