package com.cloudsoftware.army;

public class Citizen {
    private String cin;
    private String firstName;
    private String lastName;
    private String birthdate;
    private String gender;

    public Citizen() {}

    public Citizen(String cin, String firstName, String lastName, String birthdate, String gender) {
        this.cin = cin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.gender = gender;
    }

    // Getters and setters
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
