package com.levent.rindex.ccs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SavedSettings {

    public static String get(@NonNull String key,@NonNull Context context){
        SharedPreferences preferences = context.getSharedPreferences("AppSaved",0);
        return preferences.getString(key,"");
    }

    public static void set(@NonNull String key,@NonNull String value,@NonNull Context context){
        SharedPreferences preferences = context.getSharedPreferences("AppSaved",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
