package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Gestisce il layout del singolo post
 */
public class PostLayout extends LinearLayout {

    private Post post;

    private Context c;

    private TextView pText;
    private ImageView pImageView;
    private TextView pPlace;
    private ListView pPicListView;

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
        pPicListView = (ListView) findViewById(R.id.picListView);

        //TODO sistemare click Listener

        //Imposta dati del post nel layout
        setPost(p);
    }

    public void setPost(Post p){
        post = p;

        //Caricamento dati
        pText.setText(post.text);
        pPlace.setText(post.place);

        Picasso.with(getContext()).setIndicatorsEnabled(true);
        Picasso.with(getContext())
                .load(post.picture)
                .into(pImageView);

        //Immagini
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
        InterprExCircleAdapter adapter = new InterprExCircleAdapter(c, imgs);
        pPicListView.setAdapter(adapter);
    }
}

