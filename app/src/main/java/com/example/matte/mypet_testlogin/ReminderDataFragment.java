package com.example.matte.mypet_testlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReminderDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReminderDataFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText rNameEditText;
    private EditText rPlaceEditText;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button sendReminder;

    private SharedPreferences shPref;

    private String idUser;

    public ReminderDataFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReminderDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReminderDataFragment newInstance(String param1, String param2) {
        ReminderDataFragment fragment = new ReminderDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        shPref = getActivity().getSharedPreferences("MyPetPrefs", Context.MODE_PRIVATE);
        idUser = shPref.getString("IdUser", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminder_data, container, false);

        rNameEditText = (EditText) view.findViewById(R.id.reminderEventEditText);
        rPlaceEditText = (EditText) view.findViewById(R.id.reminderPlaceEditText);

        sendReminder = (Button) view.findViewById(R.id.buttonSendReminderData);
        sendReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertReminder();
            }
        });

        getActivity().setTitle("Nuovo promemoria");

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void insertReminder() {
        //Recuperare dati
        //TODO fare controlli. usare TextView.setError()
        String nameTxt = rNameEditText.getText().toString();
        String placeTxt = rPlaceEditText.getText().toString();
        String dateTxt = "date";
        String idAnim = "9";
        //TODO gestire data

        //Inviare richiesta al server per l'update
        InsertReminderTask insertReminder = new InsertReminderTask(shPref.getString("Token", ""));
        insertReminder.execute(nameTxt, placeTxt, dateTxt, idAnim, idUser);
    }


    /**
     * AsyncTask che inserisce un nuovo promemoria
     */
    public class InsertReminderTask extends AsyncTask<String, Integer, JSONObject> {
        private String uToken = "";
        private final Reminder reminder;

        private ServerComm serverComm;

        private ProgressDialog pDialog;

        InsertReminderTask(String token) {
            reminder = new Reminder();

            uToken = token;

            pDialog = new ProgressDialog(getContext());
            serverComm = new ServerComm();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("MyPet", "insertReminder start");

            pDialog.setTitle("Insert promemoria");
            pDialog.setMessage("Richiesta al server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... p) {
            try {
                reminder.eventname = p[0];
                reminder.eventplace = p[1];
                reminder.eventtime = p[2];
                reminder.idanim = p[3];
                reminder.iduser = p[4];

                Animal a = HomeActivity.dbManager.getAnimal(reminder.idanim);
                reminder.animpic = a.profilepic;

                String postArgs = "insertReminder=" +
                        "&iduser=" + reminder.iduser +
                        "&token=" + uToken +
                        "&name=" + reminder.eventname +
                        "&place=" + reminder.eventplace +
                        "&time=" + reminder.eventtime +
                        "&idanim=" + reminder.idanim;

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
                    //Recupera id reminder
                    reminder.id = jObj.getString("idremind");

                    //Se va a buon fine, update nel DB locale
                    //TODO controlla buon fine
                    long numRows = HomeActivity.dbManager.insertReminder(reminder);

                    //Aggiorna token nelle SharedPref
                    shPref.edit().putString("Token", jObj.getString("token")).apply();
                    //TODO ripulisci shPref, copia login

                    //Torna al fragment precedente
                    getFragmentManager().popBackStack();

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