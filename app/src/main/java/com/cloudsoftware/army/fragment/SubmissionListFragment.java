package com.cloudsoftware.army.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloudsoftware.army.R;
import com.cloudsoftware.army.adapters.SubmissionAdapter;
import com.cloudsoftware.army.SubmissionDetailActivity;
import com.cloudsoftware.army.models.Submission;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SubmissionListFragment extends Fragment {

    private static final String ARG_SUBMISSION_STATUS = "submission_type";
    private ListView listView;
    private TextView noSubmissionsText;
    private SubmissionAdapter adapter;
    private List<Submission> submissions;
    private FirebaseFirestore db;
    private String submissionType;

    public static SubmissionListFragment newInstance(String submissionstatus) {
        SubmissionListFragment fragment = new SubmissionListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SUBMISSION_STATUS, submissionstatus);
        fragment.setArguments(args);
        return fragment;
    }

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

        if (getArguments() != null) {
            submissionType = getArguments().getString(ARG_SUBMISSION_STATUS);
        }

        loadSubmissions();
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Submission clickedSubmission = submissions.get(position);
            Intent intent = new Intent(getActivity(), SubmissionDetailActivity.class);
            intent.putExtra("SUBMISSION", clickedSubmission);
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSubmissions();
    }

    private void loadSubmissions() {
        db.collection("submissions")
                .whereEqualTo("status", submissionType)
                .get()
                .addOnCompleteListener(task -> {
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
