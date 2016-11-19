package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by matte on 161119.
 */

public class ReminderListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Reminder> reminders;

    public ReminderListAdapter(Context context, ArrayList<Reminder> reminders){
        this.context = context;
        this.reminders = reminders;
    }

    @Override
    public int getCount() {
        return reminders.size();
    }

    @Override
    public Object getItem(int pos) {
        return reminders.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        ReminderLayout reminderLayout = null;
        Reminder r = reminders.get(pos);

//        if(view == null){
        if(true){
            reminderLayout = new ReminderLayout(context, r);
        } else {
            reminderLayout = (ReminderLayout) view;
            reminderLayout.setReminder(r);
        }

        return reminderLayout;
    }
}