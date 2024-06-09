package com.cloudsoftware.army;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.type.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PICK_DOCUMENT_REQUEST = 1;

    private FirebaseFirestore db;
    private TextView nameView, cinView, birthdateView, genderView, statusView, notificationsView;
    private Button pickFiles;
    private TextView tvSelectedFilePaths;
    Citizen citizen;
    private List<String> selectedFilePaths = new ArrayList<>();
    private List<String> documents = new ArrayList<>();
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
        tvSelectedFilePaths = findViewById(R.id.tv_selected_file_paths);

        btnPickFile.setOnClickListener(v -> pickDocument());

         citizen = getIntent().getParcelableExtra("CITIZEN");
        if (citizen != null) {
            displayUserData(citizen);

        }

        pickFiles.setOnClickListener(view -> {
            pickDocument();
        });
        submit.setOnClickListener(event->{
        UploadDocument(selectedFilePaths);
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
        Submission submission =new Submission(
                null,
                citizen.getCin(),
                documents,
                currentDate,
                "study",
                "",
                "pending");


        });
    }
    private void UploadDocument(List<String> files) {
        if (selectedFilePaths.isEmpty()) {
            // No file selected
            return;
        }

         StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        for (String filePath : files) {
            Uri fileUri = Uri.fromFile(new File(filePath));
            StorageReference fileRef = storageRef.child("documents/"+citizen.getCin()+"/" + fileUri.getLastPathSegment());

            fileRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // File uploaded successfully
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            documents.add(downloadUrl);

                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads

                    });
        }
    }


    private void displayUserData(Citizen citizen) {
        nameView.setText("Name: " + citizen.getFirstName() + " " + citizen.getLastName());
        cinView.setText("CIN: " + citizen.getCin());
        birthdateView.setText("Birthdate: " + citizen.getBirthdate());
        genderView.setText("Gender: " + citizen.getGender());
        statusView.setText("Status: " + citizen.getStatus());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DOCUMENT_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String filePath = FileUtils.getPath(this, uri);
                selectedFilePaths.add(filePath);
                tvSelectedFilePaths.setText(filePath);
            }
        }
    }



}
