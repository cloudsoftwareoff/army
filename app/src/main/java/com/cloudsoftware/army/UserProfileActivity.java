package com.cloudsoftware.army;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudsoftware.army.adapters.SubmissionAdapter;
import com.cloudsoftware.army.models.Citizen;
import com.cloudsoftware.army.models.Submission;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PICK_DOCUMENT_REQUEST = 1;
    private Spinner submissionTypeSpinner;
    private FirebaseFirestore db;
    private TextView nameView, cinView, birthdateView, genderView, statusView, notificationsView, tvSelectedFilePaths;
    private ProgressDialog progressDialog;
    private final List<Uri> selectedFilePaths = new ArrayList<>();
    private final List<String> selectedFilesNames = new ArrayList<>();
    private final List<String> documents = new ArrayList<>();
    private Citizen citizen;
    private SubmissionAdapter submissionAdapter;
    private List<Submission> submissions;
    LinearLayout new_submission,submissionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = FirebaseFirestore.getInstance();

        nameView = findViewById(R.id.name);
        cinView = findViewById(R.id.cin);
        birthdateView = findViewById(R.id.birthdate);
        genderView = findViewById(R.id.gender);
        statusView = findViewById(R.id.status);
        notificationsView = findViewById(R.id.notifications);
        Button btnPickFile = findViewById(R.id.btn_pick_file);
        Button submit = findViewById(R.id.btn_submit);
        new_submission=findViewById(R.id.new_app_linear);
        submissionList=findViewById(R.id.apps_linear);
        ImageView go_back=findViewById(R.id.go_back);
        ListView listView=findViewById(R.id.submission_list_view);
        tvSelectedFilePaths = findViewById(R.id.tv_selected_file_paths);
        submissions = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading files...");
        progressDialog.setCancelable(false);

        submissionTypeSpinner = findViewById(R.id.spinner_list);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.submission_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        submissionAdapter = new SubmissionAdapter(this, submissions);
        listView.setAdapter(submissionAdapter);
        // Apply the adapter to the spinner
        submissionTypeSpinner.setAdapter(adapter);


        //pick date
        btnPickFile.setOnClickListener(v -> pickDocument());

        citizen = getIntent().getParcelableExtra("CITIZEN");
        if (citizen != null) {
            displayUserData(citizen);
        }
        loadSubmissions();


        go_back.setOnClickListener(event->{
            finish();
        });
        // submit
        submit.setOnClickListener(event -> {
            if (!selectedFilePaths.isEmpty()) {
                progressDialog.show();
                uploadDocuments(selectedFilePaths, uriList -> {
                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();
                    Submission submission = new Submission(
                            null,
                            citizen.getCin(),
                            uriList,
                            currentDate,
                            submissionTypeSpinner.getSelectedItem().toString(),
                            "",
                            "pending"
                    );
                    addSubmissionToFirestore(submission);
                });
            }
        });
    }

    private void uploadDocuments(List<Uri> files, OnDocumentsUploaded callback) {
        if (files.isEmpty()) {
            return;
        }

        List<String> uploadedDocumentUrls = new ArrayList<>();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        for (Uri filePath : files) {
            StorageReference fileRef = storageRef.child("documents/" + citizen.getCin() + "/" + filePath.getLastPathSegment());

            fileRef.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            uploadedDocumentUrls.add(uri.toString());
                            if (uploadedDocumentUrls.size() == files.size()) {
                                progressDialog.dismiss();
                                callback.onUploaded(uploadedDocumentUrls);
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        showPopup("Error", "Failed to upload file: " + filePath.getLastPathSegment());
                    });
        }
    }

    private void addSubmissionToFirestore(Submission submission) {
        // Create a new document reference
        DocumentReference documentReference = db.collection("submissions").document();
        String submissionId = documentReference.getId();
        submission.setSubmissionId(submissionId);

        // Add the submission to Firestore
        documentReference
                .set(submission)
                .addOnSuccessListener(aVoid -> {
                    showPopup("Success", "Submission added successfully.");
                })
                .addOnFailureListener(e -> {
                    showPopup("Error", "Failed to add submission.");
                });
    }

    private void loadSubmissions() {
        db.collection("submissions")
                .whereEqualTo("userId", citizen.getCin())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        submissions.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Submission submission = document.toObject(Submission.class);
                            submissions.add(submission);
                            if(submission.getStatus().equals("pending")){
                           //     new_submission.setVisibility(View.GONE);
                            }
                        }
                        if (submissions.isEmpty()) {
                         //   submissionList.setVisibility(View.GONE);
                        } else {
                            submissionList.setVisibility(View.VISIBLE);
                        }
                        submissionAdapter.notifyDataSetChanged();
                    } else {
                       // submissionList.setVisibility(View.GONE);

                    }
                });
    }
    private void displayUserData(Citizen citizen) {
        nameView.setText("Name: " + citizen.getFirstName() + " " + citizen.getLastName());
        cinView.setText("CIN: " + citizen.getCin());
        birthdateView.setText("Birthdate: " + citizen.getBirthdate());
        genderView.setText("Gender: " + citizen.getGender());
        statusView.setText(citizen.getStatus());
    }

    private void showPopup(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void pickDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DOCUMENT_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedFilePaths.add(uri);
                selectedFilesNames.add(uri.getLastPathSegment());
                tvSelectedFilePaths.setText(String.join("\n", selectedFilesNames));
            }
        }
    }

    private interface OnDocumentsUploaded {
        void onUploaded(List<String> uriList);
    }
}
