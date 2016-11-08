package com.example.matte.mypet_testlogin;

import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Silvia Lombardo on 08/11/2016.
 */

public class UserListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<User> users;

    public UserListAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int us) {
        return users.get(us);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int us, View view, ViewGroup viewGroup) {
        UserLayout userLayout = null;
        User u = users.get(us);

        if(view == null){
            userLayout = new UserLayout(context, u);
        } else {
            userLayout = (UserLayout) view;
            userLayout.setUser(u);
        }
        return userLayout;
    }

}
