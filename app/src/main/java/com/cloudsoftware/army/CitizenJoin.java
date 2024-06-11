package com.cloudsoftware.army;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudsoftware.army.models.Citizen;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CitizenJoin extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText birthDate;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_citizen_join);

        EditText cin_number = findViewById(R.id.cin_edit);
        birthDate = findViewById(R.id.birth_edit);
        Button verify_data = findViewById(R.id.verify_data);

        db = FirebaseFirestore.getInstance();

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying...");
        progressDialog.setCancelable(false);

        birthDate.setOnClickListener(v -> showDatePickerDialog());

        verify_data.setOnClickListener(event -> {
            String _cin = cin_number.getText().toString();
            String _birth = birthDate.getText().toString();

            if (_cin.equals("") || _cin.length() != 8) {
                cin_number.setError(getString(R.string.invalid_cin_number));
                return;
            }

            if (_birth.equals("") || !_birth.contains("/")) {
                birthDate.setError(getString(R.string.invalid_birthdate));
                return;
            }

            // Show the progress dialog before starting verification
            progressDialog.show();
            fetchUserData(_cin, _birth);
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel(calendar);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateLabel(Calendar calendar) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        birthDate.setText(sdf.format(calendar.getTime()));
    }

    private void fetchUserData(String cin, String birthDate) {
        db.collection("citizens").document(cin).get().addOnCompleteListener(task -> {
            // Dismiss the progress dialog once the task is complete
            progressDialog.dismiss();

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Citizen citizen = document.toObject(Citizen.class);
                    if (citizen != null) {
                        if (citizen.getBirthdate().equals(birthDate)) {
                            Intent intent = new Intent(CitizenJoin.this, UserProfileActivity.class);
                            intent.putExtra("CITIZEN", citizen);
                            startActivity(intent);
                        } else {
                            showPopup(getString(R.string.invalid_birthdate), getString(R.string.the_birthdate_does_not_match_our_records));
                        }
                    }
                } else {
                    showPopup(getString(R.string.no_citizen_found), getString(R.string.no_such_citizen_found));
                }
            } else {
                showPopup("Error", "Error fetching data.");
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
