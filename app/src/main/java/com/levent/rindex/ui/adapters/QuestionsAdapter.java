package com.levent.rindex.ui.adapters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.levent.rindex.R;
import com.levent.rindex.activities.AnswersActivity;
import com.levent.rindex.models.Question;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.User;
import com.levent.rindex.services.AccountService;
import com.levent.rindex.services.QuestionService;
import com.levent.rindex.services.UserService;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionsAdapter extends ArrayAdapter<Question> {

    private List<Question> questions;
    private Context context;
    private User currentUser;
    private Map<Integer,User> userCache;

    public QuestionsAdapter(@NonNull Context context, List<Question> questions,User currentUser) {
        super(context, R.layout.question_row);
        this.context = context;
        this.questions = questions;
        this.currentUser = currentUser;
        this.userCache = new HashMap<>();
    }

    private String convertToUniversalTime(int n){
        if(n <= 9){
            return "0"+n;
        }
        else{
            return String.valueOf(n);
        }
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        View root = layoutInflater.inflate(R.layout.question_row,null);
        Question currentQuestion = questions.get(position);

        ImageView profilePhoto = root.findViewById(R.id.profile_photo);
        TextView body = root.findViewById(R.id.body);
        TextView username = root.findViewById(R.id.username);
        TextView date = root.findViewById(R.id.date);
        Button reply = root.findViewById(R.id.reply);
        Button delete = root.findViewById(R.id.delete);
        Button answers = root.findViewById(R.id.answers);

        answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AnswersActivity.class);
                intent.putExtra("Question",currentQuestion);
                intent.putExtra("User",currentUser);
                context.startActivity(intent);
            }
        });

        if(currentQuestion.body.length() > 200){
            body.setText(currentQuestion.body.substring(0,199) + "...");
            body.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    body.setText(currentQuestion.body);
                }
            });
        }
        else{
            body.setText(currentQuestion.body);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime dateTime = LocalDateTime.parse(currentQuestion.date);
            date.setText(convertToUniversalTime(dateTime.getDayOfMonth()) + "." + convertToUniversalTime(dateTime.getMonthValue()) + "." + dateTime.getYear() + " " + convertToUniversalTime(dateTime.getHour()) + ":" + convertToUniversalTime(dateTime.getMinute()));
        }

        if(currentUser.id == currentQuestion.userId){
            reply.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteQuestion(currentQuestion.id,position);
                }
            });
        }

        if(userCache.containsKey(position)){
            User user = userCache.get(position);
            Glide.with(context).load(user.photoUrl).error(R.drawable.rindexlogo).into(profilePhoto);
            username.setText(user.username);
        }
        else{
            UserService userService = new UserService();
            userService.getPublicUserInfo(currentQuestion.userId).enqueue(new Callback<SingleResponse<User>>() {
                @Override
                public void onResponse(Call<SingleResponse<User>> call, Response<SingleResponse<User>> response) {
                    User user = response.body().data;
                    Glide.with(context).load(user.photoUrl).error(R.drawable.rindexlogo).into(profilePhoto);
                    username.setText(user.username);
                    userCache.put(position,user);
                }

                @Override
                public void onFailure(Call<SingleResponse<User>> call, Throwable t) {

                }
            });
        }

        return root;
    }

    private void deleteQuestion(int questionId, int position){
        QuestionService questionService = new QuestionService();
        questionService.deleteQuestion(context,questionId).enqueue(new Callback<com.levent.rindex.models.Response>() {
            @Override
            public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                if(response.isSuccessful()){
                    questions.remove(position);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<com.levent.rindex.models.Response> call, Throwable t) {

            }
        });
    }
}
