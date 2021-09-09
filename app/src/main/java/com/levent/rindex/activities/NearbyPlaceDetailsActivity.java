package com.levent.rindex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.levent.rindex.R;
import com.levent.rindex.constants.NearbyPlaceType;
import com.levent.rindex.models.NearbyPlace;
import com.levent.rindex.models.User;
import com.levent.rindex.models.additionalInfo.HotelAdditionalInfo;
import com.levent.rindex.models.additionalInfo.RestaurantAdditionalInfo;
import com.levent.rindex.services.AdditionalInformationService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyPlaceDetailsActivity extends AppCompatActivity {

    ImageView image;
    TextView title;
    Button comments;
    TextView provinceDistrict;
    NearbyPlace nearbyPlace;
    User currentUser;

    MaterialCardView hotelFeatures;
    MaterialCardView restaurantFeatures;

    TextView hasInternetConnection;
    TextView hasVirusPrecautions;
    TextView workTimes;
    TextView hasFamilyLounge;
    TextView serveAlcohol;
    TextView hasLocalFood;
    TextView starCount;
    TextView petsAllowed;

    Button launchMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_place_details);

        nearbyPlace = (NearbyPlace)getIntent().getSerializableExtra("NearbyPlace");
        currentUser = (User)getIntent().getSerializableExtra("User");

        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        comments = findViewById(R.id.comments);
        provinceDistrict = findViewById(R.id.province_district);

        restaurantFeatures = findViewById(R.id.restaurant_features);
        hotelFeatures = findViewById(R.id.hotel_features);

        hasInternetConnection = findViewById(R.id.has_internet_connection);
        hasVirusPrecautions = findViewById(R.id.has_virus_precautions);
        workTimes = findViewById(R.id.work_times);
        hasFamilyLounge = findViewById(R.id.has_family_lounge);
        serveAlcohol = findViewById(R.id.serve_alcohol);
        hasLocalFood = findViewById(R.id.has_local_food);
        starCount = findViewById(R.id.star_count);
        petsAllowed = findViewById(R.id.pets_allowed);

        launchMaps = findViewById(R.id.launch_maps);
        setCommon();
        setDetails();
    }

    private void setCommon(){
        Glide.with(this).load(nearbyPlace.imageLink).into(image);
        title.setText(nearbyPlace.name);
        provinceDistrict.setText(nearbyPlace.province + " • " + nearbyPlace.district);
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NearbyPlaceDetailsActivity.this,NearbyPlaceCommentsActivity.class);
                intent.putExtra("placeId",nearbyPlace.id);
                intent.putExtra("User",currentUser);
                startActivity(intent);
            }
        });

        launchMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+nearbyPlace.latitude+","+nearbyPlace.longitude));
                startActivity(intent);
            }
        });
    }

    private void setDetails(){
        if(nearbyPlace.type == NearbyPlaceType.Restaurant){
            restaurantFeatures.setVisibility(View.VISIBLE);
            AdditionalInformationService<RestaurantAdditionalInfo> additionalInformationService = new AdditionalInformationService<>();
            additionalInformationService.getInformation(nearbyPlace.id).enqueue(new Callback<RestaurantAdditionalInfo>() {
                @Override
                public void onResponse(Call<RestaurantAdditionalInfo> call, Response<RestaurantAdditionalInfo> response) {
                    RestaurantAdditionalInfo info = response.body();

                    workTimes.setText(info.openTime + " - " + info.closeTime);

                    if(!info.hasInternetConnection){
                        hasInternetConnection.setTextColor(Color.parseColor("#cccccc"));
                    }
                    if(!info.hasVirusPrecautions){
                        hasVirusPrecautions.setTextColor(Color.parseColor("#cccccc"));
                    }
                    if(!info.hasFamilyLounge){
                        hasFamilyLounge.setTextColor(Color.parseColor("#cccccc"));
                    }
                    if(!info.serveAlcohol){
                        serveAlcohol.setTextColor(Color.parseColor("#cccccc"));
                    }
                    if(!info.hasLocalFood){
                        hasLocalFood.setTextColor(Color.parseColor("#cccccc"));
                    }
                }

                @Override
                public void onFailure(Call<RestaurantAdditionalInfo> call, Throwable t) {

                }
            });
        }
        else if(nearbyPlace.type == NearbyPlaceType.Hotel){
            hotelFeatures.setVisibility(View.VISIBLE);
            AdditionalInformationService<HotelAdditionalInfo> additionalInformationService = new AdditionalInformationService<>();
            additionalInformationService.getInformation(nearbyPlace.id).enqueue(new Callback<HotelAdditionalInfo>() {
                @Override
                public void onResponse(Call<HotelAdditionalInfo> call, Response<HotelAdditionalInfo> response) {
                    HotelAdditionalInfo info = response.body();
                    workTimes.setText(info.openTime + " - " + info.closeTime);
                    starCount.setText(info.star);
                    if(!info.hasInternetConnection){
                        hasInternetConnection.setTextColor(Color.parseColor("#cccccc"));
                    }
                    if(!info.hasVirusPrecautions){
                        hasVirusPrecautions.setTextColor(Color.parseColor("#cccccc"));
                    }
                    if(!info.petsAllowed){
                        petsAllowed.setTextColor(Color.parseColor("#cccccc"));
                    }

                }

                @Override
                public void onFailure(Call<HotelAdditionalInfo> call, Throwable t) {

                }
            });
        }
    }
}