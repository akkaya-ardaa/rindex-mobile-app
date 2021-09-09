package com.levent.rindex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.appbar.MaterialToolbar;
import com.levent.rindex.R;
import com.levent.rindex.models.Comment;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.User;
import com.levent.rindex.services.AccountService;
import com.levent.rindex.ui.adapters.CommentsAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCommentsActivity extends AppCompatActivity {

    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comments);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        getUser();
        getComments();

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUser(){
        user = (User)getIntent().getSerializableExtra("User");
    }

    private void getComments(){
        AccountService accountService = new AccountService();
        accountService.getAllComments(user.id).enqueue(new Callback<ListResponse<Comment>>() {
            @Override
            public void onResponse(Call<ListResponse<Comment>> call, Response<ListResponse<Comment>> response) {
                if(response.body() == null){
                    return;
                }
                CommentsAdapter commentsAdapter = new CommentsAdapter(UserCommentsActivity.this,response.body().data,user);
                ListView commentsList = findViewById(R.id.comments_list);
                commentsList.setAdapter(commentsAdapter);
            }

            @Override
            public void onFailure(Call<ListResponse<Comment>> call, Throwable t) {

            }
        });
    }
}