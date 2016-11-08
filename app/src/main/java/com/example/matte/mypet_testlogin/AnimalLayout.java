package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by Silvia Lombardo on 08/11/2016.
 */

public class AnimalLayout extends LinearLayout {

    private Animal animal;

    private Context c;

    private TextView aName;
    private ImageView aImageView;

    public AnimalLayout(Context context){
        super(context);
    }

    public AnimalLayout(Context context, Animal a) {

        super(context);
        c = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_animal, this, true);

        //Riferimenti
        aName = (TextView) findViewById(R.id.animal_name);
        aImageView = (ImageView) findViewById(R.id.animal_imageView);

        //TODO sistemare click Listener

        //Imposta dati del post nel layout
        setAnimal(a);
    }

    public void setAnimal(Animal animal){

        //Caricamento dati
        aName.setText(animal.name);

        Picasso.with(getContext()).setIndicatorsEnabled(true);
        Picasso.with(getContext())
                .load(animal.profilepic)
                .resize(150, 150)   //Aggiusta le dimensioni per non pesare troppo
                .centerInside()     //Non distorce img non quadrate
                .transform(new CropCircleTransformation())
                .into(aImageView);

    }

}
