package com.cloudsoftware.army;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudsoftware.army.db.CitizenRepository;
import com.cloudsoftware.army.models.Citizen;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddCitizenActivity extends AppCompatActivity {

    private TextInputEditText cinEditText, firstNameEditText, lastNameEditText, birthdateEditText;
    private Spinner genderSpinner, statusSpinner;
    private Button addCitizenButton;

    private CitizenRepository citizenRepository;
    private Citizen citizenToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_citizen);

        cinEditText = findViewById(R.id.cin);
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        birthdateEditText = findViewById(R.id.birthdate);
        genderSpinner = findViewById(R.id.gender);
        statusSpinner = findViewById(R.id.status);
        addCitizenButton = findViewById(R.id.btn_add_citizen);

        // Initialize the CitizenRepository
        citizenRepository = new CitizenRepository();

        // Set up the gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Set up the status spinner
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        birthdateEditText.setOnClickListener(v -> showDatePickerDialog());


        // Check if there is a citizen passed for editing
         citizenToEdit = getIntent().getParcelableExtra("CITIZEN");
        if (citizenToEdit != null) {
            // Toast.makeText(this, "not null", Toast.LENGTH_SHORT).show();
            populateFieldsForEditing(citizenToEdit);
            addCitizenButton.setText(R.string.update_citizen);
        }

        addCitizenButton.setOnClickListener(v -> {
            if (citizenToEdit != null) {
                updateCitizen();
            } else {
                addCitizen();
            }
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

    private void populateFieldsForEditing(Citizen citizen) {
        cinEditText.setText(citizen.getCin());
        firstNameEditText.setText(citizen.getFirstName());
        lastNameEditText.setText(citizen.getLastName());
        birthdateEditText.setText(citizen.getBirthdate());
        genderSpinner.setSelection(citizen.getGender().equals("Male") ? 0 : 1);

        String[] statusArrayEn = getResources().getStringArray(R.array.citizen_status);
        for (int i = 0; i < statusArrayEn.length; i++) {
            if (statusArrayEn[i].equals(citizen.getStatus())) {
                statusSpinner.setSelection(i);
                break;
            }
        }
    }
    private void updateLabel(Calendar calendar) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        birthdateEditText.setText(sdf.format(calendar.getTime()));
    }

    private void addCitizen() {
        String cin = cinEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String birthdate = birthdateEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItemPosition() == 0 ? "Male" : "Female";

        // Get status in English based on selected position
        String[] statusArrayEn = getResources().getStringArray(R.array.citizen_status);
        String status = statusArrayEn[statusSpinner.getSelectedItemPosition()];

        if (cin.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || birthdate.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Citizen citizen = new Citizen(cin, firstName, lastName, birthdate, gender, status, null);

        // Save the citizen object to the database
        citizenRepository.addCitizen(citizen, () -> {
            Toast.makeText(AddCitizenActivity.this, "Citizen added", Toast.LENGTH_SHORT).show();
            finish();
        }, () -> {
            Toast.makeText(AddCitizenActivity.this, "Failed to add citizen", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateCitizen() {
        String cin = cinEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String birthdate = birthdateEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItemPosition() == 0 ? "Male" : "Female";

        // Get status in English based on selected position
        String[] statusArrayEn = getResources().getStringArray(R.array.citizen_status);
        String status = statusArrayEn[statusSpinner.getSelectedItemPosition()];

        if (cin.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || birthdate.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        citizenToEdit.setCin(cin);
        citizenToEdit.setFirstName(firstName);
        citizenToEdit.setLastName(lastName);
        citizenToEdit.setBirthdate(birthdate);
        citizenToEdit.setGender(gender);
        citizenToEdit.setStatus(status);

        // Update the citizen object in the database
        citizenRepository.updateCitizen(citizenToEdit, () -> {
            Toast.makeText(AddCitizenActivity.this, "Citizen updated", Toast.LENGTH_SHORT).show();
            finish();
        }, () -> {
            Toast.makeText(AddCitizenActivity.this, "Failed to update citizen", Toast.LENGTH_SHORT).show();
        });
    }
}
