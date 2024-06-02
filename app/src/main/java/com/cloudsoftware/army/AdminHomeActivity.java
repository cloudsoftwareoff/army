package com.cloudsoftware.army;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ListView listView;
    private CitizenAdapter adapter;
    private List<Citizen> citizens;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
        listView = findViewById(R.id.citizen);
        citizens = new ArrayList<>();
        adapter = new CitizenAdapter(this, citizens);
        listView.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        LinearLayout drawer = findViewById(R.id._nav_view);
        LinearLayout log_out = drawer.findViewById(R.id.logout);
        ImageView menu = findViewById(R.id.menu);
        db = FirebaseFirestore.getInstance();
        loadCitizens();


        // Dawer
        menu.setOnClickListener(event -> {
            drawerLayout.openDrawer(GravityCompat.START);

        });

        // Logout
        log_out.setOnClickListener(v -> {

            showLogoutDialog();
        });
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Logout", (dialog, which) -> logout());
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // User cancelled logout, dismiss the dialog
            dialog.dismiss();
        });
        builder.show();
    }
    private void logout() {

        mAuth.signOut();
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
    public void loadCitizens() {
        CollectionReference citizensRef = db.collection("citizens");
        citizensRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Citizen citizen = document.toObject(Citizen.class);
                    if(citizen.isEighteenOrOlder()) {
                        citizens.add(citizen);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                //  error
            }
        });
    }
}