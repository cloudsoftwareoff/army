package com.cloudsoftware.army;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
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
    private FirebaseAuth mAuth;
    private CitizenRepository citizenRepository;
    private ProgressDialog progressDialog;

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
        citizenRepository = new CitizenRepository();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        LinearLayout drawer = findViewById(R.id._nav_view);
        LinearLayout log_out = drawer.findViewById(R.id.logout);
        ImageView menu = findViewById(R.id.menu);
        db = FirebaseFirestore.getInstance();
        Button start_recruiting = findViewById(R.id.start);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        loadCitizens();

        // Drawer
        menu.setOnClickListener(event -> drawerLayout.openDrawer(GravityCompat.START));

        // Logout
        log_out.setOnClickListener(v -> showLogoutDialog());

        start_recruiting.setOnClickListener(event -> {
            Intent intent = new Intent(this, ManageCitizenActivity.class);
            startActivity(intent);
            // Set all citizens' status to "recruit"
          /*  progressDialog.setMessage("Updating status...");
            progressDialog.show();
            citizenRepository.setAllCitizensStatus("Eligible", citizens, new CitizenRepository.StatusUpdateCallback() {
                @Override
                public void onStatusUpdateComplete() {
                    progressDialog.dismiss();
                    showPopup("Success", "All citizens' status updated successfully.");
                }

                @Override
                public void onStatusUpdateFailed() {
                    progressDialog.dismiss();
                    showPopup("Error", "Failed to update some citizens' status.");
                }
            });*/
        });
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Logout", (dialog, which) -> logout());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    public void loadCitizens() {
        progressDialog.show();
        CollectionReference citizensRef = db.collection("citizens");
        citizensRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Citizen citizen = document.toObject(Citizen.class);
                    if (citizen.isEighteenOrOlder()) {
                        citizens.add(citizen);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                // Handle error
            }
            progressDialog.dismiss();
        });
    }

    private void showPopup(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
