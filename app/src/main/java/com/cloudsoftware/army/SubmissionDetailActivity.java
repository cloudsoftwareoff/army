package com.cloudsoftware.army;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private Spinner statusSpinner;
    private Button saveStatusButton;
    private Submission submission;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_detail);

        cinView = findViewById(R.id.cin);
        dateView = findViewById(R.id.date);
        typeView = findViewById(R.id.type);
        statusView = findViewById(R.id.status);
        documentsRecyclerView = findViewById(R.id.documents_recycler_view);
        statusSpinner = findViewById(R.id.status_spinner);
        saveStatusButton = findViewById(R.id.save_status_button);

        submission = getIntent().getParcelableExtra("SUBMISSION");
        if (submission != null) {
            displaySubmissionDetails(submission);
        }

        // Set up status spinner
        List<String> statusOptions = Arrays.asList("pending", "approved", "rejected");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        saveStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStatus();
            }
        });
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
        int statusPosition = ((ArrayAdapter<String>) statusSpinner.getAdapter()).getPosition(submission.getStatus());
        statusSpinner.setSelection(statusPosition);
    }

    private void saveStatus() {
        String newStatus = statusSpinner.getSelectedItem().toString();
        submission.setStatus(newStatus);

        // Save the new status to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("submissions").document(submission.getSubmissionId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SubmissionDetailActivity.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                    statusView.setText("Status: " + newStatus);
                })
                .addOnFailureListener(e -> Toast.makeText(SubmissionDetailActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show());
    }
}
