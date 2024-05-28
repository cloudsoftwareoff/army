package com.cloudsoftware.army;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
Button start=findViewById(R.id.start);


start.setOnClickListener(event->{
    Intent intent = new Intent(this, CitizenListActivity.class);
    startActivity(intent);
});
        // Initialize Firestore
       // db = FirebaseFirestore.getInstance();

        // Generate random data and add to Firestore
       // generateRandomCitizenData();
    }

 /*   private void generateRandomCitizenData() {
        CollectionReference citizensRef = db.collection("citizens");

        for (int i = 0; i < 50; i++) {
            Citizen citizen = generateRandomCitizen();
            citizensRef.document(citizen.getCin()).set(citizen);
        }
    }

    private Citizen generateRandomCitizen() {
        String[] maleFirstNames = {
                "Mohamed", "Ahmed", "Ali", "Hassan", "Khaled", "Omar", "Youssef", "Nabil", "Sami", "Anis",
                "Walid", "Fares", "Rami", "Mahdi", "Issam", "Tarek", "Raouf", "Nizar", "Karim", "Bilel",
                "Amine", "Chaker", "Ilyes", "Hatem", "Marwen", "Nader", "Saber", "Adel", "Fethi", "Zied"
        };
        String[] femaleFirstNames = {
                "Fatma", "Aicha", "Leila", "Sonia", "Mouna", "Hanen", "Amel", "Rania", "Meriem", "Noura",
                "Nadia", "Syrine", "Wafa", "Salma", "Souad", "Rim", "Nada", "Rihab", "Sahar", "Houda",
                "Imen", "Khaoula", "Hager", "Feriel", "Lina", "Ines", "Marwa", "Yasmine", "Samar", "Faten"
        };


        String[] lastNames = {
                "Ben Ali", "Trabelsi", "Bouazizi", "Ghannouchi", "Bouhlel", "Hammami", "Mabrouk", "Mansouri",
                "Jaziri", "Chouchane", "Saidi", "Chahed", "Koubaa", "Makni", "Gharbi", "Sassi", "Baccouche",
                "Chebbi", "Slim", "Hidoussi", "Belhadj", "Kacem", "Zouari", "Ben Ahmed", "Hamdi", "Jendoubi",
                "Jouini", "Khelil", "Messaoudi", "Ouertani"
        };

        String[] genders = {"Female"};

        Random random = new Random();

        String cin = String.format("%08d", 10000000 + random.nextInt(13000000));
        String firstName = femaleFirstNames[random.nextInt(femaleFirstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        String birthdate = String.format("%02d/%02d/%04d", random.nextInt(28) + 1, random.nextInt(12) + 1, 2000 + random.nextInt(11));
        String gender = genders[random.nextInt(genders.length)];

        return new Citizen(cin, firstName, lastName, birthdate, gender);
    }
    */

}
