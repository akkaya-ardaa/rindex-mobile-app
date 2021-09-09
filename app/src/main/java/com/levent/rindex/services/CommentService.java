package com.levent.rindex.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.levent.rindex.ccs.SavedSettings;
import com.levent.rindex.constants.APIConstants;
import com.levent.rindex.models.Comment;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.Response;
import com.levent.rindex.models.SingleResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentService {
    public Call<ListResponse<Comment>> getComments(Context context, int placeId, boolean isNearby){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Comment>> call = null;
        if(!isNearby)
            call = apiService.getComments(placeId, SavedSettings.get("Auth_Token",context));
        else
            call = apiService.getNearbyComments(placeId,SavedSettings.get("Auth_Token",context));
        Log.e("Using token",SavedSettings.get("Auth_Token",context));
        return call;
    }

    public Call<SingleResponse<Comment>> getMyComment(Context context,int placeId, boolean isNearby){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<SingleResponse<Comment>> call = null;
        if(!isNearby)
            call = apiService.getMyComment(placeId,SavedSettings.get("Auth_Token",context));
        else
            call = apiService.getMyCommentNearby(placeId,SavedSettings.get("Auth_Token",context));
        Log.e("Using token",SavedSettings.get("Auth_Token",context));
        return call;
    }

    public Call<Response> uploadComment(Context context,Comment comment,int placeId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<Response> call = apiService.uploadComment(placeId,comment,SavedSettings.get("Auth_Token",context));
        return call;
    }

    public Call<Response> deleteComment(Context context,int commentId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<Response> call = apiService.deleteComment(commentId,SavedSettings.get("Auth_Token",context));
        return call;
    }
}
