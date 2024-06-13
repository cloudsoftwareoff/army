package com.cloudsoftware.army.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudsoftware.army.R;
import com.cloudsoftware.army.adapters.RoundAdapter;
import com.cloudsoftware.army.db.RecruitingManager;
import com.cloudsoftware.army.models.ArmyRecruitingRound;

import java.util.ArrayList;
import java.util.List;

public class RoundListFragment extends Fragment {

    private RecyclerView recyclerView;
    private RoundAdapter adapter;
    private List<ArmyRecruitingRound> roundList;
    private ProgressBar progressBar;
    private RecruitingManager recruitingManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_round_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        roundList = new ArrayList<>();
        adapter = new RoundAdapter(roundList);
        recruitingManager = new RecruitingManager();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadRounds();

        return view;
    }

    private void loadRounds() {
        progressBar.setVisibility(View.VISIBLE);
        recruitingManager.getAllRounds((isSuccess, rounds) -> {
            progressBar.setVisibility(View.GONE);
            if (isSuccess) {
                roundList.clear();
                roundList.addAll(rounds);
                adapter.notifyDataSetChanged();
            } else {
                // Handle the error
            }

        });
    }
}

