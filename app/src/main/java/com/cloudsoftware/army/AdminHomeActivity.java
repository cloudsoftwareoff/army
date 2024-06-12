package com.cloudsoftware.army;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.cloudsoftware.army.adapters.CitizenAdapter;
import com.cloudsoftware.army.fragment.CitizensFragment;
import com.cloudsoftware.army.models.Citizen;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
       // citizens = new ArrayList<>();
        //adapter = new CitizenAdapter(this, citizens);

        mAuth = FirebaseAuth.getInstance();
        citizenRepository = new CitizenRepository();
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 1:
                        return CitizensFragment.newInstance("Penalisé");
                    case 2:
                        return CitizensFragment.newInstance("Exempt");
                    default:
                        return CitizensFragment.newInstance("Eligible");
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Eligible");
                    break;
                case 1:
                    tab.setText("Penalisé");
                    break;
                case 2:
                    tab.setText("Exempt");
                    break;
            }
        }).attach();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        LinearLayout drawer = findViewById(R.id._nav_view);
        LinearLayout log_out = drawer.findViewById(R.id.logout);
        ImageView menu = findViewById(R.id.menu);
        db = FirebaseFirestore.getInstance();
        Button start_recruiting = findViewById(R.id.start);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        //loadCitizens();

        // Drawer
        menu.setOnClickListener(event -> drawerLayout.openDrawer(GravityCompat.START));

        // Logout
        log_out.setOnClickListener(v -> showLogoutDialog());

        start_recruiting.setOnClickListener(event -> {
            Intent intent = new Intent(this, SubmissionListActivity.class);
            startActivity(intent);
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
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    public void loadCitizens() {
        progressDialog.show();
        CollectionReference citizensRef = db.collection("citizens");
        citizensRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                citizens.clear();
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
