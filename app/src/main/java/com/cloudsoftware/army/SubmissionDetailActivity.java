package com.cloudsoftware.army;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudsoftware.army.adapters.DocumentAdapter;
import com.cloudsoftware.army.models.Submission;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class SubmissionDetailActivity extends AppCompatActivity {

    private TextView cinView, dateView, typeView, statusView;
    private RecyclerView documentsRecyclerView;
    private Spinner statusSpinner, userStatusSpinner;
    private Button saveStatusButton;
    private Submission submission;
    private EditText noteEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_detail);

        cinView = findViewById(R.id.cin);
        dateView = findViewById(R.id.date);
        typeView = findViewById(R.id.type);
        statusView = findViewById(R.id.status);
        documentsRecyclerView = findViewById(R.id.documents_recycler_view);
        statusSpinner = findViewById(R.id.submission_status_spinner);
        userStatusSpinner = findViewById(R.id.citizen_status_spinner);
        saveStatusButton = findViewById(R.id.save_status_button);
        noteEdit = findViewById(R.id.noteEdit);
        ImageView go_back=findViewById(R.id.go_back);
        submission = getIntent().getParcelableExtra("SUBMISSION");
        if (submission != null) {
            displaySubmissionDetails(submission);
        }

        go_back.setOnClickListener(v->{
            finish();
        });

        // citizen status adapter
        ArrayAdapter<CharSequence> citizen_status_adapter = ArrayAdapter.createFromResource(this,
                R.array.citizen_status, android.R.layout.simple_spinner_item);
        citizen_status_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userStatusSpinner.setAdapter(citizen_status_adapter);


        // Submission adapter
        List<String> statusOptions = Arrays.asList("pending", "approved", "rejected");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        saveStatusButton.setOnClickListener(v -> saveStatus());
    }

    private void displaySubmissionDetails(Submission submission) {
        cinView.setText("CIN: " + submission.getUserId());
        dateView.setText("Date: " + submission.getSubmissionDate());
        typeView.setText("Type: " + submission.getSubmissionType());
        statusView.setText("Status: " + submission.getStatus());

        DocumentAdapter adapter = new DocumentAdapter(this, submission.getDocumentUrls());
        documentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        documentsRecyclerView.setAdapter(adapter);

        // Set current status in spinner
        // Ensure adapter is set before accessing it
        if (statusSpinner.getAdapter() != null) {
            int statusPosition = ((ArrayAdapter<String>) statusSpinner.getAdapter()).getPosition(submission.getStatus());
            statusSpinner.setSelection(statusPosition);
        }
    }

    private void saveStatus() {
        String newStatus = statusSpinner.getSelectedItem().toString();
        submission.setStatus(newStatus);
        submission.setNote(noteEdit.getText().toString());



        // Save the new status to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("citizens").document(submission.getUserId())
                        .update("status",userStatusSpinner.getSelectedItem().toString())
                                .addOnSuccessListener(aVoid->{
                                    //success
                                })
                                        .addOnFailureListener(e->{
                                            Toast.makeText(SubmissionDetailActivity.this, "Failed to citizen status", Toast.LENGTH_SHORT).show()        ;


                                        });
        db.collection("submissions").document(submission.getSubmissionId())
                .update("status", newStatus, "note", submission.getNote())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SubmissionDetailActivity.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                    statusView.setText("Status: " + newStatus);
                })
                .addOnFailureListener(e -> Toast.makeText(SubmissionDetailActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show());
    }
}
