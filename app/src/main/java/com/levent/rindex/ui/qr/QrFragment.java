package com.levent.rindex.ui.qr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;
import com.levent.rindex.R;
import com.levent.rindex.activities.PlaceDetailsActivity;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.SingleResponse;
import com.levent.rindex.models.User;
import com.levent.rindex.services.PlaceService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrFragment extends Fragment {

    private CodeScanner mCodeScanner;
    private CodeScannerView codeScannerView;

    private User user;

    private final int requestCode = 0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qr, container, false);

        codeScannerView = root.findViewById(R.id.code_scanner);

        requestCameraPermission();
        getUser();
        return root;
    }

    private void requestCameraPermission(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},requestCode);
        }
        else{
            initReader();
        }
    }

    private void initReader(){
        Log.e("Reader Init!","Method run!");
        mCodeScanner =  new CodeScanner(getContext(),codeScannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                String decodedUrl = result.getText();
                Log.e("Decode Detected!","URL Decoded! => "+decodedUrl);

                try{
                    String[] explodedUrl = decodedUrl.split(":");
                    if(!explodedUrl[0].equals("rindexstart")){
                        Log.e("Module","Module error!");
                        Snackbar.make(codeScannerView,R.string.qr_not_valid,Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                        PlaceService placeService = new PlaceService();

                        int toStartPlaceId = Integer.valueOf(explodedUrl[1]);
                        placeService.getPlaceById(toStartPlaceId).enqueue(new Callback<SingleResponse<Place>>() {
                            @Override
                            public void onResponse(Call<SingleResponse<Place>> call, Response<SingleResponse<Place>> response) {

                                if(response.body() != null){
                                    Place place = response.body().data;

                                    Intent intent = new Intent(getContext(), PlaceDetailsActivity.class);
                                    intent.putExtra("Place",place);
                                    intent.putExtra("User",user);

                                    startActivity(intent);
                                }
                                else{
                                    Snackbar.make(codeScannerView,R.string.qr_not_valid,Snackbar.LENGTH_SHORT).show();

                                }

                            }

                            @Override
                            public void onFailure(Call<SingleResponse<Place>> call, Throwable t) {
                                Log.e("Server Result Error!","Server returned BadRequest");
                            }
                        });


                    }
                }
                catch(Exception exception){
                    Log.e("QR Scanner Result","Scanner detected this is not a valid QR!");
                    Snackbar.make(codeScannerView,R.string.qr_not_valid,Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            initReader();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mCodeScanner != null)
            mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        if(mCodeScanner != null)
            mCodeScanner.releaseResources();
        super.onPause();
    }

    private void getUser(){
        user = (User)getActivity().getIntent().getSerializableExtra("User");
    }
}