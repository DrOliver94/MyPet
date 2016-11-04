package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by matte on 161104.
 */
public class InterprExCircleAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> imgs;

    public InterprExCircleAdapter(Context c, ArrayList<String> imgs){
        this.context = c;
        this.imgs = imgs;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public Object getItem(int i) {
        return imgs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        InterprExCircleLayout circleLayout = null;
        String img = imgs.get(i);

        if(view == null){
            circleLayout = new InterprExCircleLayout(context, img);
        } else {
            circleLayout = (InterprExCircleLayout) view;
            circleLayout.setPicture(img);
        }
        return circleLayout;
    }
}
