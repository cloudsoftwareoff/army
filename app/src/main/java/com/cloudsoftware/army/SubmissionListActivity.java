package com.cloudsoftware.army;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.cloudsoftware.army.fragment.SubmissionListFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SubmissionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_list);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ImageView go_back =findViewById(R.id.go_back);


        go_back.setOnClickListener(event->{
            finish();
        });
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 1:
                        return SubmissionListFragment.newInstance("approved");
                    case 2:
                        return SubmissionListFragment.newInstance("rejected");
                    default:
                        return SubmissionListFragment.newInstance("pending");
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
                    tab.setText("Pending");
                    break;
                case 1:
                    tab.setText("Approved");
                    break;
                case 2:
                    tab.setText("Rejected");
                    break;
            }
        }).attach();
    }
}
