package com.cloudsoftware.army;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudsoftware.army.adapters.CitizenAdapter;
import com.cloudsoftware.army.models.Citizen;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CitizenListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView listView;
    private CitizenAdapter adapter;
    private List<Citizen> citizens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_list);

        listView = findViewById(R.id.citizen);
        citizens = new ArrayList<>();
        adapter = new CitizenAdapter(this, citizens);
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadCitizens();
    }

    public void loadCitizens() {
        CollectionReference citizensRef = db.collection("citizens");
        citizensRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Citizen citizen = document.toObject(Citizen.class);
                    if(citizen.isEighteenOrOlder()) {
                        citizens.add(citizen);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                // Handle the error
            }
        });
    }
}
