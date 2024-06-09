package com.cloudsoftware.army;

import java.util.Date;
import java.util.List;

public class Submission {
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
