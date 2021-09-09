package com.levent.rindex.services;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.levent.rindex.ccs.SavedSettings;
import com.levent.rindex.constants.APIConstants;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.Post;
import com.levent.rindex.models.Response;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostService {
    public Call<ListResponse<Post>> getPostsByPlaceId(int placeId,int startIndex,int endIndex){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Post>> call = apiService.getPostsByPlaceId(placeId,startIndex,endIndex);
        return call;
    }

    public Call<ListResponse<Post>> getPostsByUserId(int userId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Post>> call = apiService.getAllPosts(userId);
        return call;
    }

    public Call<Response> add(Context context, Post post){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<Response> call = apiService.addPost(post, SavedSettings.get("Auth_Token",context));
        return call;
    }

    public Call<Response> delete(Context context, int postId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<Response> call = apiService.deletePost(postId, SavedSettings.get("Auth_Token",context));
        return call;
    }
}
