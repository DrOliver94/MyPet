package com.example.matte.mypet_testlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link UserDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDataFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "idUser";
    private static final String ARG_PARAM2 = "isEdit";

    // TODO: Rename and change types of parameters
    private String idUser;
    private Boolean isEdit;

    private SharedPreferences shPref;

    private EditText uUsernameEditTxt;
    private EditText uNameEditTxt;
    private EditText uSurnameEditTxt;
    private EditText uGenderEditTxt;
    private EditText uBirthdateEditTxt;
    private Button sendData;

//    private OnFragmentInteractionListener mListener;

    public UserDataFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idUser Parameter 1.
     * @return A new instance of fragment UserDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDataFragment newInstance(String idUser, Boolean isEdit) {
        UserDataFragment fragment = new UserDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, idUser);
        args.putBoolean(ARG_PARAM2, isEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idUser = getArguments().getString(ARG_PARAM1);
            isEdit = getArguments().getBoolean(ARG_PARAM2);
        }

        shPref = getActivity().getSharedPreferences("MyPetPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_data, container, false);

        User u = HomeActivity.dbManager.getUser(idUser);

        uUsernameEditTxt = (EditText) view.findViewById(R.id.userUsernameEditText);
        uNameEditTxt = (EditText) view.findViewById(R.id.userNameEditText);
        uSurnameEditTxt = (EditText) view.findViewById(R.id.userSurnameEditText);
        uGenderEditTxt = (EditText) view.findViewById(R.id.userGenderEditText);
        uBirthdateEditTxt = (EditText) view.findViewById(R.id.userBirthDateEditText);

        sendData = (Button) view.findViewById(R.id.buttonSendUserData);

        if (isEdit) {     //Se si vuole modificare i dati di un user
            uUsernameEditTxt.setText(u.username);
            uNameEditTxt.setText(u.name);
            uSurnameEditTxt.setText(u.surname);
            uGenderEditTxt.setText(u.gender);
            uBirthdateEditTxt.setText(u.birthdate);

            sendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                updateUser();
                }
            });

            getActivity().setTitle("Modifica utente");
        } else {
            getActivity().setTitle("Nuovo utente");
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updateUser() {
        //Recuperare dati

        //TODO fare controlli se necessario
        String usernameTxt = uUsernameEditTxt.getText().toString();
        String nameTxt = uNameEditTxt.getText().toString();
        String surnameTxt = uSurnameEditTxt.getText().toString();
        String genderTxt = uGenderEditTxt.getText().toString();
        String birthdateTxt = uBirthdateEditTxt.getText().toString();   //TODO gestire data
        String profilepicTxt = "profilepic";

        //Inviare richiesta al server per l'update
        UpdateUserTask updateUser = new UpdateUserTask(shPref.getString("Token", ""), idUser);
        updateUser.execute(usernameTxt, nameTxt, surnameTxt, genderTxt, birthdateTxt, profilepicTxt);
    }

    /**
     * Aggiorna l'animale inviato
     */
    public class UpdateUserTask extends AsyncTask<String, Integer, JSONObject> {
        private final String uToken;
        private final User user;

        private ServerComm serverComm;

        private ProgressDialog pDialog;

        UpdateUserTask(String token, String idUser) {
            user = new User();
            user.id = idUser;

            this.uToken = token;

            pDialog = new ProgressDialog(getContext());
            serverComm = new ServerComm();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("MyPet", "updateUser start");

            pDialog.setTitle("Update utente");
            pDialog.setMessage("Richiesta al server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... p) {
            try {
                user.username = p[0];
                user.name = p[1];
                user.surname = p[2];
                user.gender = p[3];
                user.birthdate = p[4];
//                String profilepic = p[5];

                String postArgs = "updateUser=" + user.id +
                        "&token=" + uToken +
                        "&username=" + user.username +
                        "&name=" + user.name +
                        "&surname=" + user.surname +
                        "&gender=" + user.gender +
                        "&birthdate=" + user.birthdate;

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
                    //Se va a buon fine, update nel DB locale
                    long numRows = HomeActivity.dbManager.updateUser(user);

                    //Aggiorna token nelle SharedPref
                    shPref.edit().putString("Token", jObj.getString("token")).apply();

                    //gira al fragment di profilo animale
//                    getFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.main_fragment, AnimalProfileFragment.newInstance(anim.id))
//                            .addToBackStack("")
//                            .commit();

                    //Torna al fragment precedente
                    getFragmentManager().popBackStack();

                } else {

                    //TODO Altrimenti indicare l'errore all'utente
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

//    private void insertAnimal(){
//        //Recuperare dati, fare controlli se necessario
////        ArrayList<String> par = new ArrayList<String>();
////
//        String nameTxt = aNameEditTxt.getText().toString();
//        String speciesTxt = aSpeciesEditTxt.getText().toString();
//        String genderTxt = aGenderEditTxt.getText().toString();
//        String birthdateTxt = aBirthdateEditTxt.getText().toString();   //TODO gestire data
//        String profilepicTxt = "profilepic";
//
//        //Inviare richiesta al server per l'update
//        InsertAnimalTask insertAnim = new InsertAnimalTask(shPref.getString("Token", ""), "0", idUser);
//        insertAnim.execute(nameTxt, speciesTxt, genderTxt, birthdateTxt, profilepicTxt);
//    }


}