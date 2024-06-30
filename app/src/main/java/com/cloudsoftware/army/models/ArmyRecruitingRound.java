package com.cloudsoftware.army.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class ArmyRecruitingRound implements Parcelable {
    private String id;
    private String roundName;
    private String startDate;
    private String endDate;
    private List<String> candidatesId;
    private List<String> selectedCandidatesId;
    private String location;

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    // Default constructor required for calls to DataSnapshot.getValue(ArmyRecruitingRound.class)
    public ArmyRecruitingRound() {}

    public ArmyRecruitingRound(String id, String roundName, String startDate,
                               String endDate, String location) {
        this.id = id;
        this.roundName = roundName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.candidatesId = new ArrayList<>();
        this.selectedCandidatesId = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getCandidatesId() {
        return candidatesId;
    }

    public void setCandidatesId(List<String> candidatesId) {
        this.candidatesId = candidatesId;
    }

    public List<String> getSelectedCandidatesId() {
        return selectedCandidatesId;
    }

    public void setSelectedCandidatesId(List<String> selectedCandidatesId) {
        this.selectedCandidatesId = selectedCandidatesId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean hasNotEnded() {
        try {
            Date end = sdf.parse(this.endDate);
            Date current = new Date();
            return current.before(end);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Parcelable implementation
    protected ArmyRecruitingRound(Parcel in) {
        id = in.readString();
        roundName = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        candidatesId = in.createStringArrayList();
        selectedCandidatesId = in.createStringArrayList();
        location = in.readString();
    }

    public static final Creator<ArmyRecruitingRound> CREATOR = new Creator<ArmyRecruitingRound>() {
        @Override
        public ArmyRecruitingRound createFromParcel(Parcel in) {
            return new ArmyRecruitingRound(in);
        }

        @Override
        public ArmyRecruitingRound[] newArray(int size) {
            return new ArmyRecruitingRound[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(roundName);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeStringList(candidatesId);
        dest.writeStringList(selectedCandidatesId);
        dest.writeString(location);
    }
}
