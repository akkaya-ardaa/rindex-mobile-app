package com.levent.rindex.services;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.levent.rindex.ccs.SavedSettings;
import com.levent.rindex.constants.APIConstants;
import com.levent.rindex.models.Comment;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Post;
import com.levent.rindex.models.Response;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.Token;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountService {
    public Call<SingleResponse<Token>> login(String mail,String password,String notificationToken){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<SingleResponse<Token>> call = apiService.login(mail,password,notificationToken);
        return call;
    }

    public Call<ListResponse<Comment>> getAllComments(int userId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();
        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Comment>> call = apiService.getAllComments(userId);
        return call;
    }

    public Call<ListResponse<Post>> getAllPosts(int userId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();
        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Post>> call = apiService.getAllPosts(userId);
        return call;
    }

    public  Call<Response> updateNameSurname(Context context, String name, String surname){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();
        APIService apiService = retrofit.create(APIService.class);
        Call<Response> call = apiService.updateNameSurname(name,surname, SavedSettings.get("Auth_Token",context));
        return call;
    }

    public  Call<Response> updatePhoto(Context context, String path){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();
        APIService apiService = retrofit.create(APIService.class);
        Call<Response> call = apiService.updatePhoto(path, SavedSettings.get("Auth_Token",context));
        return call;
    }

    public  Call<Response> updateBiography(Context context, String biography){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();
        APIService apiService = retrofit.create(APIService.class);
        Call<Response> call = apiService.updateBiography(biography, SavedSettings.get("Auth_Token",context));
        return call;
    }
}
