package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by matte with <3 on 161104.
 */
public class InterprExCircleLayout extends LinearLayout {

    private ImageView picture;

    public InterprExCircleLayout(Context context) {
        super(context);
    }

    public InterprExCircleLayout(Context c, String img){
        super(c);

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_interprexcircle, this, true);

        //Riferimenti
        picture = (ImageView) findViewById(R.id.circleimg);

        setPicture(img);
    }

    public void setPicture(String img){
        Picasso.with(getContext()).setIndicatorsEnabled(true);
        Picasso.with(getContext())
                .load(img)
                .resize(150, 150)   //Limita dimensione
                .centerInside()     //Non distorce img non quadrate
                .transform(new CropCircleTransformation())
                .into(picture);
    }
}
