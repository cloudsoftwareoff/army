package com.cloudsoftware.army.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class Submission implements  Parcelable{
    private String submissionId;
    private String userId;
    private List<String> documentUrls;
    private Date submissionDate;
    private String submissionType;
    private String note;
    private String status;

    // Required default constructor for Firestore serialization/deserialization
    public Submission() {}

    public Submission(String submissionId, String userId, List<String> documentUrls, Date submissionDate, String submissionType, String note, String status) {
        this.submissionId = submissionId;
        this.userId = userId;
        this.documentUrls = documentUrls;
        this.submissionDate = submissionDate;
        this.submissionType = submissionType;
        this.note = note;
        this.status = status;
    }
    protected Submission(Parcel in) {
        submissionId = in.readString();
        userId = in.readString();
        documentUrls = in.createStringArrayList();
        submissionType = in.readString();
        note = in.readString();
        status = in.readString();
        submissionDate = new Date(in.readLong());
    }

    public static final Parcelable.Creator<Submission> CREATOR = new Parcelable.Creator<Submission>() {
        @Override
        public Submission createFromParcel(Parcel in) {
            return new Submission(in);
        }

        @Override
        public Submission[] newArray(int size) {
            return new Submission[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(submissionId);
        dest.writeString(userId);
        dest.writeStringList(documentUrls);
        dest.writeString(submissionType);
        dest.writeString(note);
        dest.writeString(status);
        dest.writeLong(submissionDate.getTime());
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getDocumentUrls() {
        return documentUrls;
    }

    public void setDocumentUrls(List<String> documentUrls) {
        this.documentUrls = documentUrls;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
