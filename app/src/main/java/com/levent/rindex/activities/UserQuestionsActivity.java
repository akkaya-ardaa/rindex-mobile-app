package com.levent.rindex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.UserManager;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.appbar.MaterialToolbar;
import com.levent.rindex.R;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Question;
import com.levent.rindex.models.User;
import com.levent.rindex.services.QuestionService;
import com.levent.rindex.ui.adapters.QuestionsAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserQuestionsActivity extends AppCompatActivity {

    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_questions);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUser();
        listQuestions();
    }

    private void getUser(){
        user = (User)getIntent().getSerializableExtra("User");
    }

    private void listQuestions(){
        QuestionService questionService = new QuestionService();
        questionService.getQuestionsByUserId(user.id).enqueue(new Callback<ListResponse<Question>>() {
            @Override
            public void onResponse(Call<ListResponse<Question>> call, Response<ListResponse<Question>> response) {
                if(response.isSuccessful()){
                    ListView listView = findViewById(R.id.questions);

                    QuestionsAdapter questionsAdapter = new QuestionsAdapter(UserQuestionsActivity.this,response.body().data,user);
                    listView.setAdapter(questionsAdapter);
                }
            }

            @Override
            public void onFailure(Call<ListResponse<Question>> call, Throwable t) {

            }
        });
    }
}