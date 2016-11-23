package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

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
        if(post.text != null && !post.text.isEmpty()) {
            pText.setText(post.text);
            pText.setVisibility(VISIBLE);
        } else {
            pText.setVisibility(GONE);
        }

        if(post.place != null) {
            if(!post.placeAddress.equals("")) {
                pPlace.setText(post.placeAddress);
                pPlace.setVisibility(VISIBLE);
            } else if(!post.placeName.equals("")){
                pPlace.setText(post.placeName);
                pPlace.setVisibility(VISIBLE);
            } else if(!post.placeLatLon.equals("")) {
                pPlace.setText(post.placeLatLon.toString());
                pPlace.setVisibility(VISIBLE);
            } else {
                pPlace.setVisibility(GONE);
            }
            if(post.placeLatLon != null && !post.placeLatLon.equals("")) {
                pPlace.setOnClickListener(new OnClickOpenPostListener(post.placeLatLon));
            }
        } else {
            pPlace.setVisibility(GONE);
        }

        if(post.date != null) {
            SimpleDateFormat format = new SimpleDateFormat("d MMMM y HH:mm");
            pTime.setText(format.format(post.date));
        } else {
            pTime.setVisibility(GONE);
        }

        if(post.picture != null && !post.picture.isEmpty() && !post.picture.equals("https://webdev.dibris.unige.it/~S3951060/null")) {
//            Picasso.with(getContext()).setIndicatorsEnabled(true);
            pImageView.setVisibility(VISIBLE);
            Picasso.with(getContext())
                    .load(post.picture)
                    .placeholder(R.drawable.defaultimg)
                    .resize(750, 750)   //Aggiusta le dimensioni per non pesare troppo
                    .centerInside()
                    .into(pImageView);
        } else {
            pImageView.setVisibility(GONE);
        }

        //##### TAG
        int i=0;

        //Img autore
        View vAuth = new InterprExCircleLayout(c, post.picauthor);
        vAuth.setOnClickListener(new OnClickOpenUserProfileListener(post.idauthor));
        pPicLayout.addView(vAuth, i++);

        //Img utenti
        if(post.users != null) {
            for (User u : post.users) {
                View v = new InterprExCircleLayout(c, u.profilepic);
                v.setOnClickListener(new OnClickOpenUserProfileListener(u.id));
                pPicLayout.addView(v, i++);
            }
        }

        //Img animali
        if(post.animals != null){
            for(Animal a : post.animals){
                View v = new InterprExCircleLayout(c, a.profilepic);
                v.setOnClickListener(new OnClickOpenAnimalProfileListener(a.id));
                pPicLayout.addView(v, i++);
            }
        }
//
//        pPicLayout.removeAllViews();
//        for(int i = 0; i<imgs.size(); i++){
//            String img = imgs.get(i);
//            View v = new InterprExCircleLayout(c, img);
//            v.setOnClickListener(new OnClickOpenAnimalProfileListener("10"));
//
//            pPicLayout.addView(v, i);
//        }

//        pPicLayout.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                View v = view;
//            }
//        });

    }

    public class OnClickOpenPostListener implements OnClickListener {
        LatLng latlng;

        public OnClickOpenPostListener(LatLng latlng){
            super();
            this.latlng = latlng;
        }

        @Override
        public void onClick(View view) {
            Log.d("MyPet", latlng.toString());
            Intent i = new Intent(view.getContext(), MapsActivity.class);
            i.putExtra("com.example.matte.mypet.latlng", latlng);
            view.getContext().startActivity(i);

        }
    }

    public class OnClickOpenAnimalProfileListener implements OnClickListener {
        String idAnimal;

        public OnClickOpenAnimalProfileListener(String idAnimal){
            super();
            this.idAnimal = idAnimal;
        }

        @Override
        public void onClick(View view) {
            ((HomeActivity)view.getContext()).getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, AnimalProfileFragment.newInstance(idAnimal))
                    .addToBackStack(null)
                    .commit();
        }
    }

    public class OnClickOpenUserProfileListener implements OnClickListener {
        String idUser;

        public OnClickOpenUserProfileListener(String idUser){
            super();
            this.idUser = idUser;
        }

        @Override
        public void onClick(View view) {
            ((HomeActivity)view.getContext()).getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, ProfileFragment.newInstance(idUser))
                    .addToBackStack(null)
                    .commit();
        }
    }
}

