package com.levent.rindex.services;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.levent.rindex.ccs.SavedSettings;
import com.levent.rindex.constants.APIConstants;
import com.levent.rindex.models.Answer;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Response;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.Token;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnswerService {
    public Call<ListResponse<Answer>> getAnswersByQuestionId(int id){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<ListResponse<Answer>> call = apiService.getAnswersByQuestionId(id);
        return call;
    }

    public Call<Response> addAnswer(Context context, Answer answer){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<Response> call = apiService.addAnswer(answer, SavedSettings.get("Auth_Token",context));
        return call;
    }

    public Call<Response> toggleStarAnswer(Context context, int id){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIConstants.Url).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();

        APIService apiService = retrofit.create(APIService.class);
        Call<Response> call = apiService.toggleStarAnswer(id, SavedSettings.get("Auth_Token",context));
        return call;
    }
}
