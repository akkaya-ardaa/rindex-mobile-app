package com.levent.rindex.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.levent.rindex.R;
import com.levent.rindex.models.Place;
import com.levent.rindex.models.User;
import com.levent.rindex.ui.adapters.UsersAdapter;
import com.levent.rindex.utils.RealtimeDatabaseUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LiveChatListActivity extends AppCompatActivity {

    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat_list);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        place = (Place)getIntent().getSerializableExtra("Place");
        listUsersAvailable();
    }

    private void listUsersAvailable(){
        RealtimeDatabaseUtil realtimeDatabaseUtil = new RealtimeDatabaseUtil("users_in_"+place.id);
        realtimeDatabaseUtil.database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    users.add(user);
                }

                ListView usersView = findViewById(R.id.users);
                UsersAdapter usersAdapter = new UsersAdapter(LiveChatListActivity.this,users);
                usersView.setAdapter(usersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}