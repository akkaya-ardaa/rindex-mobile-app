package com.levent.rindex.ui.adapters;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.levent.rindex.R;
import com.levent.rindex.models.Answer;
import com.levent.rindex.models.Question;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.User;
import com.levent.rindex.services.AnswerService;
import com.levent.rindex.services.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswersAdapter extends ArrayAdapter<Answer> {

    private List<Answer> answers;
    private Context context;
    private Map<Integer,User> userCache;
    private User currentUser;
    private Question currentQuestion;

    public AnswersAdapter(@NonNull Context context, List<Answer> answers, User currentUser, Question currentQuestion) {
        super(context, R.layout.answer_row);
        this.answers = answers;
        this.context = context;
        this.userCache = new HashMap<>();
        this.currentUser = currentUser;
        this.currentQuestion = currentQuestion;
    }

    @Override
    public int getCount() {
        return answers.size();
    }

    private String convertToUniversalTime(int n){
        if(n <= 9){
            return "0"+n;
        }
        else{
            return String.valueOf(n);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View root = layoutInflater.inflate(R.layout.answer_row,null);

        Answer answer = answers.get(position);

        View divider = root.findViewById(R.id.divider);
        TextView body = root.findViewById(R.id.body);
        TextView username = root.findViewById(R.id.username);
        ImageView profilePhoto = root.findViewById(R.id.profile_photo);
        TextView date = root.findViewById(R.id.date);
        Button star = root.findViewById(R.id.star);
        Button starred = root.findViewById(R.id.starred);
        Button unStar = root.findViewById(R.id.unstar);

        if(currentUser.id == currentQuestion.userId){
            if(answer.starred){
                unStar.setVisibility(View.VISIBLE);
            }
            else{
                star.setVisibility(View.VISIBLE);
            }
        }
        else{
            if(answer.starred){
                starred.setVisibility(View.VISIBLE);
            }
            else{
                divider.setVisibility(View.GONE);
            }
        }

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerService answerService = new AnswerService();
                answerService.toggleStarAnswer(context,answer.id).enqueue(new Callback<com.levent.rindex.models.Response>() {
                    @Override
                    public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                        answers.get(position).starred = true;
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<com.levent.rindex.models.Response> call, Throwable t) {

                    }
                });
            }
        });

        unStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerService answerService = new AnswerService();
                answerService.toggleStarAnswer(context,answer.id).enqueue(new Callback<com.levent.rindex.models.Response>() {
                    @Override
                    public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                        answers.get(position).starred = false;
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<com.levent.rindex.models.Response> call, Throwable t) {

                    }
                });
            }
        });

        if(answer.body.length() > 200){
            body.setText(answer.body.substring(0,199)+"...");
            body.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    body.setText(answer.body);
                }
            });
        }
        else{
            body.setText(answer.body);
        }

        if(userCache.containsKey(position)){
            User user = userCache.get(position);
            username.setText(user.username);
            Glide.with(context).load(user.photoUrl).error(R.drawable.rindexlogo).into(profilePhoto);
        }
        else{
            UserService userService = new UserService();
            userService.getPublicUserInfo(answer.userId).enqueue(new Callback<SingleResponse<User>>() {
                @Override
                public void onResponse(Call<SingleResponse<User>> call, Response<SingleResponse<User>> response) {
                    if(response.isSuccessful()){
                        User user = response.body().data;
                        userCache.put(position,user);
                        username.setText(user.username);
                        Glide.with(context).load(user.photoUrl).error(R.drawable.rindexlogo).into(profilePhoto);
                    }
                }

                @Override
                public void onFailure(Call<SingleResponse<User>> call, Throwable t) {

                }
            });
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime dateTime = LocalDateTime.parse(answer.date);
            date.setText(convertToUniversalTime(dateTime.getDayOfMonth()) + "." + convertToUniversalTime(dateTime.getMonthValue()) + "." + dateTime.getYear() + " " + convertToUniversalTime(dateTime.getHour()) + ":" + convertToUniversalTime(dateTime.getMinute()));
        }

        return root;
    }
}
