package com.athisintiya.helpinghands.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.athisintiya.helpinghands.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private static UserRepository instance;
    private User currentUser;
    private FirebaseFirestore db;
    private final String COLLECTION_USERS = "users";

    private UserRepository(Context context) {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context);
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        Log.d(TAG, "Current user set: " + (user != null ? user.getEmail() : "null"));
    }

    // Updated updateUser method with callback
    public void updateUser(User user, UserUpdateCallback callback) {
        if (user == null || TextUtils.isEmpty(user.getId())) {
            Log.e(TAG, "Cannot update user: invalid user data");
            if (callback != null) {
                callback.onFailure("Invalid user data");
            }
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User updated successfully: " + user.getEmail());
                    setCurrentUser(user); // Update cached user
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user: " + e.getMessage());
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    // Keep the original updateUser for backward compatibility
    public void updateUser(User user) {
        updateUser(user, null);
    }

    public Task<DocumentSnapshot> getUserById(String userId) {
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "Invalid user ID provided");
            return null;
        }

        return db.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            setCurrentUser(user);
                            Log.d(TAG, "User loaded successfully: " + user.getEmail());
                        }
                    } else {
                        Log.w(TAG, "User document does not exist: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user: " + e.getMessage());
                });
    }

    public boolean recordDonation(String userId) {
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "Invalid user ID for donation recording");
            return false;
        }

        try {
            db.collection(COLLECTION_USERS)
                    .document(userId)
                    .update("donationCount", FieldValue.increment(1))
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Donation recorded successfully for user: " + userId);
                        // Update local user object
                        if (currentUser != null && currentUser.getId().equals(userId)) {
                            currentUser.incrementDonationCount();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error recording donation: " + e.getMessage());
                    });
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Exception recording donation: " + e.getMessage());
            return false;
        }
    }

    public void createUser(User user) {
        if (user != null && user.getId() != null && !user.getId().isEmpty()) {
            db.collection(COLLECTION_USERS)
                    .document(user.getId())
                    .set(user)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User created successfully: " + user.getEmail());
                        setCurrentUser(user);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating user: " + e.getMessage());
                    });
        } else {
            Log.e(TAG, "Cannot create user: invalid user data");
        }
    }

    public void deleteUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "Invalid user ID for deletion");
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User deleted successfully: " + userId);
                    if (currentUser != null && currentUser.getId().equals(userId)) {
                        setCurrentUser(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting user: " + e.getMessage());
                });
    }

    public boolean isUserLoggedIn() {
        return currentUser != null && currentUser.isValid();
    }

    public void clearCurrentUser() {
        this.currentUser = null;
        Log.d(TAG, "Current user cleared");
    }

    // Updated callback interface with failure method
    public interface UserUpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }
}