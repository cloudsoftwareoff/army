package com.cloudsoftware.army;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloudsoftware.army.adapters.CitizenAdapter;
import com.cloudsoftware.army.models.Citizen;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllCitizensFragment extends Fragment {
    private ListView listView;
    private CitizenAdapter adapter;
    private List<Citizen> citizens;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_citizens, container, false);
        listView = view.findViewById(R.id.citizen_list_view);
        progressBar = view.findViewById(R.id.progress_bar);
        citizens = new ArrayList<>();
        adapter = new CitizenAdapter(getContext(), citizens);
        listView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        loadCitizens();
        return view;
    }

    private void loadCitizens() {
        progressBar.setVisibility(View.VISIBLE);

        CitizenRepository citizenRepository = new CitizenRepository();
        citizenRepository.fetchCitizensByStatus("Eligible", fetchedCitizens -> {

            progressBar.setVisibility(View.GONE);

            citizens.addAll(fetchedCitizens);
            adapter.notifyDataSetChanged();
        }, () -> {

            progressBar.setVisibility(View.GONE);

        });
    }
}
