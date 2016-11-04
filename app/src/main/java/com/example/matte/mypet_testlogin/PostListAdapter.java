package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by matte on 161031.
 */
public class PostListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Post> posts;

    public PostListAdapter(Context context, ArrayList<Post> posts){
        this.context = context;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int pos) {
        return posts.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Restituisce il PostLayout nella posizione specificata
     *
     * @param pos
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        PostLayout postLayout = null;
        Post p = posts.get(pos);

        if(view == null){
            postLayout = new PostLayout(context, p);
        } else {
            postLayout = (PostLayout) view;
            postLayout.setPost(p);
        }
        return postLayout;
    }
}