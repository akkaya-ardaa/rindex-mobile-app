package com.levent.rindex.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RealtimeDatabaseUtil {

    public DatabaseReference database;

    public RealtimeDatabaseUtil(String key) {
        database = FirebaseDatabase.getInstance().getReference(key);
    }

    public void addData(Object object){
        database.push().setValue(object);
    }
}
