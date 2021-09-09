package com.levent.rindex.ui.place_details;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.levent.rindex.R;
import com.levent.rindex.activities.NearbyPlacesListActivity;
import com.levent.rindex.constants.NearbyPlaceType;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.User;

public class PlaceDetailsNearbyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_place_details_nearby, container, false);

        MaterialCardView hotels = root.findViewById(R.id.hotels);
        MaterialCardView restaurants = root.findViewById(R.id.restaurants);
        MaterialCardView photoPlaces = root.findViewById(R.id.photo_places);

        Place place = (Place) getActivity().getIntent().getSerializableExtra("Place");
        User user = (User) getActivity().getIntent().getSerializableExtra("User");

        hotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NearbyPlacesListActivity.class);
                intent.putExtra("Type", NearbyPlaceType.Hotel);
                intent.putExtra("Place", place);
                intent.putExtra("User", user);
                startActivity(intent);
            }
        });

        restaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NearbyPlacesListActivity.class);
                intent.putExtra("Type", NearbyPlaceType.Restaurant);
                intent.putExtra("Place", place);
                intent.putExtra("User", user);
                startActivity(intent);
            }
        });

        photoPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NearbyPlacesListActivity.class);
                intent.putExtra("Type", NearbyPlaceType.PhotoPlace);
                intent.putExtra("Place", place);
                intent.putExtra("User", user);
                startActivity(intent);
            }
        });

        return root;
    }
}