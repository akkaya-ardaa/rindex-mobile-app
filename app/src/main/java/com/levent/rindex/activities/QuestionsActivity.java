package com.levent.rindex.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.levent.rindex.R;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.Question;
import com.levent.rindex.models.User;
import com.levent.rindex.services.QuestionService;
import com.levent.rindex.ui.adapters.QuestionsAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionsActivity extends AppCompatActivity {

    private Place place;
    private String body;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUser();
        getPlace();
        listQuestions();
        setupAddQuestion();
    }

    private void getUser(){
        user = (User)getIntent().getSerializableExtra("User");
    }

    private void getPlace(){
        place = (Place)getIntent().getSerializableExtra("Place");
    }

    private void listQuestions(){
        QuestionService questionService = new QuestionService();

        ListView questions = findViewById(R.id.questions);
        questionService.getQuestionsByPlaceId(place.id).enqueue(new Callback<ListResponse<Question>>() {
            @Override
            public void onResponse(Call<ListResponse<Question>> call, Response<ListResponse<Question>> response) {
                QuestionsAdapter questionsAdapter = new QuestionsAdapter(QuestionsActivity.this,response.body().data,user);
                questions.setAdapter(questionsAdapter);
            }

            @Override
            public void onFailure(Call<ListResponse<Question>> call, Throwable t) {

            }
        });
    }
    private void setupAddQuestion(){
        FloatingActionButton addQuestion = findViewById(R.id.add_question);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogRoot = layoutInflater.inflate(R.layout.add_question_dialog,null);

                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(QuestionsActivity.this);
                alertDialogBuilder.setView(dialogRoot);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                MaterialCardView bodyCard = dialogRoot.findViewById(R.id.body_card);
                Button closeButton = dialogRoot.findViewById(R.id.close);
                Button confirmButton = dialogRoot.findViewById(R.id.confirm);

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                bodyCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View editorRoot = layoutInflater.inflate(R.layout.add_question_body_layout,null);

                        MaterialAlertDialogBuilder alertDialogBuilder1 = new MaterialAlertDialogBuilder(QuestionsActivity.this);
                        alertDialogBuilder1.setView(editorRoot);
                        AlertDialog alertDialog1 = alertDialogBuilder1.create();
                        alertDialog1.show();

                        EditText bodyField = editorRoot.findViewById(R.id.body_field);
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

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        MaterialAlertDialogBuilder alertDialogBuilder1 = new MaterialAlertDialogBuilder(QuestionsActivity.this);
                        alertDialogBuilder1.setView(layoutInflater.inflate(R.layout.loading_layout,null));
                        AlertDialog loadingDialog = alertDialogBuilder1.create();
                        loadingDialog.setCancelable(false);
                        loadingDialog.show();

                        QuestionService questionService = new QuestionService();
                        Question question = new Question();
                        question.body = body;
                        question.placeId = place.id;
                        questionService.addQuestion(QuestionsActivity.this,question).enqueue(new Callback<com.levent.rindex.models.Response>() {
                            @Override
                            public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                                if(response.isSuccessful()){
                                    loadingDialog.dismiss();
                                    Toast.makeText(QuestionsActivity.this,R.string.question_added,Toast.LENGTH_SHORT).show();
                                    listQuestions();
                                    body = null;

                                }
                            }

                            @Override
                            public void onFailure(Call<com.levent.rindex.models.Response> call, Throwable t) {

                            }
                        });
                    }
                });
            }
        });
    }
}