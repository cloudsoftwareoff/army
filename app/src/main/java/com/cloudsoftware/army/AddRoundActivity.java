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
    private ArmyRecruitingRound existingRound;

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

        // Check if we are in edit mode
        existingRound = getIntent().getParcelableExtra("round");
        if (existingRound != null) {
            populateFields(existingRound);
            btnAddRound.setText(R.string.update_round);
        }

        btnAddRound.setOnClickListener(v -> {
            String name = roundName.getText().toString().trim();
            String start = startDate.getText().toString().trim();
            String end = endDate.getText().toString().trim();
            String loc = location.getText().toString().trim();

            if (name.isEmpty() || start.isEmpty() || end.isEmpty() || loc.isEmpty()) {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            if (existingRound == null) {
                existingRound = new ArmyRecruitingRound();
            }

            existingRound.setRoundName(name);
            existingRound.setStartDate(start);
            existingRound.setEndDate(end);
            existingRound.setLocation(loc);

            if (existingRound.getId() == null) {
                existingRound.setCandidatesId(new ArrayList<>());
                existingRound.setSelectedCandidatesId(new ArrayList<>());
                addRound(existingRound);
            } else {
                updateRound(existingRound);
            }
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

    private void populateFields(ArmyRecruitingRound round) {
        roundName.setText(round.getRoundName());
        startDate.setText(round.getStartDate());
        endDate.setText(round.getEndDate());
        location.setText(round.getLocation());
    }

    private void addRound(ArmyRecruitingRound round) {
        recruitingManager.addRound(round, (isSuccess, addedRound) -> {
            if (isSuccess) {
                Toast.makeText(this, R.string.round_added_successfully, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.failed_to_add_round, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRound(ArmyRecruitingRound round) {
        recruitingManager.updateRound(round, (isSuccess, updatedRound) -> {
            if (isSuccess) {
                Toast.makeText(this, R.string.round_updated_successfully, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.failed_to_update_round, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
