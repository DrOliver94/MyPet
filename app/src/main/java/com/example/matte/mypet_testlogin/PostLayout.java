package com.example.matte.mypet_testlogin;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Gestisce il layout del singolo post
 */

public class PostLayout extends LinearLayout {

    private Post post;

    private Context c;

    private TextView pText;
    private TextView pPlace;
    private TextView pTime;
    private ImageView pImageView;
    private LinearLayout pPicLayout;

    public PostLayout(Context context){
        super(context);
    }

    public PostLayout(Context context, Post p){
        super(context);

        c = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_post, this, true);

        //Riferimenti
        pText = (TextView) findViewById(R.id.post_text);
        pPlace = (TextView) findViewById(R.id.post_place);
        pImageView = (ImageView) findViewById(R.id.post_imageView);
        pPicLayout = (LinearLayout) findViewById(R.id.picListView);
        pTime = (TextView) findViewById(R.id.post_time);

        //TODO sistemare click Listener
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        //Imposta dati del post nel layout
        setPost(p);
    }

    public void setPost(Post post){

        //Caricamento dati
        pText.setText(post.text);
        if(post.place != null) {
            if(!post.placeAddress.equals("")) {
                pPlace.setText(post.placeAddress);
            } else {
                pPlace.setText(post.placeName);
            }

        }
        if(post.date != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd MMMM y HH:mm");
            pTime.setText(format.format(post.date));
        }

        if(post.picture != null && !post.picture.isEmpty()) {
            Picasso.with(getContext()).setIndicatorsEnabled(true);
            Picasso.with(getContext())
                    .load(post.picture)
                    .resize(750, 750)   //Aggiusta le dimensioni per non pesare troppo
                    .centerInside()
                    .into(pImageView);
        } else {
            //TODO pulire img
        }

        //Tag
        ArrayList<String> imgs = new ArrayList<>();
        imgs.add(post.picauthor);
        if(post.users != null) {
            for (User u : post.users) {
                imgs.add(u.profilepic);
            }
        }
        if(post.animals != null){
            for(Animal a : post.animals){
                imgs.add(a.profilepic);
            }
        }

        pPicLayout.removeAllViews();
        for(int i = 0; i<imgs.size(); i++){
            String img = imgs.get(i);
            View v = new InterprExCircleLayout(c, img, "id");
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    View v = view;
                }
            });
            pPicLayout.addView(v, i);
        }

//        pPicLayout.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                View v = view;
//            }
//        });

    }
}

