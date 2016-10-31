package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Gestisce il layout del post
 */

public class PostLayout extends LinearLayout {

    private Post post;

    private TextView pText;
    private ImageView pImageView;

    public PostLayout(Context context){
        super(context);
    }

    public PostLayout(Context context, Post p){
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_post, this, true);

        //Riferimenti
        pText = (TextView) findViewById(R.id.post_text);
        pImageView = (ImageView) findViewById(R.id.post_imageView);

        //TODO sistemare click Listener

        //Imposta dati del post nel layout
        setPost(p);
    }

    public void setPost(Post p){
        post = p;

        //Caricamento dati
        pText.setText(post.text);
        Picasso.with(getContext()).setIndicatorsEnabled(true);
        Picasso.with(getContext())
                .load(HomeActivity.IMG_BASEURL + post.picture)
                .into(pImageView);
    }
}

