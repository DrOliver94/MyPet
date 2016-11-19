package com.example.matte.mypet_testlogin;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private Button changeReminderDate;
    private Spinner spinAnimals;
    private TextView rDateTextView;
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

        //Riferimenti UI
        rNameEditText = (EditText) view.findViewById(R.id.reminderEventEditText);
        rPlaceEditText = (EditText) view.findViewById(R.id.reminderPlaceEditText);
        rDateTextView = (TextView) view.findViewById(R.id.reminderDateTextView);

        //Due button
        sendReminder = (Button) view.findViewById(R.id.buttonSendReminderData);
        sendReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertReminder();
            }
        });

        //Spinner
        ArrayList<Animal> animals = HomeActivity.dbManager.getAnimalsByOwner(idUser);
//        ArrayAdapter<Animal> adapter = new ArrayAdapter<Animal>(getActivity(), android.R.layout.simple_spinner_item, animals);
//        ArrayAdapter<Animal> adapter = ArrayAdapter.createFromResource(getActivity(), android.R.layout.simple_spinner_item, R.layout.spinner_layout);

        ArrayAdapter<Animal> adapter = new ArrayAdapter<Animal>(getActivity(), R.layout.spinner_layout, animals);
        spinAnimals = (Spinner) view.findViewById(R.id.reminderDataAnimalSpin);
        spinAnimals.setAdapter(adapter);

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
        String dateTxt = "2015-12-08 09:52:00";    //TODO gestire data
        Animal selectedAnim = (Animal) spinAnimals.getSelectedItem();

        //Inviare richiesta al server per l'update
        InsertReminderTask insertReminder = new InsertReminderTask(shPref.getString("Token", ""));
        insertReminder.execute(nameTxt, placeTxt, dateTxt, selectedAnim.id, idUser);
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

            pDialog = new ProgressDialog(getActivity());
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
                String eventtime = p[2];
                reminder.idanim = p[3];
                reminder.iduser = p[4];

                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd HH:mm:ss");
                try {
                    reminder.eventtime = format.parse(eventtime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Animal a = HomeActivity.dbManager.getAnimal(reminder.idanim);
                reminder.animpic = a.profilepic;

                String postArgs = "insertReminder=" +
                        "&iduser=" + reminder.iduser +
                        "&token=" + uToken +
                        "&name=" + reminder.eventname +
                        "&place=" + reminder.eventplace +
                        "&time=" + eventtime +
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

                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                    //Torna al fragment precedente
                    getFragmentManager().popBackStack();

                    return;
                } else {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                    Toast.makeText(getActivity(), jObj.getString("error"), Toast.LENGTH_SHORT).show();

                    //Aggiorna token nelle SharedPref
                    shPref.edit().putString("Token", jObj.getString("token")).apply();

                    //TODO indicare l'errore all'utente. es: uUsernameEditTxt.setError();
                    return;
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

    public int year;
    public int month;
    public int day;
    public String newDate;

    public DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener(){

        public Date newBirthDate;

        @Override
        public void onDateSet(DatePicker view, int Year, int monthOfYear, int dayOfMonth) {
            newDate = new StringBuilder()
                    .append(Year).append("-")
                    .append(monthOfYear+1).append("-")
                    .append(dayOfMonth).toString();

            SimpleDateFormat format = new SimpleDateFormat("y-MM-dd HH:mm:ss");
            try {
                newBirthDate = format.parse(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            format = new SimpleDateFormat("dd MMMM y");
            rDateTextView.setText(format.format(newBirthDate));

        }
    };

    public void showDatePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        Dialog newD = new DatePickerDialog(getActivity(), datePickerListener, year, month, day);
        newD.show();
    }



}
