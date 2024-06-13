package com.cloudsoftware.army;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudsoftware.army.db.RecruitingManager;
import com.cloudsoftware.army.models.ArmyRecruitingRound;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddRoundActivity extends AppCompatActivity {

    private EditText roundName, startDate, endDate, location;
    private RecruitingManager recruitingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_round);

        roundName = findViewById(R.id.round_name);
        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        location = findViewById(R.id.location);
        Button btnAddRound = findViewById(R.id.btn_add_round);

        recruitingManager = new RecruitingManager();

        startDate.setOnClickListener(v -> showDatePickerDialog(startDate));
        endDate.setOnClickListener(v -> showDatePickerDialog(endDate));

        btnAddRound.setOnClickListener(v -> {
            String name = roundName.getText().toString().trim();
            String start = startDate.getText().toString().trim();
            String end = endDate.getText().toString().trim();
            String loc = location.getText().toString().trim();

            if (name.isEmpty() || start.isEmpty() || end.isEmpty() || loc.isEmpty()) {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            ArmyRecruitingRound round = new ArmyRecruitingRound();

            round.setRoundName(name);
            round.setStartDate(start);
            round.setEndDate(end);
            round.setLocation(loc);
            round.setCandidatesId(new ArrayList<>());
            round.setSelectedCandidatesId(new ArrayList<>());

            recruitingManager.addRound(round, (isSuccess, addedRound) -> {
                if (isSuccess) {
                    Toast.makeText(this, R.string.round_added_successfully, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.failed_to_add_round, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showDatePickerDialog(EditText dateField) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel(dateField, calendar);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
}

    private void updateLabel(EditText dateField, Calendar calendar) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        dateField.setText(sdf.format(calendar.getTime()));
    }
}
