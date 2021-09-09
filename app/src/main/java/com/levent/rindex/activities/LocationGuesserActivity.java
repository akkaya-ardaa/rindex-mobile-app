package com.levent.rindex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.levent.rindex.R;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.User;
import com.levent.rindex.ui.adapters.PlacesAdapter;

import java.util.ArrayList;
import java.util.List;

public class LocationGuesserActivity extends AppCompatActivity {

    private Place place;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_guesser);

        user = (User)getIntent().getSerializableExtra("User");
        place = (Place)getIntent().getSerializableExtra("Place");

        TextView username = findViewById(R.id.username);
        ImageView profilePhoto = findViewById(R.id.profile_photo);
        TextView detection = findViewById(R.id.detection);

        username.setText(user.username);
        Glide.with(this).load(user.photoUrl).error(R.drawable.rindexlogo).into(profilePhoto);

        detection.setText(getString(R.string.detection)+place.name);
        setupPlace();

        Button yes = findViewById(R.id.yes);
        Button no = findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupPlace(){

        List<Place> placeList = new ArrayList<>();
        placeList.add(place);

        ListView result = findViewById(R.id.result);
        PlacesAdapter placesAdapter = new PlacesAdapter(this,placeList,user);
        result.setAdapter(placesAdapter);
    }
}