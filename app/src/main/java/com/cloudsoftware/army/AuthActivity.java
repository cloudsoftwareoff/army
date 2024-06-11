package com.cloudsoftware.army;

import android.app.ProgressDialog;
import android.content.Intent;
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
        Button signup_btn=findViewById(R.id.signup);

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
        //Create account
        signup_btn.setOnClickListener(event->{
            if(emailEdit.getText().equals("") || !emailEdit.getText().toString().contains("@")){
                emailEdit.setError(getString(R.string.email_field_required));
                return;

            }
            if(passwordEdit.getText().toString().equals("")){
                passwordEdit.setError("Password required");
                return;
            }

            performSignup(emailEdit.getText().toString(),passwordEdit.getText().toString());

        });

    }

    private  void performLogin(String email,String password){
        ProgressDialog progressDialog = ProgressDialog.show(AuthActivity.this, "", "Signing in...", true);
mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this,task -> {
    if(task.isSuccessful()){
        progressDialog.dismiss();
        FirebaseUser user =mAuth.getCurrentUser();
        Intent intent = new Intent(AuthActivity.this, AdminHomeActivity.class);
        startActivity(intent);
        finish();
    }else {
        Toast.makeText(AuthActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

    }
}).addOnFailureListener(this,task->{
    Toast.makeText(AuthActivity.this, task.toString(),
            Toast.LENGTH_SHORT).show();
});
    }
    private void performSignup(String email, String password) {
        ProgressDialog progressDialog = ProgressDialog.show(AuthActivity.this, "", "Signing in...", true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();
                        UserClass user = new UserClass(email, password, email.substring(0,email.indexOf("@")), userId);
                        // Save user to Firestore
                        db.collection("users").document(userId).set(user)
                                .addOnCompleteListener(this, dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Intent intent = new Intent(AuthActivity.this, AdminHomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(AuthActivity.this, "Failed to save user data: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    } else {

                        Toast.makeText(AuthActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this,task->{
                    Toast.makeText(AuthActivity.this, task.toString(),
                            Toast.LENGTH_SHORT).show();
                })
        ;
    }
}