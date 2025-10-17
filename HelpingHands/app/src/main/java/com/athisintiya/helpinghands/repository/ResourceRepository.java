package com.athisintiya.helpinghands.repository;

import android.util.Log;

import com.athisintiya.helpinghands.model.ResourceLocation;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ResourceRepository {
    private static final String TAG = "ResourceRepository";
    private static ResourceRepository instance;
    private FirebaseFirestore db;
    private final String COLLECTION_RESOURCES = "resources";

    private ResourceRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized ResourceRepository getInstance() {
        if (instance == null) {
            instance = new ResourceRepository();
        }
        return instance;
    }

    public void getAllResources(ResourceCallback callback) {
        db.collection(COLLECTION_RESOURCES)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ResourceLocation> resources = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        ResourceLocation resource = document.toObject(ResourceLocation.class);
                        if (resource != null && resource.isValid()) {
                            resource.setId(document.getId());
                            resources.add(resource);
                        }
                    }
                    callback.onSuccess(resources);
                    Log.d(TAG, "Loaded " + resources.size() + " resources");
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                    Log.e(TAG, "Error loading resources: " + e.getMessage());
                });
    }

    public void getResourcesByType(String type, ResourceCallback callback) {
        if (type == null || type.isEmpty()) {
            callback.onError("Invalid resource type");
            return;
        }

        db.collection(COLLECTION_RESOURCES)
                .whereEqualTo("type", type.toLowerCase())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ResourceLocation> resources = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        ResourceLocation resource = document.toObject(ResourceLocation.class);
                        if (resource != null && resource.isValid()) {
                            resource.setId(document.getId());
                            resources.add(resource);
                        }
                    }
                    callback.onSuccess(resources);
                    Log.d(TAG, "Loaded " + resources.size() + " resources of type: " + type);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                    Log.e(TAG, "Error loading resources by type: " + e.getMessage());
                });
    }

    public void searchResources(String query, ResourceCallback callback) {
        if (query == null || query.isEmpty()) {
            callback.onError("Invalid search query");
            return;
        }

        db.collection(COLLECTION_RESOURCES)
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ResourceLocation> resources = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        ResourceLocation resource = document.toObject(ResourceLocation.class);
                        if (resource != null && resource.isValid()) {
                            resource.setId(document.getId());
                            resources.add(resource);
                        }
                    }
                    callback.onSuccess(resources);
                    Log.d(TAG, "Found " + resources.size() + " resources for query: " + query);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                    Log.e(TAG, "Error searching resources: " + e.getMessage());
                });
    }

    public void addResource(ResourceLocation resource, ResourceCallback callback) {
        if (resource == null || !resource.isValid()) {
            callback.onError("Invalid resource data");
            return;
        }

        db.collection(COLLECTION_RESOURCES)
                .add(resource)
                .addOnSuccessListener(documentReference -> {
                    resource.setId(documentReference.getId());
                    callback.onSuccess(List.of(resource));
                    Log.d(TAG, "Resource added successfully: " + resource.getName());
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                    Log.e(TAG, "Error adding resource: " + e.getMessage());
                });
    }

    public interface ResourceCallback {
        void onSuccess(List<ResourceLocation> resources);
        void onError(String errorMessage);
    }
}