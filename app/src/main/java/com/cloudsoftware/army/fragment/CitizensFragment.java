package com.cloudsoftware.army.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloudsoftware.army.R;
import com.cloudsoftware.army.adapters.CitizenAdapter;
import com.cloudsoftware.army.adapters.OnCitizenLongClickListener;
import com.cloudsoftware.army.db.CitizenRepository;
import com.cloudsoftware.army.models.Citizen;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CitizensFragment extends Fragment implements OnCitizenLongClickListener {

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
        adapter = new CitizenAdapter(getContext(), citizens, this);
        listView.setAdapter(adapter);

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

    @Override
    public void onCitizenLongClick(Citizen citizen) {
        showChangeStatusDialog(citizen);
    }

    public void showChangeStatusDialog(Citizen citizen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Status");

        String[] statuses = {"Eligible", "Court", "Warrant", "Exempt"};

        builder.setItems(statuses, (dialog, which) -> {
            String selectedStatus = statuses[which];
            progressBar.setVisibility(View.VISIBLE);

            CitizenRepository citizenRepository = new CitizenRepository();
            citizenRepository.updateCitizenStatus(citizen.getCin(), selectedStatus, () -> {
                Toast.makeText(getContext(), "Status updated successfully", Toast.LENGTH_SHORT).show();
                loadCitizens();
            }, () -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
