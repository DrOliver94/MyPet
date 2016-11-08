package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Silvia Lombardo on 08/11/2016.
 */

public class AnimalListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Animal> animals;

    public AnimalListAdapter(Context context, ArrayList<Animal> animals){
        this.context = context;
        this.animals = animals;
    }

    @Override
    public int getCount() {
        return animals.size();
    }

    @Override
    public Object getItem(int ani) {
        return animals.get(ani);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int ani, View view, ViewGroup viewGroup) {
        AnimalLayout animalLayout = null;
        Animal a = animals.get(ani);

        if(view == null){
            animalLayout = new AnimalLayout(context, a);
        } else {
            animalLayout = (AnimalLayout) view;
            animalLayout.setAnimal(a);
        }
        return animalLayout;
    }

}
