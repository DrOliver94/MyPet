package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Gestisce il layout del singolo reminder
 */

public class ReminderLayout extends LinearLayout {

    private Reminder reminder;

    private TextView rEventTxtView;
    private TextView rPlaceTxtView;
    private TextView rTimeTxtView;
    private ImageView rAnimPic;
    private TextView rAnimName;

    public ReminderLayout(Context context){
        super(context);
    }

    public ReminderLayout(Context context, Reminder r){
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_reminder, this, true);

        //Riferimenti
        rEventTxtView = (TextView) findViewById(R.id.reminder_eventname);
        rPlaceTxtView = (TextView) findViewById(R.id.reminder_eventplace);
//        rTimeTxtView = (TextView) findViewById(R.id.reminder_eventtime);
        rAnimPic = (ImageView) findViewById(R.id.reminder_animpic);
        rAnimName = (TextView) findViewById(R.id.reminder_animname);

        //TODO sistemare click Listener?
//        this.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return false;
//            }
//        });

        //Imposta dati del post nel layout
        setReminder(r);
    }

    public void setReminder(Reminder r){
        rEventTxtView.setText(r.eventname);

        rAnimName.setText(r.animname);
        Picasso.with(getContext()).setIndicatorsEnabled(false);
        Picasso.with(getContext())
                .load(r.animpic)
                .resize(130, 130)   //Limita dimensione
                .centerInside()     //Non distorce img non quadrate
                .transform(new CropCircleTransformation())
                .into(rAnimPic);

        //Caricamento dati
        //TODO gestire place
        if(r.eventplace != null) {
            SimpleDateFormat format = new SimpleDateFormat("d MMMM y HH:mm");
            rPlaceTxtView.setText(r.eventplace + " - " + format.format(r.eventtime));
        } else {
            rPlaceTxtView.setVisibility(GONE);
        }

//        if(r.eventtime != null) {
//            SimpleDateFormat format = new SimpleDateFormat("dd MMMM y HH:mm");
//            rTimeTxtView.setText(format.format(r.eventtime));
//        }







        //TODO leggere latlng
//        if(r.place != null) {
//            if(!r.placeAddress.equals("")) {
//                pPlace.setText(r.placeAddress);
//            } else {
//                pPlace.setText(r.placeName);
//            }
//            pPlace.setOnClickListener(new OnClickOpenPostListener(r.placeLatLon));
//        } else {
//            pPlace.setVisibility(GONE);
//        }

    }

//    public class OnClickOpenPostListener implements OnClickListener {
//        LatLng latlng;
//
//        public OnClickOpenPostListener(LatLng latlng){
//            super();
//            this.latlng = latlng;
//        }
//
//        @Override
//        public void onClick(View view) {
//            Log.d("MyPet", latlng.toString());
//            Intent i = new Intent(view.getContext(), MapsActivity.class);
//            i.putExtra("com.example.matte.mypet.latlng", latlng);
//            view.getContext().startActivity(i);
//
//        }
//    }
//
//    public class OnClickOpenAnimalProfileListener implements OnClickListener {
//        String idAnimal;
//
//        public OnClickOpenAnimalProfileListener(String idAnimal){
//            super();
//            this.idAnimal = idAnimal;
//        }
//
//        @Override
//        public void onClick(View view) {
//            ((HomeActivity)view.getContext()).getFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.main_fragment, AnimalProfileFragment.newInstance(idAnimal))
//                    .addToBackStack(null)
//                    .commit();
//        }
//    }
//
//    public class OnClickOpenUserProfileListener implements OnClickListener {
//        String idUser;
//
//        public OnClickOpenUserProfileListener(String idUser){
//            super();
//            this.idUser = idUser;
//        }
//
//        @Override
//        public void onClick(View view) {
//            ((HomeActivity)view.getContext()).getFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.main_fragment, ProfileFragment.newInstance(idUser))
//                    .addToBackStack(null)
//                    .commit();
//        }
//    }
}

