package com.cloudsoftware.army;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CitizenAdapter extends BaseAdapter {
    private Context context;
    private List<Citizen> citizens;
    private LayoutInflater inflater;

    public CitizenAdapter(Context context, List<Citizen> citizens) {
        this.context = context;
        this.citizens = citizens;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return citizens.size();
    }

    @Override
    public Object getItem(int position) {
        return citizens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.citizen, parent, false);
        }

        Citizen citizen = citizens.get(position);

        TextView nameView = convertView.findViewById(R.id.name);
        TextView cinView = convertView.findViewById(R.id.cin_num);

        nameView.setText(citizen.getFirstName() + " " + citizen.getLastName());
        cinView.setText(citizen.getCin());
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CitizenDetailActivity.class);
            intent.putExtra("CITIZEN_CIN", citizen.getCin());
            intent.putExtra("CITIZEN_NAME", citizen.getFirstName() + " " + citizen.getLastName());
            intent.putExtra("CITIZEN_BIRTHDATE", citizen.getBirthdate());
            intent.putExtra("CITIZEN_GENDER", citizen.getGender());
            context.startActivity(intent);
        });

        return convertView;
    }
}
