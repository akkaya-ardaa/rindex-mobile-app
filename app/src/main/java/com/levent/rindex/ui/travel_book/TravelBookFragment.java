package com.levent.rindex.ui.travel_book;

import android.graphics.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.levent.rindex.R;
import com.levent.rindex.models.TravelBookItem;
import com.levent.rindex.services.TravelBookService;

public class TravelBookFragment extends Fragment {

    GoogleMap gMap;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_travel_book, container, false);

        setupMap();

        return root;
    }

    private void markLocations(){
        TravelBookItem[] travelBookItems = TravelBookService.getTravelBookItems(getContext());
        Log.e("Travel Book Item Count",String.valueOf(travelBookItems.length));
        if(travelBookItems == null){
            Log.e("Empty!","Travel book is empty.");
            Toast.makeText(getContext(),getString(R.string.travel_book_empty),Toast.LENGTH_LONG).show();
        }
        else if(travelBookItems.length == 0){
            Log.e("Empty!","Travel book is empty.");
            Toast.makeText(getContext(),getString(R.string.travel_book_empty),Toast.LENGTH_LONG).show();
        }
        else{
            for(int i = 0; i < travelBookItems.length; i++){
                TravelBookItem current = travelBookItems[i];

                LatLng latLng = new LatLng(Double.valueOf(current.latitude),Double.valueOf(current.longitude));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(current.title);

                gMap.addMarker(markerOptions);
            }
        }
    }

    private void setupMap(){
        SupportMapFragment supportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.925533,32.866287),5));
                markLocations();
            }
        });
    }
}