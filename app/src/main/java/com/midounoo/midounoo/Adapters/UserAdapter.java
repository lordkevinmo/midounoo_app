package com.midounoo.midounoo.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.midounoo.midounoo.Model.User;
import com.midounoo.midounoo.R;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends ArrayAdapter<User> {

    public UserAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = ((Activity) getContext())
                    .getLayoutInflater()
                    .inflate(R.layout.activity_user, parent, false);
        }

        CircleImageView profil = convertView.findViewById(R.id.profil);
        TextView nom = convertView.findViewById(R.id.nomBdd);
        TextView prenom = convertView.findViewById(R.id.prenombdd);
        TextView email = convertView.findViewById(R.id.mailbdd);
        TextView phone = convertView.findViewById(R.id.phoneNumber);

        User user = getItem(position);

        boolean isPhoto = user.getPhotoUrl() != null;
        if (isPhoto){
            Glide.with(profil.getContext())
                    .load(user.getPhotoUrl())
                    .into(profil);
        } else {
            Glide.with(profil.getContext())
                    .load(R.drawable.logo)
                    .into(profil);
        }
        nom.setText(user.getName());
        prenom.setText(user.getfName());
        email.setText(user.getEmail());
        phone.setText(user.getNumberPhone());

        return convertView;
    }
}
