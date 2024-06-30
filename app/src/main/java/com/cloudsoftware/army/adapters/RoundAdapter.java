package com.cloudsoftware.army.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudsoftware.army.R;
import com.cloudsoftware.army.models.ArmyRecruitingRound;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RoundAdapter extends RecyclerView.Adapter<RoundAdapter.RoundViewHolder> {

    private List<ArmyRecruitingRound> rounds;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private OnItemClickListener onItemClickListener;

    public RoundAdapter(List<ArmyRecruitingRound> rounds, OnItemClickListener onItemClickListener) {
        this.rounds = rounds;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_round, parent, false);
        return new RoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoundViewHolder holder, int position) {
        ArmyRecruitingRound round = rounds.get(position);
        holder.roundName.setText(round.getRoundName());

        try {
            Date start = dateFormat.parse(round.getStartDate());
            Date end = dateFormat.parse(round.getEndDate());
            holder.startDate.setText(holder.itemView.getContext().getString(R.string.start_date, dateFormat.format(start)));
            holder.endDate.setText(holder.itemView.getContext().getString(R.string.end_date, dateFormat.format(end)));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.startDate.setText(holder.itemView.getContext().getString(R.string.start_date, round.getStartDate()));
            holder.endDate.setText(holder.itemView.getContext().getString(R.string.end_date, round.getEndDate()));
        }

        holder.location.setText(holder.itemView.getContext().getString(R.string.location, round.getLocation()));
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(round));
    }


    @Override
    public int getItemCount() {
        return rounds.size();
    }

    static class RoundViewHolder extends RecyclerView.ViewHolder {
        TextView roundName, startDate, endDate, location;

        public RoundViewHolder(@NonNull View itemView) {
            super(itemView);
            roundName = itemView.findViewById(R.id.round_name);
            startDate = itemView.findViewById(R.id.start_date);
            endDate = itemView.findViewById(R.id.end_date);
            location = itemView.findViewById(R.id.location);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ArmyRecruitingRound round);
    }
}
