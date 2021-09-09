package com.levent.rindex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.appbar.MaterialToolbar;
import com.levent.rindex.R;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Post;
import com.levent.rindex.models.User;
import com.levent.rindex.services.PostService;
import com.levent.rindex.ui.adapters.PostsAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPostsActivity extends AppCompatActivity {


    private User user;
    private ListView posts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        posts = findViewById(R.id.posts);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUser();
        getPosts();
    }

    private void getUser(){
        this.user = (User) getIntent().getSerializableExtra("User");
    }

    private void getPosts(){
        PostService postService = new PostService();
        postService.getPostsByUserId(this.user.id).enqueue(new Callback<ListResponse<Post>>() {
            @Override
            public void onResponse(Call<ListResponse<Post>> call, Response<ListResponse<Post>> response) {
                PostsAdapter postsAdapter = new PostsAdapter(UserPostsActivity.this,response.body().data,user,posts);
                posts.setAdapter(postsAdapter);
            }

            @Override
            public void onFailure(Call<ListResponse<Post>> call, Throwable t) {

            }
        });
    }
}