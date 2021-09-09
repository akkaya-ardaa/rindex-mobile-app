package com.levent.rindex.services;

import com.google.gson.GsonBuilder;
import com.levent.rindex.constants.APIConstants;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.NearbyPlace;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdditionalInformationService<T> {
    public Call<T> getInformation(int nearbyPlaceId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<T> call = (Call<T>) apiService.getNearbyPlaceInformationRestaurant(nearbyPlaceId);
        return call;
    }
}
