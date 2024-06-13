package com.cloudsoftware.army.db;



import android.content.Context;
import android.widget.Toast;

import com.cloudsoftware.army.models.Citizen;
import com.cloudsoftware.army.models.Submission;
import com.cloudsoftware.army.models.UserStatus;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";
    private FirebaseFirestore db;
    private Context context;
    private List<Submission> submissions;
    public FirestoreHelper(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }
    public void addSubmission(Submission submission) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new document reference to get the generated ID
        DocumentReference newSubmissionRef = db.collection("submissions").document();
        String submissionId = newSubmissionRef.getId();
        submission.setSubmissionId(submissionId);
        newSubmissionRef.set(submission)
                .addOnSuccessListener(aVoid -> System.out.println("Submission added with ID: " + submissionId))
                .addOnFailureListener(e -> System.err.println("Error adding submission: " + e.getMessage()));
    }
    public void fetchUserData(String cin, String birthDate, FirestoreCallback callback) {
        db.collection("citizens").document(cin).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Citizen citizen = document.toObject(Citizen.class);
                    if (citizen != null && citizen.getBirthdate().equals(birthDate)) {
                        callback.onCallback(citizen);
                    } else {
                        showPopup("Invalid Birthdate", "The birthdate does not match our records.");
                    }
                } else {
                    showPopup("No Citizen Found", "No such citizen found.");
                }
            } else {
                showPopup("Error", "Error fetching data.");
            }
        });
    }


    private void showPopup(String title, String message) {
        // Implement popup logic here
        Toast.makeText(context, title + ": " + message, Toast.LENGTH_LONG).show();
    }

    public interface FirestoreCallback {
        void onCallback(Citizen citizen);
    }

    public interface StatusCallback {
        void onCallback(UserStatus status);
    }
}

