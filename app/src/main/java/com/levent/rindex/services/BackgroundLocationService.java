package com.levent.rindex.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ServiceCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.levent.rindex.R;
import com.levent.rindex.activities.LocationGuesserActivity;
import com.levent.rindex.models.ListResponse;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.User;
import com.levent.rindex.utils.RealtimeDatabaseUtil;

import java.io.IOException;
import java.util.List;

import kotlin.random.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackgroundLocationService extends Service {

    private User user;

    private int preLast;

    //Distance
    public double distance(double lat1, double long1, double lat2,
                                            double long2) {


        double dist = org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
        return dist;
    }
    //End of distance

    private boolean notificationSent = false;

    private int sendLocationNotification(String channelId,String title,Place place){

        Intent intent = new Intent(this, LocationGuesserActivity.class);
        intent.putExtra("User",user);
        intent.putExtra("Place",place);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.drawable.rindexlogo)
                .setContentIntent(pendingIntent)
                .setContentTitle(getString(R.string.location_guesser) +" "+ title)
                .setContentText(getString(R.string.click_for_details))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        int notificationId = Random.Default.nextInt();

        NotificationManagerCompat.from(this).notify(notificationId,builder.build());
        notificationSent = true;
        return notificationId;
    }

    private void createNotificationChannel(String name,String description,String id) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location location = locationResult.getLastLocation();




        }
    };

    private void handleLocation(Location location){
        mGeoCoder = new Geocoder(BackgroundLocationService.this);
        Toast.makeText(this,"Veri geldi!!!",Toast.LENGTH_SHORT).show();
        Log.e("Location Data","income!!");
        try {
            String province = getCityNameByCoordinates(location.getLatitude(),location.getLongitude());

            PlaceService placeService = new PlaceService();
            placeService.getPlacesByProvince(province).enqueue(new Callback<ListResponse<Place>>() {
                @Override
                public void onResponse(Call<ListResponse<Place>> call, Response<ListResponse<Place>> response) {
                    if(response.isSuccessful()){
                        List<Place> places = response.body().data;

                        for(int i = 0; i < places.size(); i++){
                            Place place = places.get(i);

                            double dist = distance(location.getLatitude(),location.getLongitude(),Double.valueOf(place.latitude),Double.valueOf(place.longitude));
                            Log.e("Distance To "+place.name,String.valueOf(dist));
                            if(dist < 200){

                                RealtimeDatabaseUtil realtimeDatabaseUtil = new RealtimeDatabaseUtil("users_in_"+place.id);

                                if(preLast != place.id) {
                                    sendLocationNotification("rindexsmart", place.name,place);
                                    realtimeDatabaseUtil.addData(user);


                                    preLast = place.id;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ListResponse<Place>> call, Throwable t) {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCityNameByCoordinates(double lat, double lon) throws IOException {
        List<Address> addresses = mGeoCoder.getFromLocation(lat, lon, 10);
        if (addresses != null && addresses.size() > 0) {
            for (Address adr : addresses) {
                if (adr.getAdminArea() != null && adr.getAdminArea().length() > 0) {
                    return adr.getAdminArea();
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private static Geocoder mGeoCoder;

    @Override
    public void onCreate() {
        createNotificationChannel("Rindex Smart","Rindex Smart Notification Center","rindexsmart");
        startLocationService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        user = (User)intent.getSerializableExtra("User");
        return super.onStartCommand(intent, flags, startId);
    }

    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.rindexlogo);
        builder.setContentTitle(getString(R.string.rindex_location_service));
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText(getString(R.string.running));
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, getString(R.string.rindex_location_service), NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription(getString(R.string.running));
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1200);
        locationRequest.setWaitForAccurateLocation(true);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                handleLocation(location);
            }
        });

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                handleLocation(location);
            }
        });

        startForeground(1001, builder.build());
    }
}


