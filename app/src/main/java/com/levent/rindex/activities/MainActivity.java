package com.levent.rindex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.levent.rindex.R;
import com.levent.rindex.ccs.SavedSettings;
import com.levent.rindex.models.TravelBookItem;
import com.levent.rindex.models.User;
import com.levent.rindex.services.BackgroundLocationService;
import com.levent.rindex.services.TravelBookService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_travel_book, R.id.navigation_qr,R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        currentUser = (User) getIntent().getSerializableExtra("User");

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        setNotificationToken();
        startBackgroundService();
    }

    private void setNotificationToken(){
        Log.e("Notification Token", "Token: "+SavedSettings.get("Notification_Token",MainActivity.this));
    }

    private void startBackgroundService(){
        Intent intent = new Intent(MainActivity.this, BackgroundLocationService.class);
        intent.putExtra("User",currentUser);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_filter){
            Intent intent = new Intent(MainActivity.this,SearchActivity.class);
            intent.putExtra("User",currentUser);
            startActivity(intent);
        }
        return true;
    }
}