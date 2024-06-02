package com.cloudsoftware.army;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {
    public static String calculateAge(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // To strictly parse the date format
        try {
            Date birthDate = sdf.parse(date);
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTime(birthDate);
            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

            // If birth date is greater than today's date (after 18 years), then subtract one year from the age
            if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return String.valueOf(age);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date format";
        }
    }
}