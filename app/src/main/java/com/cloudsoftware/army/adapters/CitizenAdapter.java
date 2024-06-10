
package com.cloudsoftware.army.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.cloudsoftware.army.CitizenDetailActivity;
import com.cloudsoftware.army.R;
import com.cloudsoftware.army.Utility;
import com.cloudsoftware.army.models.Citizen;

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
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.citizen, parent, false);
            holder = new ViewHolder();
            holder.user_gender = convertView.findViewById(R.id.user_image);
            holder.nameView = convertView.findViewById(R.id.name);
            holder.cinView = convertView.findViewById(R.id.cin_num);
            holder.user_age = convertView.findViewById(R.id.user_age);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Citizen citizen = citizens.get(position);

        if (citizen.getGender().equals("Male")) {
            holder.user_gender.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.male));
        } else  {
            holder.user_gender.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.female));
        }

        holder.user_age.setText(Utility.calculateAge(citizen.getBirthdate()));
        holder.nameView.setText(citizen.getFirstName() + " " + citizen.getLastName());
        holder.cinView.setText(citizen.getCin());

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

    private static class ViewHolder {
        ImageView user_gender;
        TextView nameView;
        TextView cinView;
        TextView user_age;
    }
}
