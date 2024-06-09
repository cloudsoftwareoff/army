package com.cloudsoftware.army;



import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";
    private FirebaseFirestore db;
    private Context context;

    public FirestoreHelper(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
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

    public void checkEligibilityAndNotify() {
        db.collection("citizens").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Citizen citizen = document.toObject(Citizen.class);
                    if (citizen != null) {
                        int age = Integer.parseInt(Utility.calculateAge(citizen.getBirthdate()));
                        if (age == 18) {
                            updateUserStatus(citizen.getCin(), "Eligible", 0);
                            sendNotification(citizen, "You are eligible to join the army. Please report to the nearest recruitment center.");
                        } else if (age > 18) {
                            // Fetch user status and update accordingly
                            fetchUserStatus(citizen.getCin(), status -> {
                                if (status != null) {
                                    int notifCount = status.getNotifCount();
                                    if (notifCount == 0) {
                                        updateUserStatus(citizen.getCin(), "Court", notifCount + 1);
                                        sendNotification(citizen, "You need to report to the court.");
                                    } else if (notifCount == 1) {
                                        updateUserStatus(citizen.getCin(), "Warrant", notifCount + 1);
                                        sendNotification(citizen, "A warrant has been issued for your arrest.");
                                    }
                                }
                            });
                        }
                    }
                }
            } else {
                Log.e(TAG, "Error fetching citizens: ", task.getException());
            }
        });
    }

    private void fetchUserStatus(String userId, StatusCallback callback) {
        db.collection("user_statuses").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    UserStatus status = document.toObject(UserStatus.class);
                    callback.onCallback(status);
                } else {
                    callback.onCallback(null);
                }
            } else {
                Log.e(TAG, "Error fetching user status: ", task.getException());
                callback.onCallback(null);
            }
        });
    }

    private void updateUserStatus(String userId, String status, int notifCount) {
        UserStatus userStatus = new UserStatus(userId, status, notifCount);
        db.collection("user_statuses").document(userId).set(userStatus)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User status updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating user status: ", e));
    }

    private void sendNotification(Citizen citizen, String message) {
        // Implement push notification logic here using FCM or local notifications
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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

