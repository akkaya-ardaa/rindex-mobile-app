package com.levent.rindex.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.levent.rindex.R;
import com.levent.rindex.models.Answer;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Question;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.User;
import com.levent.rindex.services.AnswerService;
import com.levent.rindex.services.UserService;
import com.levent.rindex.ui.adapters.AnswersAdapter;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswersActivity extends AppCompatActivity {

    private Question question;
    private String body;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        FloatingActionButton addAnswer = findViewById(R.id.add_answer);

        addAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                body = null;
                View dialogRoot = getLayoutInflater().inflate(R.layout.add_answer_dialog,null);

                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AnswersActivity.this);
                alertDialogBuilder.setView(dialogRoot);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.setCancelable(false);

                Button close = dialogRoot.findViewById(R.id.close);
                Button confirm = dialogRoot.findViewById(R.id.confirm);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(body == null){
                            Snackbar.make(dialogRoot,R.string.please_enter_body,Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        MaterialAlertDialogBuilder alertDialogBuilder1 = new MaterialAlertDialogBuilder(AnswersActivity.this);
                        alertDialogBuilder1.setView(getLayoutInflater().inflate(R.layout.loading_layout,null));
                        AlertDialog dialog = alertDialogBuilder1.create();
                        dialog.setCancelable(false);
                        dialog.show();

                        AnswerService answerService = new AnswerService();
                        Answer answer = new Answer();
                        answer.body = body;
                        answer.questionId = question.id;
                        answerService.addAnswer(AnswersActivity.this,answer).enqueue(new Callback<com.levent.rindex.models.Response>() {
                            @Override
                            public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                                if(response.isSuccessful()){
                                    dialog.dismiss();
                                    alertDialog.dismiss();
                                    getAnswers();
                                }
                            }

                            @Override
                            public void onFailure(Call<com.levent.rindex.models.Response> call, Throwable t) {
                                dialog.dismiss();
                            }
                        });
                    }
                });

                MaterialCardView bodyOfAnswer = dialogRoot.findViewById(R.id.body_card);
                bodyOfAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View fieldRoot = getLayoutInflater().inflate(R.layout.add_answer_body_layout,null);

                        MaterialAlertDialogBuilder alertDialogBuilder1 = new MaterialAlertDialogBuilder(AnswersActivity.this);
                        alertDialogBuilder1.setView(fieldRoot);
                        AlertDialog alertDialog1 = alertDialogBuilder1.create();
                        alertDialog1.show();
                        EditText bodyField = fieldRoot.findViewById(R.id.body_field);

                        if(body != null)
                            bodyField.setText(body);

                        alertDialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                body = bodyField.getText().toString();
                            }
                        });
                    }
                });
            }
        });

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUser();
        getQuestion();
        getAnswers();
    }

    private String convertToUniversalTime(int n){
        if(n <= 9){
            return "0"+n;
        }
        else{
            return String.valueOf(n);
        }
    }

    private void getUser(){
        user = (User)getIntent().getSerializableExtra("User");
    }

    private void getQuestion(){
        question = (Question)getIntent().getSerializableExtra("Question");

        TextView body = findViewById(R.id.body);
        TextView username = findViewById(R.id.username);
        ImageView profilePhoto = findViewById(R.id.profile_photo);
        TextView date = findViewById(R.id.date);

        body.setText(question.body);

        UserService userService = new UserService();
        userService.getPublicUserInfo(question.userId).enqueue(new Callback<SingleResponse<User>>() {
            @Override
            public void onResponse(Call<SingleResponse<User>> call, Response<SingleResponse<User>> response) {
                if(response.isSuccessful()){
                    User user = response.body().data;
                    username.setText(user.username);
                    Glide.with(AnswersActivity.this).load(user.photoUrl).error(R.drawable.rindexlogo).into(profilePhoto);
                }
            }

            @Override
            public void onFailure(Call<SingleResponse<User>> call, Throwable t) {

            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime dateTime = LocalDateTime.parse(question.date);
            date.setText(convertToUniversalTime(dateTime.getDayOfMonth()) + "." + convertToUniversalTime(dateTime.getMonthValue()) + "." + dateTime.getYear() + " " + convertToUniversalTime(dateTime.getHour()) + ":" + convertToUniversalTime(dateTime.getMinute()));
        }
    }

    private void getAnswers(){
        AnswerService answerService = new AnswerService();
        answerService.getAnswersByQuestionId(question.id).enqueue(new Callback<ListResponse<Answer>>() {
            @Override
            public void onResponse(Call<ListResponse<Answer>> call, Response<ListResponse<Answer>> response) {
                if(response.isSuccessful()){
                    List<Answer> answers = response.body().data;
                    AnswersAdapter answersAdapter = new AnswersAdapter(AnswersActivity.this,answers,user,question);
                    ListView answersList = findViewById(R.id.answers);
                    answersList.setAdapter(answersAdapter);
                }
            }

            @Override
            public void onFailure(Call<ListResponse<Answer>> call, Throwable t) {

            }
        });
    }
}