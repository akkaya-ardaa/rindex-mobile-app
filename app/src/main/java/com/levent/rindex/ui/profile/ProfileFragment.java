package com.levent.rindex.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.UploadTask;
import com.levent.rindex.R;
import com.levent.rindex.activities.UserCommentsActivity;
import com.levent.rindex.activities.UserPostsActivity;
import com.levent.rindex.activities.UserQuestionsActivity;
import com.levent.rindex.models.Comment;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Post;
import com.levent.rindex.models.Question;
import com.levent.rindex.models.User;
import com.levent.rindex.services.AccountService;
import com.levent.rindex.services.FirebaseService;
import com.levent.rindex.services.QuestionService;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Permission;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    User user;
    TextView nameSurname;
    TextView username;
    TextView biography;
    TextView commentsCount;
    TextView questionsCount;
    TextView postsCount;
    ImageView profilePhoto;
    MaterialCardView postsCard;
    MaterialCardView commentsCard;
    MaterialCardView questionsCard;
    Button edit;

    private final int PERMISSION_CODE = 1001;
    private final int REQUEST_CODE = 1002;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        getUser();

        nameSurname = root.findViewById(R.id.name_surname);
        username = root.findViewById(R.id.username);
        biography = root.findViewById(R.id.biography);
        commentsCount = root.findViewById(R.id.comments_count);
        postsCount = root.findViewById(R.id.posts_count);
        postsCard = root.findViewById(R.id.posts_card);
        commentsCard = root.findViewById(R.id.comments_card);
        profilePhoto = root.findViewById(R.id.profile_photo);
        questionsCard = root.findViewById(R.id.questions_card);
        questionsCount = root.findViewById(R.id.questions_count);
        edit = root.findViewById(R.id.edit);

        nameSurname.setText(user.firstName + " " + user.lastName);
        username.setText(user.username);
        biography.setText(user.biography);
        Glide.with(getContext()).load(user.photoUrl).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.e("PP Load Failed!","URL: "+user.photoUrl);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).error(R.drawable.rindexlogo).into(profilePhoto);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogRoot = layoutInflater.inflate(R.layout.edit_profile_layout,null);

                MaterialCardView nameSurnameCard = dialogRoot.findViewById(R.id.name_surname_card);
                MaterialCardView photoCard = dialogRoot.findViewById(R.id.photo_card);
                MaterialCardView biographyCard = dialogRoot.findViewById(R.id.biography_card);

                nameSurnameCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View nameSurnameRoot = layoutInflater.inflate(R.layout.edit_profile_name_surname,null);

                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                        alertDialogBuilder.setView(nameSurnameRoot);
                        AlertDialog nameSurnameDialog = alertDialogBuilder.create();
                        nameSurnameDialog.show();
                        nameSurnameDialog.setCancelable(false);

                        EditText nameEditor = nameSurnameRoot.findViewById(R.id.name);
                        EditText surnameEditor = nameSurnameRoot.findViewById(R.id.surname);

                        nameEditor.setText(user.firstName);
                        surnameEditor.setText(user.lastName);

                        Button close = nameSurnameRoot.findViewById(R.id.close);
                        Button confirm = nameSurnameRoot.findViewById(R.id.confirm);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nameSurnameDialog.dismiss();
                            }
                        });
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeNameSurname(nameEditor.getText().toString(),surnameEditor.getText().toString());
                                nameSurnameDialog.dismiss();
                            }
                        });
                    }
                });
                photoCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeProfilePicture();
                    }
                });
                biographyCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View biographyRoot = layoutInflater.inflate(R.layout.edit_profile_biography,null);

                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                        alertDialogBuilder.setView(biographyRoot);
                        AlertDialog biographyDialog = alertDialogBuilder.create();
                        biographyDialog.show();
                        biographyDialog.setCancelable(false);

                        EditText biographyEditor = biographyRoot.findViewById(R.id.biography);
                        biographyEditor.setText(user.biography);

                        Button close = biographyRoot.findViewById(R.id.close);
                        Button confirm = biographyRoot.findViewById(R.id.confirm);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                biographyDialog.dismiss();
                            }
                        });
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeBiography(biographyEditor.getText().toString());
                            }
                        });
                    }
                });

                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                alertDialogBuilder.setView(dialogRoot);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        commentsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserCommentsActivity.class);
                intent.putExtra("User",user);
                startActivity(intent);
            }
        });


        getUserTotalCommentCount();
        getUserTotalPostCount();
        getUserTotalQuestionCount();

        return root;
    }

    private void getUser(){
        user = (User)getActivity().getIntent().getSerializableExtra("User");
    }
    private void getUserTotalCommentCount() {
        AccountService accountService = new AccountService();
        accountService.getAllComments(user.id).enqueue(new Callback<ListResponse<Comment>>() {
            @Override
            public void onResponse(Call<ListResponse<Comment>> call, Response<ListResponse<Comment>> response) {
                if(response.body() == null){
                    return;
                }
                commentsCount.setText(getString(R.string.total_comment_count) + String.valueOf(response.body().data.size()));
            }

            @Override
            public void onFailure(Call<ListResponse<Comment>> call, Throwable t) {

            }
        });
    }
    private void getUserTotalPostCount(){
        AccountService accountService = new AccountService();
        accountService.getAllPosts(user.id).enqueue(new Callback<ListResponse<Post>>() {
            @Override
            public void onResponse(Call<ListResponse<Post>> call, Response<ListResponse<Post>> response) {
                if(response.body() == null){
                    return;
                }
                postsCount.setText(getString(R.string.click_to_edit_posts) + " " + String.valueOf(response.body().data.size()));

                postsCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),UserPostsActivity.class);
                        intent.putExtra("User",user);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<ListResponse<Post>> call, Throwable t) {

            }
        });
    }
    private void changeNameSurname(String name, String surname){
        AccountService accountService = new AccountService();
        accountService.updateNameSurname(getContext(),name,surname).enqueue(new Callback<com.levent.rindex.models.Response>() {
            @Override
            public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getContext(),R.string.changed_successfully_rr,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.levent.rindex.models.Response> call, Throwable t) {

            }
        });
    }
    private void changeProfilePicture(){
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
    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Log.e("Crop OK","Result Ok!");
                compressAndUpload(data.getData());
            }
        }
    }
    private void compressAndUpload(Uri uri){
        Bitmap bitmapImage = null;
        try {
            bitmapImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int nh = (int) ( bitmapImage.getHeight() * (1024.0 / bitmapImage.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 1024, nh, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG,40,stream);

        FirebaseService firebaseService = new FirebaseService(getContext());
        final String path = "ProfilePhotos/"+ UUID.randomUUID().toString();
        firebaseService.uploadImage(stream.toByteArray(),path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                firebaseService.getDownloadUrl(path).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        AccountService accountService = new AccountService();
                        accountService.updatePhoto(getContext(),uri.toString()).enqueue(new Callback<com.levent.rindex.models.Response>() {
                            @Override
                            public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(getContext(),R.string.changed_successfully_rr,Toast.LENGTH_SHORT).show();
                                    Glide.with(getContext()).load(uri).into(profilePhoto);
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
    private void changeBiography(String biography){
        AccountService accountService = new AccountService();
        accountService.updateBiography(getContext(),biography).enqueue(new Callback<com.levent.rindex.models.Response>() {
            @Override
            public void onResponse(Call<com.levent.rindex.models.Response> call, Response<com.levent.rindex.models.Response> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getContext(),R.string.changed_successfully_rr,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.levent.rindex.models.Response> call, Throwable t) {

            }
        });
    }
    private void getUserTotalQuestionCount(){
        QuestionService questionService = new QuestionService();
        questionService.getQuestionsByUserId(user.id).enqueue(new Callback<ListResponse<Question>>() {
            @Override
            public void onResponse(Call<ListResponse<Question>> call, Response<ListResponse<Question>> response) {
                questionsCount.setText(getString(R.string.click_to_edit_questions) +" "+ String.valueOf(response.body().data.size()));
                questionsCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UserQuestionsActivity.class);
                        intent.putExtra("User",user);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<ListResponse<Question>> call, Throwable t) {

            }
        });
    }
}