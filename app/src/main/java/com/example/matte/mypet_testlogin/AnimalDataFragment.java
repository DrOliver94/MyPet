package com.example.matte.mypet_testlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_animal_data, container, false);

        if(isEdit){ //Se si modifica un animal => caricare negli edittext i dati dell'animal
            if (mListener != null) {
                mListener.onFragmentInteraction("Modifica animale");
            }
        } else {    //Si crea un nuovo animale

            if (mListener != null) {
                mListener.onFragmentInteraction("Nuovo animale");
            }
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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

        par.add("nome");
        par.add("specie");
        par.add("sesso");
        par.add("datanascita");
        par.add("profilepic");

        //Inviare richiesta al server per l'update
        InsertAnimalTask insertAnim = new InsertAnimalTask("token", idAnim, "idUser");
        insertAnim.execute((String[])par.toArray());
    }

    private void updateAnimal(){
        //Recuperare dati, fare controlli se necessario
        ArrayList<String> par = new ArrayList<String>();

        par.add("nome");
        par.add("specie");
        par.add("sesso");
        par.add("datanascita");
        par.add("profilepic");

        //Inviare richiesta al server per l'update
        UpdateAnimalTask updateAnim = new UpdateAnimalTask("token", idAnim, "idUser");
        updateAnim.execute((String[])par.toArray());
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
                if(jObj.isNull("error")) {
                    //Se va a buon fine, update nel DB locale
                    HomeActivity.dbManager.insertAnimal(anim, idUser);
                    //TODO girare al fragment di profilo animale
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

                String postArgs = "updateAnimal=" + anim.id +
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
                if(jObj.isNull("error")) {
                    //Se va a buon fine, update nel DB locale
                    HomeActivity.dbManager.insertAnimal(anim, idUser);
                    //TODO girare al fragment di profilo animale
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
