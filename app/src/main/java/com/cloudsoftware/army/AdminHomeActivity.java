package com.cloudsoftware.army;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cloudsoftware.army.db.RecruitingManager;
import com.cloudsoftware.army.fragment.RoundListFragment;
import com.cloudsoftware.army.models.ArmyRecruitingRound;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicReference;

public class AdminHomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);


        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        LinearLayout drawer = findViewById(R.id._nav_view);
        LinearLayout log_out = drawer.findViewById(R.id.logout);

        LinearLayout view_citizen=findViewById(R.id.manage_citizen);
        TextView user_email=findViewById(R.id.user_email);
        ImageView menu = findViewById(R.id.menu);
        ImageView view_requests = findViewById(R.id.requests);
        LinearLayout add_round=drawer.findViewById(R.id.add_round_linear);

        AtomicReference<Boolean> canAdd= new AtomicReference<>(false);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RoundListFragment())
                    .commit();
        }
        mAuth = FirebaseAuth.getInstance();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        RecruitingManager recruitingManager=new RecruitingManager();
        recruitingManager.getAllRounds((isSuccess, rounds) ->{
            if(isSuccess){
                for (ArmyRecruitingRound round:rounds){
                    if(!round.hasNotEnded()){
                        canAdd.set(true);
                        break;
                    }
                }
            }
        });
        user_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        // Drawer
        menu.setOnClickListener(event -> drawerLayout.openDrawer(GravityCompat.START));

        // Logout
        log_out.setOnClickListener(v -> showLogoutDialog());

        view_requests.setOnClickListener(event -> {
            Intent intent = new Intent(this, SubmissionListActivity.class);
            startActivity(intent);
        });
        view_citizen.setOnClickListener(event -> {
            Intent intent = new Intent(this, CitizenListActivity.class);
            startActivity(intent);
        });
        add_round.setOnClickListener(event -> {
            if(!canAdd.get()){
                showPopup(getString(R.string.there_s_recruiting_round_that_has_not_ended_yet));
                return;
            }
            Intent intent = new Intent(this, AddRoundActivity.class);
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void showPopup(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.recruiting_round_alert)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
