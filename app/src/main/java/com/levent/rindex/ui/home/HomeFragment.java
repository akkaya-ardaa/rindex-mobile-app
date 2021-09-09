package com.levent.rindex.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.levent.rindex.R;
import com.levent.rindex.activities.MainActivity;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.User;
import com.levent.rindex.services.PlaceService;
import com.levent.rindex.ui.adapters.PlacesAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    User currentUser;
    View root;
    private int lastIndex = 0;
    private int preLast;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);

        currentUser = (User) getActivity().getIntent().getSerializableExtra("User");

         setupPlacesList();
         return root;
    }

    private void getProvincePlaces(String province){
        PlaceService placeService = new PlaceService();
        placeService.getPlacesByProvince(province).enqueue(new Callback<ListResponse<Place>>() {
            @Override
            public void onResponse(Call<ListResponse<Place>> call, Response<ListResponse<Place>> response) {
                List<Place> places = response.body().data;
                ListView placesListView = root.findViewById(R.id.places_list);

                Place[] placesArray = new Place[places.size()];
                places.toArray(placesArray);

                PlacesAdapter placesAdapter = new PlacesAdapter(getContext(),places,currentUser);
                placesListView.setAdapter(placesAdapter);

                RelativeLayout spinner = root.findViewById(R.id.loading);
                spinner.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<ListResponse<Place>> call, Throwable t) {

            }
        });

    }

    private void setupPlacesList(){
        //önce biraz veri çekelim...
        ListView placesListView = root.findViewById(R.id.places_list);
        PlaceService placeService = new PlaceService();
        placeService.getPlacesByIndex(lastIndex,lastIndex+10).enqueue(new Callback<ListResponse<Place>>() {
            @Override
            public void onResponse(Call<ListResponse<Place>> call, Response<ListResponse<Place>> response) {
                Log.e("Hi","init");
                List<Place> places = response.body().data;
                PlacesAdapter placesAdapter = new PlacesAdapter(getContext(),places,currentUser);
                placesListView.setAdapter(placesAdapter);

                RelativeLayout spinner = root.findViewById(R.id.loading);
                spinner.setVisibility(View.INVISIBLE);
                lastIndex = lastIndex + 10;

                placesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        final int lastItem = firstVisibleItem + visibleItemCount;

                        if(lastItem == totalItemCount)
                        {
                            if(preLast!=lastItem)
                            {
                                spinner.setVisibility(View.VISIBLE);
                                //to avoid multiple calls for last item
                                Log.d("Last", "Last");
                                preLast = lastItem;

                                placeService.getPlacesByIndex(lastIndex,lastIndex + 10).enqueue(new Callback<ListResponse<Place>>() {
                                    @Override
                                    public void onResponse(Call<ListResponse<Place>> call, Response<ListResponse<Place>> response) {

                                        List<Place> newPlaces = response.body().data;
                                        for (int i = 0; i < newPlaces.size(); i++){
                                            places.add(newPlaces.get(i));
                                        }
                                        placesAdapter.notifyDataSetChanged();
                                        lastIndex = lastIndex + 10;
                                        spinner.setVisibility(View.INVISIBLE);

                                    }

                                    @Override
                                    public void onFailure(Call<ListResponse<Place>> call, Throwable t) {

                                    }
                                });

                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ListResponse<Place>> call, Throwable t) {

            }
        });
    }
}