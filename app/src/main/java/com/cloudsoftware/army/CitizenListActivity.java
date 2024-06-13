package com.cloudsoftware.army;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.cloudsoftware.army.adapters.CitizenAdapter;
import com.cloudsoftware.army.fragment.CitizensFragment;
import com.cloudsoftware.army.models.Citizen;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CitizenListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView listView;
    private CitizenAdapter adapter;
    private List<Citizen> citizens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_list);


        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ImageView go_back=findViewById(R.id.goback);
        go_back.setOnClickListener(v->{
            finish();
        });

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
                    tab.setText(getString( R.string.eligible));
                    break;
                case 1:
                    tab.setText(getString( R.string.penalis));
                    break;
                case 2:
                    tab.setText(getString( R.string.exempt));
                    break;
            }
        }).attach();


    }


}
