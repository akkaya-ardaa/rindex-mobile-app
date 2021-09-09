package com.levent.rindex.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.levent.rindex.R;
import com.levent.rindex.constants.APIConstants;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.Post;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.User;
import com.levent.rindex.services.PlaceService;
import com.levent.rindex.services.PostService;
import com.levent.rindex.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsAdapter extends ArrayAdapter<Post> {

    private Context context;
    private List<Post> posts;
    private User currentUser;
    private ListView listView;
    private int reduce = 0;
    Map<Integer,View> cache;
    public PostsAdapter(@NonNull Context context, List<Post> posts, User currentUser, ListView listView){
        super(context, R.layout.post_row);
        this.context = context;
        this.posts = posts;
        this.cache = new HashMap<>();
        this.currentUser = currentUser;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Post currentPost = posts.get(position);
        View row = layoutInflater.inflate(R.layout.post_row,null);
        ImageView delete = row.findViewById(R.id.delete);

        if(this.cache.containsKey(position)){
            View cachedRow = this.cache.get(position);
            ImageView cachedDelete =  cachedRow.findViewById(R.id.delete);
            cachedDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);
                    alertDialogBuilder.setTitle(R.string.sure);
                    alertDialogBuilder.setMessage(context.getString(R.string.can_not_be_undone) + String.valueOf(position));

                    alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletePost(currentPost.id,position);
                            dialog.dismiss();
                        }
                    });

                    alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
            return cachedRow;
        }
        else{


            UserService userService = new UserService();
            userService.getPublicUserInfo(currentPost.userId).enqueue(new Callback<SingleResponse<User>>() {
                @Override
                public void onResponse(Call<SingleResponse<User>> call, Response<SingleResponse<User>> response) {
                    User user = response.body().data;

                    TextView username = row.findViewById(R.id.username);
                    username.setText(user.username);

                    MaterialCardView cardView = row.findViewById(R.id.card);

                    TextView description = row.findViewById(R.id.description);
                    description.setText(currentPost.description);

                    ImageView image = row.findViewById(R.id.image);
                    ImageView delete = row.findViewById(R.id.delete);
                    ImageView profilePhoto = row.findViewById(R.id.profile_photo);

                    Glide.with(context).load(user.photoUrl).error(R.drawable.rindexlogo).into(profilePhoto);

                    Log.e("Load Image","URL:"+currentPost.imageUrl);
                    Glide.with(context).load(currentPost.imageUrl).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("Failed Load URL","URL:"+currentPost.imageUrl);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(image);

                    if(currentUser.id == currentPost.userId){
                        delete.setVisibility(View.VISIBLE);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);
                                alertDialogBuilder.setTitle(R.string.sure);
                                alertDialogBuilder.setMessage(context.getString(R.string.can_not_be_undone) );

                                alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deletePost(currentPost.id,position);
                                        dialog.dismiss();
                                    }
                                });

                                alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        });
                    }

                }

                @Override
                public void onFailure(Call<SingleResponse<User>> call, Throwable t) {

                }
            });

            PlaceService placeService = new PlaceService();
            placeService.getPlaceById(currentPost.placeId).enqueue(new Callback<SingleResponse<Place>>() {
                @Override
                public void onResponse(Call<SingleResponse<Place>> call, Response<SingleResponse<Place>> response) {
                    Place place = response.body().data;

                    TextView location = row.findViewById(R.id.location);
                    location.setText(place.name + " ");

                    cache.put(position,row);
                }

                @Override
                public void onFailure(Call<SingleResponse<Place>> call, Throwable t) {

                }
            });
        }

        return row;
    }

    private void deletePost(int postId,int position){
        PostService postService = new PostService();
        postService.delete(context,postId).enqueue(new Callback<com.levent.rindex.models.Response>() {
            @Override
            public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                if(response.isSuccessful()){
                    for (int i = 0; i < posts.size(); i++) {
                        Post post = posts.get(i);
                        if(post.id == postId){
                            posts.remove(i);
                            Log.e("Removed",String.valueOf(i));
                        }
                    }
                    notifyDataSetChanged();
                    cache = new HashMap<>();
                }
            }

            @Override
            public void onFailure(Call<com.levent.rindex.models.Response> call, Throwable t) {

            }
        });
    }
}
