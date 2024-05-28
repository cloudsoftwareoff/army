package com.cloudsoftware.army;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CitizenDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_detail);

        TextView detailTextView = findViewById(R.id.detailTextView);

        String cin = getIntent().getStringExtra("CITIZEN_CIN");
        String name = getIntent().getStringExtra("CITIZEN_NAME");
        String birthdate = getIntent().getStringExtra("CITIZEN_BIRTHDATE");
        String gender = getIntent().getStringExtra("CITIZEN_GENDER");

        String details = "CIN: " + cin + "\n" +
                "Name: " + name + "\n" +
                "Birthdate: " + birthdate + "\n" +
                "Gender: " + gender;

        detailTextView.setText(details);
    }
}
