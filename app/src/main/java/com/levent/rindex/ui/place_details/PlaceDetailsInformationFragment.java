package com.levent.rindex.ui.place_details;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.levent.rindex.activities.PlaceCommentsActivity;
import com.levent.rindex.R;
import com.levent.rindex.constants.APIConstants;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.User;

import java.util.Locale;

public class PlaceDetailsInformationFragment extends Fragment {

    User currentUser;
    WebView content;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_place_details_information, container, false);

        currentUser = (User) getActivity().getIntent().getSerializableExtra("User");

        Place place = (Place) getActivity().getIntent().getSerializableExtra("Place");
        ImageView image = root.findViewById(R.id.image);
        TextView title = root.findViewById(R.id.title);
        Button comments = root.findViewById(R.id.comments);
        TextView provinceDistrict = root.findViewById(R.id.province_district);
        content = root.findViewById(R.id.content);

        provinceDistrict.setText(place.province + " • " + place.district);
        title.setText(place.name);
        Glide.with(this).load(place.imageLink).into(image);
        content.getSettings().setJavaScriptEnabled(true);
        content.loadUrl(APIConstants.Url+"api/places/ViewContent?placeId="+place.id+"&language="+ Locale.getDefault().getISO3Language());

        Log.e("URL Watcher",APIConstants.Url+"api/places/ViewContent?placeId="+place.id+"&language="+ Locale.getDefault().getISO3Language());


        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),PlaceCommentsActivity.class);
                intent.putExtra("placeId",place.id);
                intent.putExtra("User",currentUser);
                startActivity(intent);
            }
        });

        return root;
    }
}