package com.cloudsoftware.army.db;

import com.cloudsoftware.army.models.ArmyRecruitingRound;
import com.cloudsoftware.army.models.Citizen;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecruitingManager {
    private FirebaseFirestore db;
    private CollectionReference roundsRef;

    public RecruitingManager() {
        db = FirebaseFirestore.getInstance();
        roundsRef = db.collection("rounds");
    }

    // Method to add a new recruiting round
    public void addRound(ArmyRecruitingRound round, OnRoundAddedListener listener) {
        DocumentReference refId = roundsRef.document();
        round.setId(refId.getId());
        List<String> citizenIds = new ArrayList<>();
        CitizenRepository citizenRepository = new CitizenRepository();

        citizenRepository.fetchCitizensByStatus("Eligible", citizens -> {
            for (Citizen citizen : citizens) {
                if (citizen.isEighteenOrOlder()) {
                    citizenIds.add(citizen.getCin());
                }
            }
            round.setCandidatesId(citizenIds);

            // Adding round to Firestore
            refId.set(round)
                    .addOnSuccessListener(documentReference -> {
                        listener.onRoundAdded(true, round);
                    })
                    .addOnFailureListener(e -> listener.onRoundAdded(false, null));
        },()->{

        });
    }
    // Add the updateRound method in RecruitingManager
    public void updateRound(ArmyRecruitingRound round, OnRoundAddedListener listener) {
        roundsRef.document(round.getId()).set(round)
                .addOnSuccessListener(aVoid -> listener.onRoundAdded(true, round))
                .addOnFailureListener(e -> listener.onRoundAdded(false, null));
    }

    // Method to get all recruiting rounds
    public void getAllRounds(OnRoundsFetchedListener listener) {
        roundsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ArmyRecruitingRound> rounds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ArmyRecruitingRound round = document.toObject(ArmyRecruitingRound.class);
                            round.setId(document.getId());
                            rounds.add(round);
                        }
                        listener.onRoundsFetched(true, rounds);
                    } else {
                        listener.onRoundsFetched(false, null);
                    }
                });
    }

    // Method to get a specific recruiting round by ID
    public void getRoundById(String id, OnRoundFetchedListener listener) {
        roundsRef.document(id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArmyRecruitingRound round = task.getResult().toObject(ArmyRecruitingRound.class);
                        if (round != null) {
                            round.setId(task.getResult().getId());
                            listener.onRoundFetched(true, round);
                        } else {
                            listener.onRoundFetched(false, null);
                        }
                    } else {
                        listener.onRoundFetched(false, null);
                    }
                });
    }
    // Method to get the current recruiting round
    public void getCurrentRound(OnRoundFetchedListener listener) {
        getAllRounds((isSuccess, rounds) -> {
            if (isSuccess && rounds != null) {
                for (ArmyRecruitingRound round : rounds) {
                    if (round.hasNotEnded()) {
                        listener.onRoundFetched(true, round);
                        return;
                    }
                }
                // No active round found
                listener.onRoundFetched(false, null);
            } else {
                listener.onRoundFetched(false, null);
            }
        });
    }

    // Listener interfaces
    public interface OnRoundAddedListener {
        void onRoundAdded(boolean isSuccess, ArmyRecruitingRound round);
    }

    public interface OnRoundsFetchedListener {
        void onRoundsFetched(boolean isSuccess, List<ArmyRecruitingRound> rounds);
    }

    public interface OnRoundFetchedListener {
        void onRoundFetched(boolean isSuccess, ArmyRecruitingRound round);
    }
}
