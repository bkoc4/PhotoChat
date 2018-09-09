package com.photo.advanced.photochat.helper;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DataHelper {

    private static DataHelper instance;
    FirebaseAuth fbUserAuth;
    FirebaseFirestore fbFireStore;
    StorageReference storageRef;

    public static DataHelper getInstance() {
        if (instance == null) {
            instance = new DataHelper();
        }
        return instance;
    }

    public DataHelper() {
        fbFireStore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        fbFireStore.setFirestoreSettings(settings);

        fbUserAuth = FirebaseAuth.getInstance();

        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public FirebaseAuth getUserAuth() {
        return fbUserAuth;
    }

    public FirebaseFirestore getFireStore() {
        return fbFireStore;
    }

    public CollectionReference getUserCollection () {
        return getFireStore().collection("Users");
    }

    public StorageReference getStorage(){
        return storageRef;
    }
}
