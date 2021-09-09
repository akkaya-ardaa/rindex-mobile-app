package com.levent.rindex.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.levent.rindex.ccs.SavedSettings;
import com.levent.rindex.models.TravelBookItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TravelBookService {

    public static void removeTravelBookItem(Context context,int placeId){
        Gson gson = new Gson();
        String travelBookItemsJson = SavedSettings.get("travelBookItems",context);
        TravelBookItem[] travelBookItems = gson.fromJson(travelBookItemsJson,TravelBookItem[].class);
        List<TravelBookItem> list = Arrays.asList(travelBookItems);
        TravelBookItem[] dump = new TravelBookItem[travelBookItems.length - 1];

        for(int i = 0; i < list.size(); i++){
            if(list.get(i).placeId == placeId){
                Log.e("Found On",list.get(i).title);


                for(int t = 0; t < travelBookItems.length; t++){
                    if(t < i){
                        dump[t] = travelBookItems[t];
                    }
                    else if(t == i){

                    }
                    else{
                        dump[t-1] = travelBookItems[t];
                    }
                }

            }
        }

        SavedSettings.set("travelBookItems",gson.toJson(dump),context);
    }

    public static void addTravelBookItem(Context context,TravelBookItem item){
        Gson gson = new Gson();
        String travelBookItemsJson = SavedSettings.get("travelBookItems",context);
        TravelBookItem[] travelBookItems = gson.fromJson(travelBookItemsJson,TravelBookItem[].class);

        if(travelBookItems == null){
            createTravelBook(context);
            travelBookItems = gson.fromJson(travelBookItemsJson,TravelBookItem[].class);
        }

        TravelBookItem[] newTravelBookItems = new TravelBookItem[travelBookItems.length + 1];

        for (int i = 0;i < travelBookItems.length; i++){
            newTravelBookItems[i] = travelBookItems[i];
        }
        newTravelBookItems[travelBookItems.length] = item;
        SavedSettings.set("travelBookItems",gson.toJson(newTravelBookItems),context);
    }

    private static void createTravelBook(Context context){
        Gson gson = new Gson();
        TravelBookItem[] empty = new TravelBookItem[0];
        SavedSettings.set("travelBookItems",gson.toJson(empty),context);
    }

    public static final TravelBookItem[] getTravelBookItems(Context context){
        String travelBookItemsJson = SavedSettings.get("travelBookItems",context);

        if(travelBookItemsJson == ""){
            createTravelBook(context);
            travelBookItemsJson = SavedSettings.get("travelBookItems",context);
        }

        Gson gson = new Gson();

        TravelBookItem[] travelBookItems = gson.fromJson(travelBookItemsJson,TravelBookItem[].class);
        return travelBookItems;
    }
}
