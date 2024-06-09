package com.cloudsoftware.army;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SubmissionListFragment extends Fragment {

    private ListView listView;
    private TextView noSubmissionsText;
    private SubmissionAdapter adapter;
    private List<Submission> submissions;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_submission_list, container, false);
        listView = view.findViewById(R.id.submission_list_view);
        noSubmissionsText = view.findViewById(R.id.no_submissions_text);
        submissions = new ArrayList<>();
        adapter = new SubmissionAdapter(getContext(), submissions);
        listView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        loadSubmissions();
        return view;
    }

    private void loadSubmissions() {
        db.collection("submissions").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                submissions.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Submission submission = document.toObject(Submission.class);
                    submissions.add(submission);
                }
                if (submissions.isEmpty()) {
                    noSubmissionsText.setVisibility(View.VISIBLE);
                } else {
                    noSubmissionsText.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            } else {
                noSubmissionsText.setVisibility(View.VISIBLE);
                noSubmissionsText.setText("Error loading submissions.");
            }
        });
    }
}
