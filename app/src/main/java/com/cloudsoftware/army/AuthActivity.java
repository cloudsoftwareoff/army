package com.cloudsoftware.army;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudsoftware.army.models.UserClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AuthActivity extends AppCompatActivity {
private  EditText emailEdit,passwordEdit;
private FirebaseAuth mAuth;
private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        emailEdit=findViewById(R.id.emailEdit);
        passwordEdit=findViewById(R.id.password_edit);
        Button login_btn =findViewById(R.id.login);


        mAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        //Login
        login_btn.setOnClickListener(event->{
            if(emailEdit.getText().equals("") || !emailEdit.getText().toString().contains("@")){
                emailEdit.setError(getString(R.string.email_field_required));
                return;

            }
            if(passwordEdit.getText().toString().equals("")){
                passwordEdit.setError(getString(R.string.password_required));
                return;
            }
            performLogin(emailEdit.getText().toString(),passwordEdit.getText().toString());
        });

    }
    private void performLogin(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ProgressDialog progressDialog = ProgressDialog.show(AuthActivity.this, "", "Signing in...", true);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    // Get the UID of the current user
                    String uid = currentUser.getUid();

                    // Query the "users" collection to find the document with the matching UID
                    db.collection("users").whereEqualTo("userId", uid).get().addOnCompleteListener(xtask -> {
                        progressDialog.dismiss();
                        if (xtask.isSuccessful()) {
                            if (!xtask.getResult().isEmpty()) {
                                // User document found, assume the user is an admin
                                editor.putString("user", "admin");
                                editor.apply();

                                Intent intent = new Intent(AuthActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                // No document found, assume the user is a citizen
                                editor.putString("user", "citizen");
                                editor.apply();

                                Intent intent = new Intent(AuthActivity.this, LoggedUserProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // Query failed
                            editor.putString("user", "citizen");
                            editor.apply();

                            Intent intent = new Intent(AuthActivity.this, LoggedUserProfileActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(e -> {
                        // Query failed
                        progressDialog.dismiss();
                        editor.putString("user", "citizen");
                        editor.apply();

                        Intent intent = new Intent(AuthActivity.this, LoggedUserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    // No current user
                    progressDialog.dismiss();
                    Toast.makeText(AuthActivity.this, R.string.no_user_found, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Sign-in failed
                progressDialog.dismiss();
                Toast.makeText(AuthActivity.this, getString(R.string.sign_in_failed) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(this, task -> {
            progressDialog.dismiss();
            Toast.makeText(AuthActivity.this, getString(R.string.sign_in_failed) + task.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}