package com.cloudsoftware.army;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SubmissionAdapter extends ArrayAdapter<Submission> {

    private Context context;
    private List<Submission> submissions;

    public SubmissionAdapter(@NonNull Context context, @NonNull List<Submission> submissions) {
        super(context, 0, submissions);
        this.context = context;
        this.submissions = submissions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_submission, parent, false);
        }

        Submission submission = submissions.get(position);

        TextView userIdView = convertView.findViewById(R.id.user_id);
        TextView submissionDateView = convertView.findViewById(R.id.submission_date);
        TextView submissionTypeView = convertView.findViewById(R.id.submission_type);
        TextView statusView = convertView.findViewById(R.id.status);

        userIdView.setText("User ID: " + submission.getUserId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        submissionDateView.setText("Date: " + dateFormat.format(submission.getSubmissionDate()));
        submissionTypeView.setText("Type: " + submission.getSubmissionType());
        statusView.setText("Status: " + submission.getStatus());

        return convertView;
    }
}
