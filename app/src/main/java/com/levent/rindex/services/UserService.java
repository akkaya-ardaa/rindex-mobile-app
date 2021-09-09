package com.levent.rindex.services;

import com.google.gson.GsonBuilder;
import com.levent.rindex.constants.APIConstants;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.User;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserService {
    public Call<SingleResponse<User>> getPublicUserInfo(int userId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<SingleResponse<User>> call = apiService.getPublicUserInfo(userId);
        return call;
    }
}
