package com.levent.rindex.services;

import com.google.gson.GsonBuilder;
import com.levent.rindex.constants.APIConstants;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.Token;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceService {
    public Call<ListResponse<Place>> getPlacesByProvinceAndDistrict(String province,String district){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Place>> call = apiService.getPlacesByProvinceAndDistrict(province,district);
        return call;
    }

    public Call<ListResponse<Place>> getPlacesByProvince(String province){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Place>> call = apiService.getPlacesByProvince(province);
        return call;
    }

    public Call<ListResponse<Place>> getPlacesByIndex(int startIndex,int endIndex){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Place>> call = apiService.getPlacesByIndex(startIndex,endIndex);
        return call;
    }

    public Call<ListResponse<Place>> search(String q){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Place>> call = apiService.searchPlaces(q);
        return call;
    }

    public Call<SingleResponse<Place>> getPlaceById(int id){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<SingleResponse<Place>> call = apiService.getPlaceById(id);
        return call;
    }

    public Call<Float> getRating(int placeId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<Float> call = apiService.getRating(placeId);
        return call;
    }
}
