package com.levent.rindex.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.appbar.MaterialToolbar;
import com.levent.rindex.R;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.User;
import com.levent.rindex.services.PlaceService;
import com.levent.rindex.ui.adapters.PlacesAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);

        currentUser = (User) getIntent().getSerializableExtra("User");

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupSearch();
    }

    private void setupSearch(){
        PlaceService placeService = new PlaceService();
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                Log.e("!!!!!!",String.valueOf(query.trim().length()));

                placeService.search(query).enqueue(new Callback<ListResponse<Place>>() {
                    @Override
                    public void onResponse(Call<ListResponse<Place>> call, Response<ListResponse<Place>> response) {
                        ListView resultsView = findViewById(R.id.results);
                        List<Place> results = response.body().data;
                        PlacesAdapter placesAdapter = new PlacesAdapter(SearchActivity.this,results,currentUser);
                        resultsView.setAdapter(placesAdapter);
                    }

                    @Override
                    public void onFailure(Call<ListResponse<Place>> call, Throwable t) {

                    }
                });
            }
        });
    }
}