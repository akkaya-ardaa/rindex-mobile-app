package com.levent.rindex.ui.adapters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.levent.rindex.R;
import com.levent.rindex.activities.NearbyPlaceDetailsActivity;
import com.levent.rindex.constants.NearbyPlaceType;
import com.levent.rindex.models.NearbyPlace;
import com.levent.rindex.models.User;
import com.levent.rindex.models.additionalInfo.RestaurantAdditionalInfo;
import com.levent.rindex.services.AdditionalInformationService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyPlaceAdapter extends ArrayAdapter<NearbyPlace> {

    private Context context;
    private List<NearbyPlace> nearbyPlaces;
    private User currentUser;
    public NearbyPlaceAdapter(@NonNull Context context, List<NearbyPlace> nearbyPlaces, User currentUser) {
        super(context, R.layout.nearby_place_row);
        this.context = context;
        this.nearbyPlaces = nearbyPlaces;
        this.currentUser = currentUser;
    }

    @Override
    public int getCount() {
        return nearbyPlaces.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.nearby_place_row,null);

        MaterialCardView cardView = row.findViewById(R.id.card);
        ImageView imageView = row.findViewById(R.id.image);
        TextView title = row.findViewById(R.id.title);
        Chip province = row.findViewById(R.id.province);
        Chip district = row.findViewById(R.id.district);
        TextView rating = row.findViewById(R.id.rating);

        NearbyPlace nearbyPlace = nearbyPlaces.get(position);

        Glide.with(context).load(nearbyPlace.imageLink).into(imageView);
        title.setText(nearbyPlace.name);

        province.setText(nearbyPlace.province);
        district.setText(nearbyPlace.district);

        setAdditionalInformation(nearbyPlace,row);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NearbyPlaceDetailsActivity.class);
                intent.putExtra("NearbyPlace",nearbyPlace);
                intent.putExtra("User",currentUser);
                context.startActivity(intent);
            }
        });

        return row;
    }

    private void setAdditionalInformation(NearbyPlace nearbyPlace,View row){
        ImageView hasLocalFood = row.findViewById(R.id.hasLocalFood);
        ImageView hasInternetConnection = row.findViewById(R.id.hasInternetConnection);
        ImageView hasVirusPrecautions = row.findViewById(R.id.hasVirusPrecautions);
        ImageView hasFamilyLounge = row.findViewById(R.id.hasFamilyLounge);
        ImageView serveAlcohol = row.findViewById(R.id.serveAlcohol);

        if(nearbyPlace.type == NearbyPlaceType.Hotel){

        }
        else if(nearbyPlace.type == NearbyPlaceType.Restaurant){
            AdditionalInformationService<RestaurantAdditionalInfo> additionalInformationService = new AdditionalInformationService<RestaurantAdditionalInfo>();
            additionalInformationService.getInformation(nearbyPlace.id).enqueue(new Callback<RestaurantAdditionalInfo>() {
                @Override
                public void onResponse(Call<RestaurantAdditionalInfo> call, Response<RestaurantAdditionalInfo> response) {
                    if(response.body() != null){
                        RestaurantAdditionalInfo restaurantAdditionalInfo = response.body();
                        makeVisible(hasLocalFood,restaurantAdditionalInfo.hasLocalFood);
                        makeVisible(hasInternetConnection,restaurantAdditionalInfo.hasInternetConnection);
                        makeVisible(hasVirusPrecautions,restaurantAdditionalInfo.hasVirusPrecautions);
                        makeVisible(hasFamilyLounge,restaurantAdditionalInfo.hasFamilyLounge);
                        makeVisible(serveAlcohol,restaurantAdditionalInfo.serveAlcohol);
                    }
                }

                @Override
                public void onFailure(Call<RestaurantAdditionalInfo> call, Throwable t) {

                }
            });
        }
        else if(nearbyPlace.type == NearbyPlaceType.PhotoPlace){

        }
    }

    private void makeVisible(View v,boolean visible){
        if(visible){
            v.setVisibility(View.VISIBLE);
        }
        else{
            v.setVisibility(View.GONE);
        }
    }
}
