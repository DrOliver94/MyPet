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
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AnimalDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AnimalDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnimalDataFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "idAnimal";
    private static final String ARG_PARAM2 = "isEdit";

    // TODO: Rename and change types of parameters
    private String idAnim;
    private Boolean isEdit;

    private String idUser;

    private EditText aNameEditTxt;
    private EditText aSpeciesEditTxt;
    private EditText aBirthdateEditTxt;
    private EditText aGenderEditTxt;

    private Button sendData;

    private SharedPreferences shPref;

    private OnFragmentInteractionListener mListener;

    public AnimalDataFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method per il fragment per editare/inserire i dati di un animale
     *
     * @param idAnimal id dell'animale
     * @param isEdit true se si sta modificando un animale esistente <br/>
     *               false se si vuole creare un nuovo animale
     * @return A new instance of fragment AnimalDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnimalDataFragment newInstance(String idAnimal, Boolean isEdit) {
        AnimalDataFragment fragment = new AnimalDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, idAnimal);
        args.putBoolean(ARG_PARAM1, isEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idAnim = getArguments().getString(ARG_PARAM1);
            isEdit = getArguments().getBoolean(ARG_PARAM2);
        }

        shPref = getActivity().getSharedPreferences("MyPetPrefs", Context.MODE_PRIVATE);

        idUser = shPref.getString("IdUser", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_animal_data, container, false);

        aNameEditTxt = (EditText) view.findViewById(R.id.animalNameEditText);
        aSpeciesEditTxt = (EditText) view.findViewById(R.id.animalSpeciesEditText);
        aBirthdateEditTxt = (EditText) view.findViewById(R.id.animalBirthDateEditText);
        aGenderEditTxt = (EditText) view.findViewById(R.id.animalGenderEditText);

        if(isEdit){ //Se si modifica un animal => caricare negli edittext i dati dell'animal
            if (mListener != null) {
                mListener.onFragmentInteraction("Modifica animale");
            }
        } else {    //Si crea un nuovo animale
            sendData = (Button) view.findViewById(R.id.buttonSendAnimData);
            sendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    insertAnimal();
                }
            });
            if (mListener != null) {
                mListener.onFragmentInteraction("Nuovo animale");
            }
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String name);
    }


    private void insertAnimal(){
        //Recuperare dati, fare controlli se necessario
        ArrayList<String> par = new ArrayList<String>();

        par.add(aNameEditTxt.getText().toString());
        par.add(aSpeciesEditTxt.getText().toString());
        par.add(aGenderEditTxt.getText().toString());
        par.add(aBirthdateEditTxt.getText().toString());
        par.add("profilepic");

        //Inviare richiesta al server per l'update
        InsertAnimalTask insertAnim = new InsertAnimalTask(shPref.getString("Token", ""), "0", idUser);
        insertAnim.execute(aNameEditTxt.getText().toString(),
                            aSpeciesEditTxt.getText().toString(),
                            aGenderEditTxt.getText().toString(),
                            aBirthdateEditTxt.getText().toString(),
                            "profilepic");
    }

    private void updateAnimal(){
        //Recuperare dati, fare controlli se necessario
        ArrayList<String> par = new ArrayList<String>();

        par.add(aNameEditTxt.getText().toString());
        par.add(aSpeciesEditTxt.getText().toString());
        par.add(aGenderEditTxt.getText().toString());
        par.add(aBirthdateEditTxt.getText().toString());
        par.add("profilepic");

        //Inviare richiesta al server per l'update
        UpdateAnimalTask updateAnim = new UpdateAnimalTask(shPref.getString("Token", ""), idAnim, idUser);
        updateAnim.execute((String[])par.toArray());
    }

    /**
     * Inserisce l'animale inviato
     */
    public class InsertAnimalTask extends AsyncTask<String, Integer, JSONObject> {
        private final String uToken;
        private final String idUser;
        private final Animal anim;

        private ServerComm serverComm;

        private ProgressDialog pDialog;

        InsertAnimalTask(String token, String idAnim, String idUser) {
            anim = new Animal();
            anim.id = idAnim;

            this.uToken = token;
            this.idUser = idUser;

            pDialog = new ProgressDialog(getContext());
            serverComm = new ServerComm();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("MyPet", "checkLogin start");

            pDialog.setTitle("Update animale");
            pDialog.setMessage("Richiesta al server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... p) {
            try {
                anim.name = p[0];
                anim.species = p[1];
                anim.gender = p[2];
                anim.birthdate = p[3];
//                String profilepic = p[4];

                String postArgs = "insertAnimal=" + anim.id +
                        "&iduser=" + idUser +
                        "&token=" + uToken +
                        "&name=" + anim.name +
                        "&species=" + anim.species +
                        "&gender=" + anim.gender +
                        "&birthdate=" + anim.birthdate;

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
                    //Se va a buon fine, recuperra l'ID e fa update nel DB locale
                    anim.id = jObj.getString("idpet");

                    long idNewAnim = HomeActivity.dbManager.insertAnimal(anim, idUser);

                    //Aggiorna token nelle SharedPref
                    shPref.edit().putString("Token", jObj.getString("token")).apply();

                    //gira al fragment di profilo animale
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment, AnimalProfileFragment.newInstance(Long.toString(idNewAnim)))
                            .addToBackStack(null)
                            .commit();
                } else {

                    //Altrimenti indicare l'errore all'utente
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

    /**
     * Aggiorna l'animale inviato
     */
    public class UpdateAnimalTask extends AsyncTask<String, Integer, JSONObject> {
        private final String uToken;
        private final String idUser;
        private final Animal anim;

        private ServerComm serverComm;

        private ProgressDialog pDialog;

        UpdateAnimalTask(String token, String idAnim, String idUser) {
            anim = new Animal();
            anim.id = idAnim;

            this.uToken = token;
            this.idUser = idUser;

            pDialog = new ProgressDialog(getContext());
            serverComm = new ServerComm();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("MyPet", "checkLogin start");

            pDialog.setTitle("Update animale");
            pDialog.setMessage("Richiesta al server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... p) {
            try {
                anim.name = p[0];
                anim.species = p[1];
                anim.gender = p[2];
                anim.birthdate = p[3];
//                String profilepic = p[4];

                String postArgs = "updateAnimal=" + anim.id +
                        "token=" + uToken +
                        "&name=" + anim.name +
                        "&species=" + anim.species +
                        "&gender=" + anim.gender;

//                Log.d("MyPet", postArgs);

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
                    long idNewAnim = HomeActivity.dbManager.updateAnimal(anim);
                    //gira al fragment di profilo animale
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment, AnimalProfileFragment.newInstance(Long.toString(idNewAnim)))
                            .addToBackStack(null)
                            .commit();
                } else {

                    //Altrimenti indicare l'errore all'utente
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
