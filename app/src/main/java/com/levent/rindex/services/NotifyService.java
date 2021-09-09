package com.levent.rindex.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.levent.rindex.ccs.SavedSettings;

public class NotifyService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        Log.e("Token Income",s);
        SavedSettings.set("Notification_Token",s, this);
    }
}

