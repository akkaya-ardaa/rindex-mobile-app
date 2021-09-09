package com.levent.rindex.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.levent.rindex.R;
import com.levent.rindex.activities.PlaceDetailsActivity;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.TravelBookItem;
import com.levent.rindex.models.User;
import com.levent.rindex.models.ViewCount;
import com.levent.rindex.services.PlaceService;
import com.levent.rindex.services.TravelBookService;
import com.levent.rindex.services.ViewCountService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesAdapter extends ArrayAdapter<Place> {
    private Context context;
    private List<Place> places;
    private User user;
    private Map<Integer,ViewCount> viewCountCache;
    private Map<Integer,Float> ratingCache;
    public PlacesAdapter(Context context, List<Place> places, User currentUser){
        super(context,R.layout.place_row,places);
        this.context = context;
        this.places = places;
        this.user = currentUser;
        this.viewCountCache = new HashMap<Integer,ViewCount>();
        this.ratingCache = new HashMap<Integer,Float>();
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewCountService viewCountService = new ViewCountService();
        
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.place_row,null);

            MaterialCardView cardView = row.findViewById(R.id.card);
            ImageView imageView = row.findViewById(R.id.image);
            TextView title = row.findViewById(R.id.title);
            TextView viewCount = row.findViewById(R.id.viewCount);
            Chip province = row.findViewById(R.id.province);
            Chip district = row.findViewById(R.id.district);
            Button addTravelBook = row.findViewById(R.id.add_travel_book);
            TextView rating = row.findViewById(R.id.rating);

            Place place = places.get(position);
            title.setText(place.name);
            province.setText(place.province);
            district.setText(place.district);

            Glide.with(context).load(place.imageLink).into(imageView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewCountService.increasePlaceViewCount(context,place.id)){
                        viewCount.setText(String.valueOf(Integer.valueOf(viewCount.getText().toString()) + 1));
                    }
                    Intent intent = new Intent(context, PlaceDetailsActivity.class);
                    intent.putExtra("Place",place);
                    intent.putExtra("User",user);
                    context.startActivity(intent);
                }
            });

            if(checkExistsInTravelBook(context,place.id)){
                setButtonNegative(addTravelBook,place);
            }
            else{
                setButtonPositive(addTravelBook,place);
            }

            if(viewCountCache.containsKey(position)){
                viewCount.setText(String.valueOf(viewCountCache.get(position).count));
            }
            else{
                viewCountService.getPlaceViewCount(place.id).enqueue(new Callback<SingleResponse<ViewCount>>() {
                    @Override
                    public void onResponse(Call<SingleResponse<ViewCount>> call, Response<SingleResponse<ViewCount>> response) {
                        viewCount.setText(String.valueOf(response.body().data.count));
                        viewCountCache.put(position,response.body().data);
                    }

                    @Override
                    public void onFailure(Call<SingleResponse<ViewCount>> call, Throwable t) {

                    }
                });
            }

            if(ratingCache.containsKey(position)){
                float ratingValue = ratingCache.get(position).floatValue();
                if(ratingValue < 0){
                    rating.setText(R.string.not_rated);
                }
                else
                rating.setText(String.valueOf(ratingValue));
            }
            else{
                PlaceService placeService = new PlaceService();
                placeService.getRating(place.id).enqueue(new Callback<Float>() {
                    @Override
                    public void onResponse(Call<Float> call, Response<Float> response) {
                        float ratingValue = response.body().floatValue();
                        if(ratingValue < 0){
                            rating.setText(R.string.not_rated);
                        }
                        else
                        rating.setText(String.valueOf(ratingValue));
                        ratingCache.put(position,response.body().floatValue());
                    }

                    @Override
                    public void onFailure(Call<Float> call, Throwable t) {

                    }
                });
            }

            return row;
    }

    private boolean checkExistsInTravelBook(Context context,int placeId){
        TravelBookItem[] travelBookItems = TravelBookService.getTravelBookItems(context);
        for (int i = 0; i < travelBookItems.length; i++){
            TravelBookItem current = travelBookItems[i];
            if(current.placeId == placeId){
                return true;
            }
        }
        return  false;
    }

    private void setButtonNegative(Button addTravelBook,Place place){
        addTravelBook.setText(R.string.already_in_travel_book);
        addTravelBook.setBackgroundColor(Color.parseColor("#c74a42"));

        addTravelBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelBookService.removeTravelBookItem(context,place.id);
                setButtonPositive(addTravelBook,place);
            }
        });
    }

    private void setButtonPositive(Button addTravelBook,Place place){
        addTravelBook.setText(R.string.add_to_travel_book);
        addTravelBook.setBackgroundColor(Color.parseColor("#6200EE"));
        addTravelBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelBookItem travelBookItem = new TravelBookItem();
                travelBookItem.title = place.name;
                travelBookItem.latitude = place.latitude;
                travelBookItem.longitude = place.longitude;
                travelBookItem.placeId = place.id;

                TravelBookService.addTravelBookItem(context,travelBookItem);
                setButtonNegative(addTravelBook,place);
            }
        });
    }
}
