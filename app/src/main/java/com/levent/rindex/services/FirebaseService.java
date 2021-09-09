package com.levent.rindex.services;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseService {
    private StorageReference storageReference;
    private FirebaseStorage storage;

    public FirebaseService(Context context) {
        FirebaseApp.initializeApp(context);
        this.storage = FirebaseStorage.getInstance();
        this.storageReference = storage.getReference();
    }

    public UploadTask uploadImage(byte[] imageBytes, String path){
        StorageReference reference = this.storageReference.child(path);
        return reference.putBytes(imageBytes);
    }

    public Task<Uri> getDownloadUrl(String path){
        StorageReference reference = this.storageReference.child(path);
        return reference.getDownloadUrl();
    }
}
