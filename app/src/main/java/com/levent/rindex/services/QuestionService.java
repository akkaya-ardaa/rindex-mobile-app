package com.levent.rindex.services;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.levent.rindex.ccs.SavedSettings;
import com.levent.rindex.constants.APIConstants;
import com.levent.rindex.models.Comment;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Post;
import com.levent.rindex.models.Question;
import com.levent.rindex.models.Response;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionService {
    public Call<ListResponse<Question>> getQuestionsByPlaceId(int placeId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Question>> call = apiService.getQuestionsByPlaceId(placeId);
        return call;
    }

    public Call<ListResponse<Question>> getQuestionsByUserId(int userId){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Question>> call = apiService.getQuestionsByUserId(userId);
        return call;
    }

    public Call<Response> addQuestion(@NonNull Context context, Question question){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<Response> call = apiService.addQuestion(question, SavedSettings.get("Auth_Token",context));
        return call;
    }

    public Call<Response> deleteQuestion(@NonNull Context context, int id){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<Response> call = apiService.deleteQuestion(id, SavedSettings.get("Auth_Token",context));
        return call;
    }
}
