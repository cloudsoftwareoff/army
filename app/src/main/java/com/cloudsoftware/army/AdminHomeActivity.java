package com.cloudsoftware.army;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ListView listView;
    private CitizenAdapter adapter;
    private List<Citizen> citizens;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
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
                //  error
            }
        });
    }
}