package com.cloudsoftware.army;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudsoftware.army.models.Citizen;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CitizenJoin extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_citizen_join);
        EditText cin_number=findViewById(R.id.cin_edit);
        EditText birthDate=findViewById(R.id.birth_edit);
        Button verify_data=findViewById(R.id.verify_data);

        db = FirebaseFirestore.getInstance();


        verify_data.setOnClickListener(event->{
         String    _cin=cin_number.getText().toString();
         String _birth=birthDate.getText().toString();
         if(_cin.equals("") || _cin.length() !=8){
             cin_number.setError("Invalid cin number");
             return;
         }
         if(_birth.equals("") || !_birth.contains("/")){
             birthDate.setError("Invalid birthdate");
             return;
         }
fetchUserData(_cin,_birth);



        });
    }
    private void fetchUserData(String cin, String birthDate) {
        db.collection("citizens").document(cin).get().addOnCompleteListener(task -> {
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
                            showPopup("Invalid Birthdate", "The birthdate does not match our records.");
                        }
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
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}