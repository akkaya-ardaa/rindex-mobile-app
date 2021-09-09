package com.levent.rindex.ui.place_details;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.UploadTask;
import com.levent.rindex.activities.LiveChatListActivity;
import com.levent.rindex.activities.QuestionsActivity;
import com.levent.rindex.R;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.Post;
import com.levent.rindex.models.User;
import com.levent.rindex.services.FirebaseService;
import com.levent.rindex.services.PostService;
import com.levent.rindex.ui.adapters.PostsAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceDetailsSocialFragment extends Fragment {

    Place place;
    User currentUser;

    private int currentIndex = 0;
    private int perCount = 10;
    private int preLast;

    private final int PERMISSION_CODE = 1001;
    private final int REQUEST_CODE = 1002;
    private final int REQUEST_OK = -1;

    private Uri photoUri;
    private String descriptionText;

    private ImageView photoPreview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_place_details_social, container, false);

        currentUser = (User) getActivity().getIntent().getSerializableExtra("User");
        place = (Place) getActivity().getIntent().getSerializableExtra("Place");

        try {
            getNextPosts(place.id, root.findViewById(R.id.posts));
        }
        catch (Exception e){

        }

        FloatingActionButton addPostButton = root.findViewById(R.id.add_post);
        FloatingActionButton helpButton = root.findViewById(R.id.help);
        FloatingActionButton liveChatButton = root.findViewById(R.id.live_chat);

        liveChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LiveChatListActivity.class);
                intent.putExtra("Place",place);
                startActivity(intent);
            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialog = layoutInflater.inflate(R.layout.add_post_layout,null);

                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
                dialogBuilder.setView(dialog);

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                alertDialog.setCancelable(false);

                photoPreview = dialog.findViewById(R.id.photo);

                Button closeButton = dialog.findViewById(R.id.close);
                Button confirmButton = dialog.findViewById(R.id.confirm);

                MaterialCardView descriptionCard = dialog.findViewById(R.id.description_card);
                MaterialCardView photoCard = dialog.findViewById(R.id.photo_card);

                descriptionCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());

                        View descriptionRoot = layoutInflater.inflate(R.layout.description_field_layout,null);
                        EditText description = descriptionRoot.findViewById(R.id.description);
                        if(descriptionText != null)
                        description.setText(descriptionText + "");
                        dialogBuilder.setView(descriptionRoot);
                        AlertDialog descriptionDialog = dialogBuilder.create();
                        descriptionDialog.show();
                        descriptionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                descriptionText = description.getText().toString();
                            }
                        });
                    }
                });

                photoCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                                String[] ps = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                requestPermissions(ps,PERMISSION_CODE);
                            }
                            else{
                                pickImage();
                            }
                        }
                        else{
                            pickImage();
                        }
                    }
                });

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                        alertDialogBuilder.setView(layoutInflater.inflate(R.layout.loading_layout,null));
                        AlertDialog loadingDialog = alertDialogBuilder.create();
                        loadingDialog.setCancelable(false);
                        loadingDialog.show();

                        Bitmap bitmapImage = null;
                        try {
                            bitmapImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),photoUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int nh = (int) ( bitmapImage.getHeight() * (1024.0 / bitmapImage.getWidth()) );
                        Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 1024, nh, true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        scaled.compress(Bitmap.CompressFormat.JPEG,40,stream);

                        FirebaseService firebaseService = new FirebaseService(getContext());
                        final String path = "PostImages/" + UUID.randomUUID().toString();
                        firebaseService.uploadImage(stream.toByteArray(),path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                firebaseService.getDownloadUrl(path).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Log.e("Decoded URI","Initialized: "+ uri.toString());

                                        PostService postService = new PostService();
                                        Post post = new Post();
                                        post.description = descriptionText;
                                        post.imageUrl = uri.toString();
                                        post.placeId = place.id;
                                        postService.add(getContext(),post).enqueue(new Callback<com.levent.rindex.models.Response>() {
                                            @Override
                                            public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                                                if(response.isSuccessful()){
                                                    Snackbar.make(confirmButton,R.string.send_post,Snackbar.LENGTH_SHORT).show();
                                                    alertDialog.dismiss();
                                                    loadingDialog.dismiss();
                                                    currentIndex = 0;
                                                    getNextPosts(place.id,root.findViewById(R.id.posts));
                                                    descriptionText = null;

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
                });
            }
        });
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QuestionsActivity.class);
                intent.putExtra("Place",place);
                intent.putExtra("User",currentUser);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            pickImage();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == REQUEST_OK && requestCode == REQUEST_CODE){
            photoUri = data.getData();
            Glide.with(getContext()).load(photoUri).into(photoPreview);
        }
    }

    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE);
    }

    private void getNextPosts(int placeId, ListView listView){
        PostService postService = new PostService();
        postService.getPostsByPlaceId(placeId,currentIndex,currentIndex + perCount).enqueue(new Callback<ListResponse<Post>>() {
            @Override
            public void onResponse(Call<ListResponse<Post>> call, Response<ListResponse<Post>> response) {
                if(response.body() == null){
                    return;
                }
                List<Post> posts = response.body().data;
                PostsAdapter postsAdapter = new PostsAdapter(getContext(),posts,currentUser,listView);
                listView.setAdapter(postsAdapter);

                currentIndex = currentIndex + perCount;


                listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        final int lastItem = firstVisibleItem + visibleItemCount;

                        if(lastItem == totalItemCount) {
                            if (preLast != lastItem) {
                                preLast = lastItem;

                                postService.getPostsByPlaceId(placeId,currentIndex,currentIndex + perCount).enqueue(new Callback<ListResponse<Post>>() {
                                    @Override
                                    public void onResponse(Call<ListResponse<Post>> call, Response<ListResponse<Post>> response) {
                                        if(response.body() == null){
                                            return;
                                        }
                                        List<Post> newPosts = response.body().data;
                                        for (int i = 0; i < newPosts.size(); i++){
                                            posts.add(newPosts.get(i));
                                        }
                                        postsAdapter.notifyDataSetChanged();

                                        currentIndex = currentIndex + perCount;
                                    }

                                    @Override
                                    public void onFailure(Call<ListResponse<Post>> call, Throwable t) {

                                    }
                                });
                            }
                        }

                    }
                });
            }

            @Override
            public void onFailure(Call<ListResponse<Post>> call, Throwable t) {

            }
        });


    }
}