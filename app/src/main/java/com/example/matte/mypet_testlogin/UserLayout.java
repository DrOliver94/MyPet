package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by Silvia Lombardo on 08/11/2016.
 */

public class UserLayout extends LinearLayout {

    private User user;

    private Context c;

    private ImageView uImageView;
    private TextView uName;
    private TextView uSurname;

    public UserLayout(Context context){
        super(context);
    }

    public UserLayout(Context context, User u) {

        super(context);
        c = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_user, this, true);

        //Riferimenti
        uImageView = (ImageView) findViewById(R.id.user_imageView);
        uName = (TextView) findViewById(R.id.user_name);
        uSurname = (TextView) findViewById(R.id.user_surname);

        //TODO sistemare click Listener

        //Imposta dati del post nel layout
        setUser(u);
    }

    public void setUser(User u){

        //Caricamento dati
        uName.setText(u.name);
        uSurname.setText(u.surname);

        Picasso.with(getContext()).setIndicatorsEnabled(true);
        Picasso.with(getContext())
                .load(u.profilepic)
                .resize(150, 150)   //Aggiusta le dimensioni per non pesare troppo
                .centerInside()     //Non distorce img non quadrate
                .transform(new CropCircleTransformation())
                .into(uImageView);

    }


}
