package com.cloudsoftware.army.db;

import com.cloudsoftware.army.models.Citizen;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    public void addCitizen(Citizen citizen, Runnable onSuccess, Runnable onFailure) {
        db.collection("citizens")
                .document(citizen.getCin())
                .set(citizen)
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(e -> onFailure.run());
    }

    public void updateCitizen(Citizen citizen, Runnable onSuccess, Runnable onFailure) {
        db.collection("citizens")
                .document(citizen.getCin())
                .set(citizen, SetOptions.merge())
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(e -> onFailure.run());
    }

    public void updateCitizenStatus(String userId, String newStatus, Runnable onSuccess, Runnable onFailure) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", newStatus);

        db.collection("citizens").document(userId)
                .update(data)
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(e -> onFailure.run());
    }

    public void fetchCitizenByUid(String uid, Consumer<Citizen> onSuccess, Runnable onFailure) {
        db.collection("citizens")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Citizen citizen = document.toObject(Citizen.class);
                            if (citizen != null) {
                                onSuccess.accept(citizen);
                                return;
                            }
                        }
                        // If no citizen found
                        onFailure.run();
                    } else {
                        onFailure.run();
                    }
                })
                .addOnFailureListener(e -> onFailure.run());
    }

    public void fetchCitizensByStatuses(List<String> statuses, Consumer<List<Citizen>> onSuccess, Runnable onFailure) {
        db.collection("citizens")
                .whereIn("status", statuses)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Citizen> fetchedCitizens = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Citizen citizen = document.toObject(Citizen.class);
                            fetchedCitizens.add(citizen);
                        }
                        onSuccess.accept(fetchedCitizens);
                    } else {
                        onFailure.run();
                    }
                });
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
