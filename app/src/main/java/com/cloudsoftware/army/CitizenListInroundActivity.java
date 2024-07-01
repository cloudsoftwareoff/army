package com.cloudsoftware.army;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudsoftware.army.adapters.CitizenAdapter;
import com.cloudsoftware.army.db.CitizenRepository;
import com.cloudsoftware.army.fragment.CitizensFragment;
import com.cloudsoftware.army.fragment.RoundListFragment;
import com.cloudsoftware.army.models.ArmyRecruitingRound;
import com.cloudsoftware.army.models.Citizen;

import java.util.ArrayList;
import java.util.List;

public class CitizenListInroundActivity extends AppCompatActivity {
private ProgressBar progressBar;
    private List<String> citizensId;
    private CitizenAdapter adapter;
    private List<Citizen> citizens;
    private ArmyRecruitingRound armyRecruitingRound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_citizen_list_inround);
        ListView listView =findViewById(R.id.citizen_list_view);
        progressBar = findViewById(R.id.progress_bar);
        citizens = new ArrayList<>();
        adapter = new CitizenAdapter(this, citizens,(citizen)->{

        });
        listView.setAdapter(adapter);
        armyRecruitingRound = getIntent().getParcelableExtra("round");
        citizensId=armyRecruitingRound.getCandidatesId();
        loadCitizens();
    }

    private void loadCitizens() {
        progressBar.setVisibility(View.VISIBLE);
        //List<String> citizenIds = round.getCitizenList();
        CitizenRepository citizenRepository = new CitizenRepository();

        for (String id : citizensId) {
            citizenRepository.fetchCitizenById(id, new CitizenRepository.OnCitizenFetchedListener() {
                @Override
                public void onFetched(Citizen citizen) {
                    citizens.add(citizen);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError() {
                    // Handle error
                }
            });
        }

        progressBar.setVisibility(View.GONE);
    }

}