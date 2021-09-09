package com.levent.rindex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.appbar.MaterialToolbar;
import com.levent.rindex.R;
import com.levent.rindex.constants.NearbyPlaceType;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.NearbyPlace;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.User;
import com.levent.rindex.services.NearbyPlaceService;
import com.levent.rindex.ui.adapters.NearbyPlaceAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyPlacesListActivity extends AppCompatActivity {

    Place place;
    int type;
    User user;
    MaterialToolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places_list);

        toolbar = findViewById(R.id.topAppBar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        place = (Place)getIntent().getSerializableExtra("Place");
        type = getIntent().getIntExtra("Type",0);
        user = (User)getIntent().getSerializableExtra("User");

        setTitle();

        listNearbyPlaces();
    }

    private void setTitle(){
        String title = "";
        switch (type){
            case NearbyPlaceType.Hotel:
                title = getString(R.string.nearby_hotels);
                break;

            case NearbyPlaceType.Restaurant:
                title = getString(R.string.nearby_restaurants);
                break;

            case NearbyPlaceType.PhotoPlace:
                title = getString(R.string.best_photo_places);
                break;
        }
        toolbar.setTitle(title);
    }

    private void listNearbyPlaces(){
        NearbyPlaceService nearbyPlaceService = new NearbyPlaceService();
        nearbyPlaceService.getByPlaceId(place.id,type).enqueue(new Callback<ListResponse<NearbyPlace>>() {
            @Override
            public void onResponse(Call<ListResponse<NearbyPlace>> call, Response<ListResponse<NearbyPlace>> response) {
                if(response.isSuccessful()){
                    List<NearbyPlace> nearbyPlaceList = response.body().data;
                    if(nearbyPlaceList.size() < 1 && nearbyPlaceList == null){
                        return;
                    }
                    NearbyPlaceAdapter nearbyPlaceAdapter = new NearbyPlaceAdapter(NearbyPlacesListActivity.this,nearbyPlaceList,user);
                    ListView result = findViewById(R.id.result);
                    result.setAdapter(nearbyPlaceAdapter);
                }
            }

            @Override
            public void onFailure(Call<ListResponse<NearbyPlace>> call, Throwable t) {

            }
        });
    }
}