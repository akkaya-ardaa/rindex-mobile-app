package com.levent.rindex.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.levent.rindex.R;
import com.levent.rindex.activities.PlaceCommentsActivity;
import com.levent.rindex.activities.UserCommentsActivity;
import com.levent.rindex.models.Comment;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.User;
import com.levent.rindex.services.AccountService;
import com.levent.rindex.services.CommentService;
import com.levent.rindex.services.UserService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsAdapter extends ArrayAdapter<Comment> {

    Map<Integer,View> cache;
    private Context context;
    private List<Comment> comments;
    private User currentUser;
    public CommentsAdapter(Context context, List<Comment> comments, User currentUser){
        super(context, R.layout.comment_row,comments);
        this.context = context;
        this.comments = comments;
        this.cache = new HashMap<Integer,View>();
        this.currentUser = currentUser;
    }

    @Override
    public int getCount() {
        return comments.size();
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

        if(cache.containsKey(position)){
            return cache.get(position);
        }

        UserService userService = new UserService();

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.comment_row,null);
        Comment currentComment = comments.get(position);

        MaterialCardView cardView = row.findViewById(R.id.card);
        TextView firstLastName = row.findViewById(R.id.first_last_name);
        ImageView profilePhoto = row.findViewById(R.id.profile_photo);
        RatingBar ratingBar = row.findViewById(R.id.rating_bar);
        TextView text = row.findViewById(R.id.text);
        TextView seeMore = row.findViewById(R.id.see_more);
        TextView date = row.findViewById(R.id.date);

        ratingBar.setRating(currentComment.star);

        if(currentComment.text.length() < 200) {
            text.setText(currentComment.text);
        }
        else{
            seeMore.setVisibility(View.VISIBLE);
            text.setText(currentComment.text.substring(0,199));
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seeMore.setVisibility(View.INVISIBLE);
                    text.setText(currentComment.text);
                }
            });
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime dateTime = LocalDateTime.parse(currentComment.addedDate);
            Calendar commentDate = Calendar.getInstance();

            date.setText(convertToUniversalTime(dateTime.getDayOfMonth()) + "." + convertToUniversalTime(dateTime.getMonthValue()) + "." + dateTime.getYear() + " " + convertToUniversalTime(dateTime.getHour()) + ":" + convertToUniversalTime(dateTime.getMinute()));
        }

        userService.getPublicUserInfo(currentComment.userId).enqueue(new Callback<SingleResponse<User>>() {
            @Override
            public void onResponse(Call<SingleResponse<User>> call, Response<SingleResponse<User>> response) {
                if(response.isSuccessful()){
                    User user = response.body().data;

                    if(currentComment.userId == currentUser.id) {
                        cardView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                View dialogRoot = layoutInflater.inflate(R.layout.comment_options_layout,null);
                                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);
                                alertDialogBuilder.setView(dialogRoot);
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                                MaterialCardView delete = dialogRoot.findViewById(R.id.delete);
                                MaterialCardView edit = dialogRoot.findViewById(R.id.edit);
                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CommentService commentService = new CommentService();
                                        commentService.deleteComment(context,currentComment.id).enqueue(new Callback<com.levent.rindex.models.Response>() {
                                            @Override
                                            public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                                                if(response.isSuccessful()){
                                                    comments.remove(position);
                                                    notifyDataSetChanged();
                                                    cache = new HashMap<>();
                                                    alertDialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<com.levent.rindex.models.Response> call, Throwable t) {

                                            }
                                        });
                                    }
                                });
                                edit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, PlaceCommentsActivity.class);
                                        intent.putExtra("User",user);
                                        intent.putExtra("placeId",currentComment.placeId);
                                        context.startActivity(intent);
                                        alertDialog.dismiss();
                                    }
                                });

                                return true;
                            }
                        });
                    }

                    Glide.with(context).load(user.photoUrl).into(profilePhoto);
                    firstLastName.setText(user.firstName+" "+user.lastName);
                    cache.put(position,row);
                }
            }

            @Override
            public void onFailure(Call<SingleResponse<User>> call, Throwable t) {

            }
        });



        return row;
    }
}
