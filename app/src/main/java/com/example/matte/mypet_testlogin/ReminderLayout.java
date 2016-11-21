package com.example.matte.mypet_testlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Gestisce il layout del singolo reminder
 */

public class ReminderLayout extends LinearLayout {
    private Reminder reminder;

    private SharedPreferences shPref;

    private TextView rEventTxtView;
    private TextView rPlaceTxtView;
    private ImageView rAnimPic;
    private TextView rAnimName;
    private ImageButton rDeleteBtn;


    public ReminderLayout(Context context){
        super(context);
    }

    public ReminderLayout(Context context, Reminder r){
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_reminder, this, true);

        shPref = context.getSharedPreferences("MyPetPrefs", Context.MODE_PRIVATE);

        //Riferimenti
        rEventTxtView = (TextView) findViewById(R.id.reminder_eventname);
        rPlaceTxtView = (TextView) findViewById(R.id.reminder_eventplace);
//        rTimeTxtView = (TextView) findViewById(R.id.reminder_eventtime);
        rAnimPic = (ImageView) findViewById(R.id.reminder_animpic);
        rAnimName = (TextView) findViewById(R.id.reminder_animname);
        rDeleteBtn = (ImageButton) findViewById(R.id.reminderDeleteBtn);

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

    public void setReminder(final Reminder r){
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

        rDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteReminder(view, r.id);
            }
        });



//        if(r.eventtime != null) {
//            SimpleDateFormat format = new SimpleDateFormat("dd MMMM y HH:mm");
//            rTimeTxtView.setText(format.format(r.eventtime));
//        }




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


    public void deleteReminder(View view, String idReminder){
        DeleteReminderTask deleteReminder = new DeleteReminderTask(shPref.getString("Token", ""), shPref.getString("IdUser", ""), view);
        deleteReminder.execute(idReminder);
    }

    /**
     * AsyncTask che aggiorna l'utente inviato
     */
    public class DeleteReminderTask extends AsyncTask<String, Integer, JSONObject> {
        private View v;

        private String idReminder;
        private String token;
        private String idUser;

        private ServerComm serverComm;

        private ProgressDialog pDialog;

        DeleteReminderTask(String token, String idUser, View v) {
            this.token = token;
            this.idUser = idUser;
            pDialog = new ProgressDialog(v.getContext());
            serverComm = new ServerComm();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("MyPet", "updateUser start");

            pDialog.setTitle("Eliminazione promemoria");
            pDialog.setMessage("Richiesta al server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... p) {
            try {
                idReminder = p[0];

                String postArgs = "deleteReminder=" + idReminder +
                                "&iduser=" + idUser +
                                "&token=" + token;

                Log.d("MyPet", postArgs);

                return serverComm.makePostRequest(postArgs);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            try {
                if(!jObj.isNull("success") && jObj.getBoolean("success")) {
                    //Se va a buon fine, delete nel DB locale
                    //TODO controlla buon fine
                    long numRows = HomeActivity.dbManager.deleteReminder(idReminder);

                    //Aggiorna token nelle SharedPref
                    shPref.edit().putString("Token", jObj.getString("token")).apply();

                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                    ((HomeActivity) v.getContext()).getFragmentManager().popBackStack();

                } else {

                    //TODO indicare l'errore all'utente. es: uUsernameEditTxt.setError();

                }
            } catch(Exception e) {
                e.fillInStackTrace();
            }

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }


        @Override
        protected void onCancelled() {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }


}

