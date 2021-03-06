package com.example.matte.mypet_testlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private DownloadImgTask downImgTask = null;

    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;
    private View mLoginFormView;

    private String username;

    private ProgressDialog pDialog;

    public static GoogleApiClient gApiClient;

    SharedPreferences shPref;

    public GoogleApiHelper gApiHelper;

    public Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("MyPet");

//***********************************

        gApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("MyPet", connectionResult.getErrorMessage());
                        return;
                    }
                })
                .build();

        gApiHelper = new GoogleApiHelper(getApplicationContext());

//        gah.getGoogleApiClient();

//        Places.GeoDataApi
//                .getPlaceById(gah.getGoogleApiClient(), "ChIJrZrU3FJB0xIRCmKBGPGEaiM")
//                .setResultCallback(new ResultCallback<PlaceBuffer>() {
//                    @Override
//                    public void onResult(@NonNull PlaceBuffer places) {
//                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
//                            place = (Place) places.get(0);
//                            Log.d("MyPet", place.getId());
//                            Log.d("MyPet", place.getAddress().toString());
//                        } else {
//                        }
//                        places.release();
//                    }
//                });

//*******************************************

        shPref = this.getSharedPreferences("MyPetPrefs", MODE_PRIVATE);     //recupera sharedPref
        //pressione di Invio nel campo password
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {            //TODO ha senso?
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        //Bottone LOGIN
        Button mLoginButton = (Button) findViewById(R.id.login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //Bottone REGISTRATI
        Button mSignupButton = (Button) findViewById(R.id.signup_button);
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.main_fragment, UserDataFragment.newInstance("", false))
//                        .addToBackStack(null)
//                        .commit();

                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                i.putExtra("com.example.matte.mypet_testlogin.NewUser", true);
                startActivity(i);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Se il token è impostato, e siamo offline => fai entrare
        if(!isOnline() && !shPref.getString("Token", "").equals("")) {
            Log.d("MyPet", "entrata senza conn");
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }

        //Riporta l'ultimo username usato nel campo di login
        mUserView = (EditText) findViewById(R.id.username);
        mUserView.setText(shPref.getString("Username", ""));

        //Verifica se l'utente ha un token valido
        //In tal caso salta il login
        if(!shPref.getString("Token", "").isEmpty()) {
            checkLoginTask checkTask = new checkLoginTask(shPref.getString("Token", ""), shPref.getString("IdUser", ""));
            checkTask.execute();
        }
    }

    /**
     * Verifica i dati inseriti e tenta il login
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt
        username = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        } else if (!isEmailValid(username)) {
            mUserView.setError(getString(R.string.error_invalid_email));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute();
        }
    }

    /**
     * Se il login ha successo (tramite UserLoginTask) prosegue con l'autenticazione
     */
    private void successLogin(JSONObject jObj) {
        //Istanza dell'editor di SharedPreferences
        SharedPreferences.Editor editor = shPref.edit();

        //Si caricano nelle ShPref le info ricevute dal server
        String token = null;
        String idUser = null;
        try {
            idUser = jObj.getString("iduser");

            token = jObj.getString("token");
            editor.putString("IdUser", idUser)
                    .putString("Token", token)
                    .putString("Username", username)
                    .putString("Name", jObj.getString("name"))
                    .putString("Surname", jObj.getString("surname"))
                    .apply(); //inserisce in background (al contrario di commit)

        } catch(NullPointerException | JSONException e) {
            e.fillInStackTrace();
        }

        //Si ricopia sul DB offline ogni dato di quello online
        dbPairingTask mPair = new dbPairingTask(token, idUser);
        mPair.execute(idUser);
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Verifica la presenza di una connessione internet
     *
     * @return true/false se l'host è online o meno
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo!=null && netInfo.isConnected();
    }

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }


    /**
     * Interroga il server e autentica l'utente
     *
     */
    public class UserLoginTask extends AsyncTask<Void, Integer, JSONObject> {
        private final String mUser;
        private final String mPassword;

        private ServerComm serverComm;

        UserLoginTask(String user, String password) {
            mUser = user;
            mPassword = password;
            pDialog = new ProgressDialog(LoginActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            serverComm = new ServerComm();

            Log.d("MyPet", "UserLoginTask started...");

            pDialog.setTitle("Richiesta ai Server");
            pDialog.setMessage("Login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... p) {
            try {
                String param="userLogin=" + mUser + "&pw=" + mPassword;
                return serverComm.makePostRequest(param);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            boolean success = false;
            try {
                success = jObj.getBoolean("success");
            } catch(Exception e) {
                e.fillInStackTrace();
            }
            if(success){
                successLogin(jObj);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }

            mAuthTask = null;
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    /**
     * Verifica se l'utente è già riconosciuto dal server
     * In tal caso salta la schermata di login
     */
    public class checkLoginTask extends AsyncTask<Void, Integer, JSONObject> {
        private final String mUserId;
        private final String mToken;

        private ServerComm serverComm;

        private ProgressDialog pDialog;

        checkLoginTask(String token, String user) {
            mUserId = user;
            mToken = token;
            pDialog = new ProgressDialog(LoginActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            serverComm = new ServerComm();

            Log.d("MyPet", "checkLogin start");

            pDialog.setTitle("Richiesta ai Server");
            pDialog.setMessage("Verifica token...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... p) {
            try {
                String postArgs = "checkToken=" + mToken + "&iduser=" + mUserId;

                Log.d("MyPet", postArgs);

                return serverComm.makePostRequest(postArgs);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            try {
                if(jObj.getBoolean("logged")) {
                    //si passa alla home
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    i.putExtra("com.example.matte.mypet_testlogin.NewUser", false);
                    startActivity(i);
                }
            } catch(Exception e) {
                e.fillInStackTrace();
            }

            mAuthTask = null;
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    public class dbPairingTask extends AsyncTask<String, Integer, JSONObject> {
        private final String mToken;
        private final String mIdUser;

        ArrayList<String> imgsList = new ArrayList<>();     //Array per raccogliere tutti i link delle immagini

        private ProgressDialog pDialog;

        private ServerComm serverComm;
        private MyPetDB dbHand;
        private SharedPreferences.Editor editor;

        private String param;

        dbPairingTask(String token, String idUser) {
            mToken = token;
            mIdUser = idUser;
            pDialog = new ProgressDialog(LoginActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            serverComm = new ServerComm();
            dbHand = new MyPetDB(LoginActivity.this);
            editor = shPref.edit();

            param="firstSync=&iduser=" + mIdUser + "&token=" + mToken;

            Log.d("MyPet", "dbPairing is on");

            pDialog.setTitle("Richiesta ai Server");
            pDialog.setMessage("Sincronizzazione col Database...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            //params comes from the execute() call
            String idUser = params[0];

            JSONObject jObj = null;
            try {
                jObj = serverComm.makePostRequest(param);

                //##### Loading utente nel DB
                JSONObject jObjUser = jObj.getJSONObject("_user");
                User u = new User(jObj.getString("iduser"), jObjUser);
                dbHand.insertUser(u);
                imgsList.add(u.profilepic);

                //##### Loading animali nel DB
                if(!jObj.isNull("_pets")) {
                    JSONObject jPets = jObj.getJSONObject("_pets");
                    JSONArray idPets = jPets.names();                   //recupera elenco ID degli animali
                    for (int i = 0; i < idPets.length(); i++) {         //per ogni animale nell'obj
                        String idPet = (String) idPets.get(i);          //recupera ID
                        JSONObject jPet = jPets.getJSONObject(idPet);   //recupera animale
                        Animal p = new Animal(idPet, jPet);             //Crea obj
                        dbHand.insertAnimal(p, u.id);                         //Inserisce nel DB
                        imgsList.add(p.profilepic);
                    }
                }

                //##### Loading amici nel DB
                if(!jObj.isNull("_friends")) {
                    JSONObject jFriends = jObj.getJSONObject("_friends");
                    JSONArray idFriends = jFriends.names();                     //recupera elenco ID degli amici
                    for (int i = 0; i < idFriends.length(); i++) {              //per ogni utente nell'obj
                        String idFriend = (String) idFriends.get(i);            //recupera ID
                        JSONObject jFriend = jFriends.getJSONObject(idFriend);  //recupera utente
                        User f = new User(idFriend, jFriend);                   //Crea obj
                        dbHand.insertUser(f);                                   //Inserisce nel DB
                        String status = jFriend.getString("status");
                        dbHand.insertFriendship(jFriend.getString("idfriendship"), idUser, f.id, status);
                        imgsList.add(f.profilepic);
                    }
                }

                //##### Loading post nel DB
                //Dopo gli amici (o nel DB potrebbero venir caricati dati parziali...)
                if(!jObj.isNull("_posts")) {
                    JSONObject jPosts = jObj.getJSONObject("_posts");
                    //TODO qualquadra non cosa qui (forse non più)
                    JSONArray idPosts = jPosts.names();                  //recupera elenco ID dei post
                    for (int i = 0; i < idPosts.length(); i++) {        //per ogni post nell'obj
                        String idPost = (String) idPosts.get(i);        //recupera ID
                        JSONObject jPost = jPosts.getJSONObject(idPost);//recupera post
                        Post p = new Post(idPost, jPost, gApiHelper);               //Crea obj
                        dbHand.insertPost(p);                           //Inserisce nel DB
                        imgsList.add(p.picture);
                    }
                }

                //##### Loading reminder nel DB
                if(!jObj.isNull("_reminders")) {
                    JSONObject jReminders = jObj.getJSONObject("_reminders");
                    JSONArray idReminders = jReminders.names();                 //recupera elenco ID degli amici
                    for (int i = 0; i < idReminders.length(); i++) {            //per ogni utente nell'obj
                        JSONObject jReminder = jReminders.getJSONObject((String) idReminders.get(i));  //recupera utente
                        Reminder r = new Reminder(jReminder);                       //Crea obj
                        dbHand.insertReminder(r);                               //Inserisce nel DB
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //TODO controllare per errori

            //L'asyncTask per il download delle imgs
            //verrà eseguito al termine di questo AsyncTask

            return jObj;
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            try {
                //aggiornamento token
                editor.putString("Token", jObj.getString("token")).apply();
            } catch(Exception e) {
                e.fillInStackTrace();
            }

            mAuthTask = null;
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            //AsyncTask per il download delle imgs
            downImgTask = new DownloadImgTask(imgsList);
            downImgTask.execute();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    /**
     * Verifica se l'utente è già riconosciuto dal server
     * In tal caso salta la schermata di login
     */
    public class DownloadImgTask extends AsyncTask<Void, Integer, JSONObject> {
        private final ArrayList<String> imgList;

        private ProgressDialog pDialog;

        DownloadImgTask(ArrayList<String> imgL) {
            imgList = imgL;
            pDialog = new ProgressDialog(LoginActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("MyPet", "downloadImg start");

            pDialog.setTitle("Richiesta ai Server");
            pDialog.setMessage("Download immagini...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... p) {
            //Download di ogni img caricata in lista
            for(String imgUrl : imgList){
                try {
                    if(imgUrl != null) {
                        Picasso.with(getBaseContext()).load(imgUrl).get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            //si passa alla home
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            i.putExtra("com.example.matte.mypet_testlogin.NewUser", false);
            startActivity(i);
        }

        @Override
        protected void onCancelled() {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

}


//#################################################################################################
//###################################### Ringraziamenti ###########################################
//#################################################################################################
//#                                                                                               #
//#         Un enorme grazie ai miei genitori, amici, colleghi e professori, che hanno            #
//#         creduto in me e mi hanno dato forza, coraggio, determinazione nel proseguire          #
//#         i miei studi. Un insieme di insostituibili persone a cui vorrò sempre bene e          #
//#         che non dimenticherò mai.                                                             #
//#                                                                                               #
//#                                                                       Matteo Oliveri          #
//#                                                                     07 dicembre 2016          #
//#                                                                                               #
//#################################################################################################
//#################################################################################################