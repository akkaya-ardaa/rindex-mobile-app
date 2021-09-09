package com.levent.rindex.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.levent.rindex.ccs.SavedSettings;
import com.levent.rindex.constants.APIConstants;
import com.levent.rindex.models.Response;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.ViewCount;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewCountService {
    public Call<SingleResponse<ViewCount>> getPlaceViewCount(int placeId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<SingleResponse<ViewCount>> call = apiService.getPlaceViewCount(placeId);
        return call;
    }

    public boolean increasePlaceViewCount(Context context, int placeId){
        if(!isIncreased(context,placeId)) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();
            APIService apiService = retrofit.create(APIService.class);
            Call<Response> call = apiService.increasePlaceViewCount(placeId);
            call.enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {

                }
            });
            setIncreased(context,placeId);
            return true;
        }
        else{
            Log.e("Already increased!",String.valueOf(placeId));
            return false;
        }
    }

    private void setIncreased(Context context,int placeId){
        Gson gson = new Gson();

        String increasedViewCountJson = SavedSettings.get("viewcount_increased",context);

        if(increasedViewCountJson == ""){
            createViewCountArray(context);
            increasedViewCountJson = SavedSettings.get("viewcount_increased",context);
        }

        int[] list = gson.fromJson(increasedViewCountJson,int[].class);
        int[] dump = new int[list.length + 1];
        for (int i = 0; i < list.length; i++){
            dump[i] = list[i];
        }
        dump[list.length] = placeId;
        String newJson = gson.toJson(dump);

        SavedSettings.set("viewcount_increased",newJson,context);
    }

    private boolean isIncreased(Context context,int placeId){
        Gson gson = new Gson();

        String increasedViewCountJson = SavedSettings.get("viewcount_increased",context);

        if(increasedViewCountJson == ""){
            createViewCountArray(context);
            increasedViewCountJson = SavedSettings.get("viewcount_increased",context);
        }

        int[] list = gson.fromJson(increasedViewCountJson,int[].class);

        for(int i = 0; i < list.length; i++){
            int current = list[i];
            if(current == placeId){
                return true;
            }
        }
        return false;
    }

    private void createViewCountArray(Context context){
        Gson gson = new Gson();
        int[] list = new int[0];
        SavedSettings.set("viewcount_increased",gson.toJson(list),context);
    }
}
