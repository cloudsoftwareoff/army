package com.cloudsoftware.army;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView nameView, cinView, birthdateView, genderView, notificationsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = FirebaseFirestore.getInstance();

        nameView = findViewById(R.id.name);
        cinView = findViewById(R.id.cin);
        birthdateView = findViewById(R.id.birthdate);
        genderView = findViewById(R.id.gender);
        notificationsView = findViewById(R.id.notifications);

        Citizen citizen = getIntent().getParcelableExtra("CITIZEN");
        if (citizen != null) {
            displayUserData(citizen);
           // fetchNotifications(citizen.getCin());
        }
    }

    private void displayUserData(Citizen citizen) {
        nameView.setText("Name: " + citizen.getFirstName() + " " + citizen.getLastName());
        cinView.setText("CIN: " + citizen.getCin());
        birthdateView.setText("Birthdate: " + citizen.getBirthdate());
        genderView.setText("Gender: " + citizen.getGender());
    }

    private void fetchNotifications(String cin) {
        db.collection("notifications").whereEqualTo("cin", cin).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StringBuilder notifications = new StringBuilder();
                for (DocumentSnapshot document : task.getResult()) {
                    String notification = document.getString("message");
                    notifications.append(notification).append("\n\n");
                }
                notificationsView.setText(notifications.toString());
            } else {
                showPopup("Error", "Error fetching notifications.");
            }
        });
    }

    private void showPopup(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
