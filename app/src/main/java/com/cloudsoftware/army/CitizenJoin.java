package com.cloudsoftware.army;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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




        });
    }
    private void fetchUserData(String cin) {
        db.collection("citizens").document(cin).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Citizen citizen = document.toObject(Citizen.class);
                    if (citizen != null) {
                        Toast.makeText(this, "Citizen Found: " + citizen.getFirstName(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "No such citizen found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}