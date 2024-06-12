package com.cloudsoftware.army;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudsoftware.army.adapters.SubmissionAdapter;
import com.cloudsoftware.army.models.Citizen;
import com.cloudsoftware.army.models.Submission;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoggedUserProfileActivity extends AppCompatActivity {
    private final List<Uri> selectedFilePaths = new ArrayList<>();
    private final List<String> selectedFilesNames = new ArrayList<>();
    private static final int PICK_DOCUMENT_REQUEST = 1;
    private Spinner submissionTypeSpinner;
    private FirebaseFirestore db;
    TextView tvSelectedFilePaths;
    Citizen citizen = new Citizen();
    ProgressDialog progressDialog;
    TextView messageText;
    private SubmissionAdapter submissionAdapter;
    private List<Submission> submissions;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logged_user_profile);

        db = FirebaseFirestore.getInstance();
        Button btnPickFile = findViewById(R.id.btn_pick_file);
        Button submit = findViewById(R.id.btn_submit);
        tvSelectedFilePaths = findViewById(R.id.tv_selected_file_paths);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.uploading_files));
        progressDialog.setCancelable(false);
        submissionTypeSpinner = findViewById(R.id.spinner_list);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.submission_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        submissionTypeSpinner.setAdapter(adapter);
        btnPickFile.setOnClickListener(v -> pickDocument());
        ListView listView = findViewById(R.id.submission_list_view);
        ImageView log_out = findViewById(R.id.log_out);
        mAuth = FirebaseAuth.getInstance();
        submissions = new ArrayList<>();
        submissionAdapter = new SubmissionAdapter(this, submissions);
        listView.setAdapter(submissionAdapter);
        messageText = findViewById(R.id.message);

        fetchUserData(FirebaseAuth.getInstance().getUid());
        loadSubmissions();
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
                            "pending");
                    addSubmissionToFirestore(submission);
                });
            } else {
                Toast.makeText(this, getString(R.string.please_pick_documents_first), Toast.LENGTH_SHORT).show();

            }
        });

        log_out.setOnClickListener(event -> {
            showLogoutDialog();
        });

    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.are_you_sure_you_want_to_logout);
        builder.setPositiveButton(R.string.logout, (dialog, which) -> logout());
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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
                            if (submission.getStatus().equals("pending")) {
                                // new_submission.setVisibility(View.GONE);
                            }
                        }
                        if (submissions.isEmpty()) {
                            // submissionList.setVisibility(View.GONE);
                        } else {
                            // submissionList.setVisibility(View.VISIBLE);
                        }
                        submissionAdapter.notifyDataSetChanged();
                    } else {
                        // submissionList.setVisibility(View.GONE);

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
            StorageReference fileRef = storageRef
                    .child("documents/" + citizen.getCin() + "/" + filePath.getLastPathSegment());

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
                    loadSubmissions();
                    showPopup("Success", getString(R.string.submission_added_successfully));
                })
                .addOnFailureListener(e -> {
                    showPopup("Error", getString(R.string.failed_to_add_submission));
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

    private void fetchUserData(String uid) {
        ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.fetching_user_data), true);

        // Query the "citizens" collection to find the document with the matching UID
        db.collection("citizens").whereEqualTo("uid", uid).get().addOnCompleteListener(task -> {
            // Dismiss the progress dialog once the task is complete
            progressDialog.dismiss();

            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    citizen = document.toObject(Citizen.class);
                    System.out.println(citizen);
                    if (citizen != null) {
                        loadSubmissions();
                        String message = "";
                        switch (citizen.getStatus()) {
                            case "Eligible":
                                message = getString(R.string.you_are_invited_to_join_the_army_training);
                                break;
                            case "Court":
                                message = getString(
                                        R.string.you_have_a_court_case_pending_please_resolve_this_before_joining);
                                break;
                            case "Warrant":
                                message = getString(
                                        R.string.there_is_a_warrant_out_for_your_arrest_please_resolve_this_issue);
                                break;
                            case "Exempt":
                                message = getString(R.string.you_are_exempt_from_joining_the_army_training);
                                break;
                            default:
                                message = getString(R.string.unknown_status_please_contact_support);
                                break;
                        }
                        messageText.setText(message);

                    }
                } else {

                    showPopup("Error", getString(R.string.no_user_found_with_the_provided_uid));
                }
            } else {
                // Query failed
                showPopup("Error", "Error fetching data.");
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
            progressDialog.dismiss();
            showPopup("Error", "Error fetching data: " + e.getMessage());
        });
    }

    private void pickDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

    public interface OnDocumentsUploaded {
        void onUploaded(List<String> uriList);
    }

}