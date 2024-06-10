package com.cloudsoftware.army;

import com.cloudsoftware.army.models.Citizen;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CitizenRepository {
    private FirebaseFirestore db;

    public CitizenRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public interface StatusUpdateCallback {
        void onStatusUpdateComplete();
        void onStatusUpdateFailed();
    }

    public void changeCitizenStatus(String userId, String status, Runnable onSuccess, Runnable onFailure) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", status);

        db.collection("citizens").document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Status updated successfully
                    System.out.println("Status updated successfully for userId: " + userId);
                    onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    System.err.println("Error updating status for userId: " + userId);
                    onFailure.run();
                });
    }

    public void setAllCitizensStatus(String status, List<Citizen> citizens, StatusUpdateCallback callback) {
        int totalCitizens = citizens.size();
        int[] successfulUpdates = {0};
        int[] failedUpdates = {0};

        for (Citizen citizen : citizens) {
            changeCitizenStatus(citizen.getCin(), status, () -> {
                successfulUpdates[0]++;
                if (successfulUpdates[0] + failedUpdates[0] == totalCitizens) {
                    callback.onStatusUpdateComplete();
                }
            }, () -> {
                failedUpdates[0]++;
                if (successfulUpdates[0] + failedUpdates[0] == totalCitizens) {
                    callback.onStatusUpdateFailed();
                }
            });
        }
    }

    public void fetchCitizensByStatus(String status, Consumer<List<Citizen>> onSuccess, Runnable onFailure) {
        db.collection("citizens")
                .whereEqualTo("status", status)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Citizen> citizens = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            querySnapshot.forEach(document -> {
                                Citizen citizen = document.toObject(Citizen.class);
                                citizens.add(citizen);
                            });
                        }
                        onSuccess.accept(citizens);
                    } else {
                        onFailure.run();
                    }
                })
                .addOnFailureListener(e -> onFailure.run());
    }
}
