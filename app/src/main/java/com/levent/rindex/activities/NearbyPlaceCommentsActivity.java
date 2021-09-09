package com.levent.rindex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.levent.rindex.R;
import com.levent.rindex.models.Comment;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.NearbyPlace;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.User;
import com.levent.rindex.services.CommentService;
import com.levent.rindex.ui.adapters.CommentsAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyPlaceCommentsActivity extends AppCompatActivity {

    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_place_comments);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        currentUser = (User) getIntent().getSerializableExtra("User");

        getCommentsAndList();
        getMyComment();
    }

    private void getMyComment(){
        CommentService commentService = new CommentService();
        int placeId = getIntent().getIntExtra("placeId",0);
        commentService.getMyComment(this,placeId,true).enqueue(new Callback<SingleResponse<Comment>>() {
            @Override
            public void onResponse(Call<SingleResponse<Comment>> call, Response<SingleResponse<Comment>> response) {
                Comment comment = response.body().data;

                RatingBar myRating = findViewById(R.id.my_rating);
                TextView myFirstLastName = findViewById(R.id.my_first_last_name);
                EditText myComment = findViewById(R.id.my_comment);
                Button mySend = findViewById(R.id.my_send);

                myFirstLastName.setText(currentUser.firstName + " " + currentUser.lastName);

                myComment.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if(myComment.getText().length() >= 500){
                            Toast.makeText(NearbyPlaceCommentsActivity.this,R.string.character_limit_exceeded,Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                if(comment != null){
                    myComment.setText(comment.text);
                    myRating.setRating(comment.star);
                }

                mySend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Comment newComment = new Comment();
                        newComment.nearbyPlaceId = placeId;
                        newComment.star = Math.round(myRating.getRating());
                        newComment.text = myComment.getText().toString().trim();

                        commentService.uploadComment(NearbyPlaceCommentsActivity.this,newComment,placeId).enqueue(new Callback<com.levent.rindex.models.Response>() {
                            @Override
                            public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                                if(response.isSuccessful()){
                                    Snackbar.make(v,response.body().message,Snackbar.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<com.levent.rindex.models.Response> call, Throwable t) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<SingleResponse<Comment>> call, Throwable t) {

            }
        });
    }

    private void getCommentsAndList(){
        CommentService commentService = new CommentService();
        int placeId = getIntent().getIntExtra("placeId",0);
        commentService.getComments(NearbyPlaceCommentsActivity.this,placeId,true).enqueue(new Callback<ListResponse<Comment>>() {
            @Override
            public void onResponse(Call<ListResponse<Comment>> call, Response<ListResponse<Comment>> response) {
                List<Comment> comments = response.body().data;
                Log.e("WARN!!!",String.valueOf(comments.size()));
                Log.e("WARN22!!!",String.valueOf(placeId));
                ListView commentsList = findViewById(R.id.comments_list);
                CommentsAdapter commentsAdapter = new CommentsAdapter(NearbyPlaceCommentsActivity.this,comments,currentUser);
                commentsList.setAdapter(commentsAdapter);
            }

            @Override
            public void onFailure(Call<ListResponse<Comment>> call, Throwable t) {
                Toast.makeText(NearbyPlaceCommentsActivity.this,"Failed!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}