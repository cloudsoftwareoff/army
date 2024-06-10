package com.cloudsoftware.army.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Citizen implements Parcelable {
    private String cin;
    private String firstName;
    private String lastName;
    private String birthdate;
    private String gender;

    //"Eligible", "Court", "Warrant", "Studying"

    private String status;

    public Citizen() {}

    public Citizen(String cin, String firstName, String lastName, String birthdate, String gender,String status) {
        this.cin = cin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.gender = gender;
        this.status=status;
    }
    protected Citizen(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        cin = in.readString();
        birthdate = in.readString();
        gender = in.readString();
        status=in.readString();
    }

    public static final Creator<Citizen> CREATOR = new Creator<Citizen>() {
        @Override
        public Citizen createFromParcel(Parcel in) {
            return new Citizen(in);
        }

        @Override
        public Citizen[] newArray(int size) {
            return new Citizen[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(cin);
        dest.writeString(birthdate);
        dest.writeString(gender);
        dest.writeString(status);
    }
    // Getters and setters
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }
    public  String getStatus(){return  status;}
    public  void setStatus(String status){ this.status=status;}
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public boolean isEighteenOrOlder() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date birthDate = sdf.parse(this.birthdate);
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTime(birthDate);
            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

            // If birth date is greater than today's date (after 18 years), then subtract one year from the age
            if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age >= 18;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
