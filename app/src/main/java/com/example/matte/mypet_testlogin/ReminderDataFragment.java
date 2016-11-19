package com.example.matte.mypet_testlogin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
    private static final String ARG_PARAM1 = "idReminder";
    private static final String ARG_PARAM2 = "isEdit";

    private String idReminder;
    private Boolean isEdit;

    public String chosenDate;
    public String chosenTime;

    private EditText rNameEditText;
    private EditText rPlaceEditText;
    private Button sendReminder;
    private Spinner spinAnimals;
    private TextView rDateTextView;
    private ImageButton changeReminderDate;
    private TextView rTimeTextView;
    private ImageButton changeReminderTime;

    private SharedPreferences shPref;

    private String idUser;

    public ReminderDataFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idReminder Parameter 1.
     * @param isEdit Parameter 2.
     * @return A new instance of fragment ReminderDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReminderDataFragment newInstance(String idReminder, Boolean isEdit) {
        ReminderDataFragment fragment = new ReminderDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, idReminder);
        args.putBoolean(ARG_PARAM2, isEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idReminder = getArguments().getString(ARG_PARAM1);
            isEdit = getArguments().getBoolean(ARG_PARAM2);
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
        rTimeTextView = (TextView) view.findViewById(R.id.reminderTimeTextView);

        //Button invio richiesta insert/update
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

        //Bottoni Cambio data/ora
        changeReminderDate = (ImageButton) view.findViewById(R.id.changeReminderDateButton);
        changeReminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        changeReminderTime = (ImageButton) view.findViewById(R.id.changeReminderTimeButton);
        changeReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

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

        //TODO controllare presenza data

        Animal selectedAnim = (Animal) spinAnimals.getSelectedItem();

        //Inviare richiesta al server per l'update
        InsertReminderTask insertReminder = new InsertReminderTask(shPref.getString("Token", ""));
        insertReminder.execute(nameTxt, placeTxt, chosenDate, chosenTime, selectedAnim.id, idUser);
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
                String eventdate = p[2];
                String eventhour = p[3];
                reminder.idanim = p[4];
                reminder.iduser = p[5];

                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd HH:mm:ss");
                try {
                    reminder.eventtime = format.parse(eventdate + " " + eventhour);
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
                        "&time=" + eventdate + " " + eventhour +
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
                    long idRem = HomeActivity.dbManager.insertReminder(reminder);

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

    public DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener(){

        public Date newDate;

        @Override
        public void onDateSet(DatePicker view, int Year, int monthOfYear, int dayOfMonth) {
            chosenDate = new StringBuilder()
                    .append(Year).append("-")
                    .append(monthOfYear+1).append("-")
                    .append(dayOfMonth).toString();

            SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
            try {
                newDate = format.parse(chosenDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            format = new SimpleDateFormat("dd MMMM y");
            rDateTextView.setText(format.format(newDate));

        }
    };

    public void showDatePickerDialog() {
        //TODO fare solo se newDate vuoto

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Dialog newD = new DatePickerDialog(getActivity(), datePickerListener, year, month, day);
        newD.show();
    }

    public TimePickerDialog.OnTimeSetListener timePickerListener
            = new TimePickerDialog.OnTimeSetListener(){

        public Date newTime;

        @Override
        public void onTimeSet(TimePicker timePicker, int h, int m) {
            chosenTime = new StringBuilder()
                    .append(h).append(":")
                    .append(m).append(":")
                    .append("00").toString();

            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            try {
                newTime = format.parse(chosenTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            format = new SimpleDateFormat("HH:mm");
            rTimeTextView.setText(format.format(newTime));
        }

    };

    public void showTimePickerDialog() {
        //TODO fare solo se newTime vuoto

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        Dialog newD = new TimePickerDialog(getActivity(), timePickerListener, hour, min, true);
        newD.show();
    }

}
