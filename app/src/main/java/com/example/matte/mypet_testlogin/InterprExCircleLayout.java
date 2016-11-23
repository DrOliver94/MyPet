package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by matte with <3 on 161104.
 */
public class InterprExCircleLayout extends LinearLayout {

    private ImageView picture;
//    public String id;

    public InterprExCircleLayout(Context context) {
        super(context);
    }

    public InterprExCircleLayout(Context c, String img){
        super(c);

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_interprexcircle, this, true);

        //Riferimenti
        picture = (ImageView) findViewById(R.id.circleimg);

//        picture.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.main_fragment, ProfileFragment.newInstance(id))
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });

        setPicture(img);
    }

    public void setPicture(String img){
//        Picasso.with(getContext()).setIndicatorsEnabled(true);
        if(img != null && !img.isEmpty()) {
            Picasso.with(getContext())
                    .load(img)
                    .resize(150, 150)   //Limita dimensione
                    .onlyScaleDown()    //Scala solo se più grande
                    .centerInside()     //Non distorce img non quadrate
                    .transform(new CropCircleTransformation())
                    .into(picture);
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.defaultuser)
                    .resize(150, 150)   //Limita dimensione
                    .onlyScaleDown()    //Scala solo se più grande
                    .centerInside()     //Non distorce img non quadrate
                    .transform(new CropCircleTransformation())
                    .into(picture);
        }
    }
}
