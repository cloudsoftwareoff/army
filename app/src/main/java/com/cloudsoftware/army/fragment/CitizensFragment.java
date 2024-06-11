package com.cloudsoftware.army.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloudsoftware.army.CitizenRepository;
import com.cloudsoftware.army.R;
import com.cloudsoftware.army.adapters.CitizenAdapter;
import com.cloudsoftware.army.models.Citizen;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CitizensFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private CitizenAdapter adapter;
    private List<Citizen> citizens;
    private ProgressBar progressBar;
    private String status;

    public static CitizensFragment newInstance(String status) {
        CitizensFragment fragment = new CitizensFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_citizens, container, false);
        ListView listView = view.findViewById(R.id.citizen_list_view);
        progressBar = view.findViewById(R.id.progress_bar);
        citizens = new ArrayList<>();
        adapter = new CitizenAdapter(getContext(), citizens);
        listView.setAdapter(adapter);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }

        loadCitizens();
        return view;
    }

    private void loadCitizens() {
        progressBar.setVisibility(View.VISIBLE);
        CitizenRepository citizenRepository = new CitizenRepository();

        if ("Penalisé".equals(status)) {
            List<String> statuses = new ArrayList<>();
            statuses.add("Court");
            statuses.add("Warrant");

            citizenRepository.fetchCitizensByStatuses(statuses, fetchedCitizens -> {
                progressBar.setVisibility(View.GONE);
                citizens.clear();
                citizens.addAll(fetchedCitizens);
                adapter.notifyDataSetChanged();
            }, () -> {
                progressBar.setVisibility(View.GONE);
            });
        } else {
            citizenRepository.fetchCitizensByStatus(status, fetchedCitizens -> {
                progressBar.setVisibility(View.GONE);
                citizens.clear();
                citizens.addAll(fetchedCitizens);
                adapter.notifyDataSetChanged();
            }, () -> {
                progressBar.setVisibility(View.GONE);
            });
        }
    }
}
