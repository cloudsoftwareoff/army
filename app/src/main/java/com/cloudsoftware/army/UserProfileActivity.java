package com.cloudsoftware.army;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudsoftware.army.adapters.SubmissionAdapter;
import com.cloudsoftware.army.models.Citizen;
import com.cloudsoftware.army.models.Submission;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private TextView nameView, cinView, birthdateView, genderView, statusView;

private Button create_account;
    private Citizen citizen;

    LinearLayout new_submission,submissionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = FirebaseFirestore.getInstance();

        nameView = findViewById(R.id.name_value);
        cinView = findViewById(R.id.cin_value);
        birthdateView = findViewById(R.id.birthdate_value);
        genderView = findViewById(R.id.gender_value);
        statusView = findViewById(R.id.status_value);
         create_account=findViewById(R.id.create_account);
       // submissionList=findViewById(R.id.apps_linear);
        ImageView go_back=findViewById(R.id.go_back);



        citizen = getIntent().getParcelableExtra("CITIZEN");
        if (citizen != null) {
            displayUserData(citizen);
        }


        //create account
        create_account.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_account, null);
            builder.setView(dialogView);

            TextInputLayout emailInputLayout = dialogView.findViewById(R.id.email_input_layout);
            EditText emailInput = dialogView.findViewById(R.id.email_input);


            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                String email = emailInput.getText().toString().trim();
                if(email.equals("") || !email.contains("@")){
                    Toast.makeText(this, R.string.email_is_required, Toast.LENGTH_SHORT).show();

                    return;
                }
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getString(R.string.creating_account));
                progressDialog.setCancelable(false);
                progressDialog.show();


                // Create Firebase account with the entered email
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, citizen.getCin())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Account creation successful
                                progressDialog.dismiss();
                                Toast.makeText(this, R.string.account_created_successfully, Toast.LENGTH_SHORT).show();
                               citizen.setUid(FirebaseAuth.getInstance().getUid());
                                Map<String, Object> data = new HashMap<>();
                                data.put("uid", citizen.getUid());


                                db.collection("citizens").document(citizen.getCin())
                                        .update(data)
                                        .addOnSuccessListener(aVoid -> {
                                            AlertDialog.Builder xbuilder = new AlertDialog.Builder(this);
                                            xbuilder.setTitle( R.string.account_created_successfully)
                                                    .setMessage(getString(R.string.your_login_details_email) + email + R.string.password + citizen.getCin())
                                                    .setPositiveButton("OK", (xdialog, xwhich) -> {

                                                        xdialog.dismiss();
                                                        Intent intent = new Intent(this, AuthActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    })
                                                    .show();

                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, R.string.error_setting_uid_for_citizen_with_cin, Toast.LENGTH_SHORT).show();


                                        });

                            } else {
                                // Account creation failed
                                progressDialog.dismiss();
                                Toast.makeText(this, getString(R.string.account_creation_failed) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });




            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

            // Show the dialog
            builder.show();
        });


        // loadSubmissions();


        go_back.setOnClickListener(event->{
            finish();
        });
        // submit

    }


    private void displayUserData(Citizen citizen) {
        nameView.setText(  citizen.getFirstName() + " " + citizen.getLastName());
        cinView.setText( citizen.getCin());
        birthdateView.setText( citizen.getBirthdate());
        genderView.setText(citizen.getGender());
        if(!citizen.getUid().isEmpty() || !citizen.getUid().equals("")){
            create_account.setVisibility(View.GONE);
        }
        String message;
        switch (citizen.getStatus()) {
            case "Eligible":
                message = getString(R.string.you_are_invited_to_join_the_army_training);
                break;
            case "Court":
                message = getString(R.string.you_have_a_court_case_pending_please_resolve_this_before_joining);
                break;
            case "Warrant":
                message = getString(R.string.there_is_a_warrant_out_for_your_arrest_please_resolve_this_issue);
                break;
            case "Exempt":
                message = getString(R.string.you_are_exempt_from_joining_the_army_training);
                break;
            default:
                message = getString(R.string.unknown_status_please_contact_support);
                break;
        }

        statusView.setText(message);
    }








}
