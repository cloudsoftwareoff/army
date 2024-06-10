package com.cloudsoftware.army;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloudsoftware.army.adapters.CitizenAdapter;
import com.cloudsoftware.army.models.Citizen;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CourtCitizensFragment extends Fragment {
    private ListView listView;
    private CitizenAdapter adapter;
    private List<Citizen> citizens;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView noDataText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_court_citizens, container, false);
        listView = view.findViewById(R.id.citizen_list_view);
        progressBar = view.findViewById(R.id.progress_bar);
        noDataText = view.findViewById(R.id.no_data_text);
        citizens = new ArrayList<>();
        adapter = new CitizenAdapter(getContext(), citizens);
        listView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        loadCitizens();
        return view;
    }

    private void loadCitizens() {
        // Show the progress bar and hide the no data text
        progressBar.setVisibility(View.VISIBLE);
        noDataText.setVisibility(View.GONE);

        CitizenRepository citizenRepository = new CitizenRepository();
        citizenRepository.fetchCitizensByStatus("Court", fetchedCitizens -> {
            // Hide the progress bar
            progressBar.setVisibility(View.GONE);

            if (fetchedCitizens.isEmpty()) {
                // Show the no data text if the list is empty
                noDataText.setVisibility(View.VISIBLE);
            } else {
                // Hide the no data text and add the fetched citizens to the list
                noDataText.setVisibility(View.GONE);
                citizens.addAll(fetchedCitizens);
                adapter.notifyDataSetChanged();
            }
        }, () -> {
            // Hide the progress bar
            progressBar.setVisibility(View.GONE);

            // Handle failure
            noDataText.setVisibility(View.VISIBLE);
            noDataText.setText("Failed to load data");
        });
    }
}
