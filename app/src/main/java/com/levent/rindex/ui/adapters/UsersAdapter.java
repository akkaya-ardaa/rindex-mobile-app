package com.levent.rindex.ui.adapters;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.levent.rindex.R;
import com.levent.rindex.models.User;

import java.util.List;

public class UsersAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> users;

    public UsersAdapter(@NonNull Context context, List<User> users) {
        super(context, R.layout.user_row);
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View root = layoutInflater.inflate(R.layout.user_row,null);

        ImageView profilePhoto = root.findViewById(R.id.profile_photo);
        TextView username = root.findViewById(R.id.username);

        username.setText(users.get(position).username);
        Glide.with(context).load(users.get(position).photoUrl).error(R.drawable.rindexlogo).into(profilePhoto);

        return root;
    }
}
