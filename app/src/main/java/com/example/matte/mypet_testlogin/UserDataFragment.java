package com.example.matte.mypet_testlogin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link UserDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDataFragment extends Fragment {
    private static final String ARG_PARAM1 = "idUser";
    private static final String ARG_PARAM2 = "isEdit";

    private final int PICK_IMAGE_REQUEST = 1;
    private Uri chosenImgUri;
    private String oldImgPath;

    private String idUser;
    private Boolean isEdit;

    public String newDate;
    public Date newBirthDate;

    private SharedPreferences shPref;

    private EditText uUsernameEditTxt;
    private EditText uNameEditTxt;
    private EditText uSurnameEditTxt;
//    private EditText uGenderEditTxt;
    private TextView uBirthdateTextView;
    private Button sendData;
    private Button uploadImg;
    private ImageButton changeUserBirthDate;
    private ImageView imgUserData;
    private EditText uPasswordEditTxt;
    private TextView uPasswordLbl;
    private EditText uPasswordConfirmEditTxt;
    private TextView uPasswordConfirmLbl;
    private RadioButton uGenderMaleRadioBtn;
    private RadioButton uGenderFemaleRadioBtn;

    private DrawerLayout drawer;

    private Bitmap bitmap;

    private ProgressDialog pDialog;

    public UserDataFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idUser Parameter 1.
     * @param isEdit true if user is editing his profile.
 *                   false if a new user is being created.
     * @return A new instance of fragment UserDataFragment.
     */
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

        //Riferimenti UI
        uUsernameEditTxt = (EditText) view.findViewById(R.id.userUsernameEditText);
        uNameEditTxt = (EditText) view.findViewById(R.id.userNameEditText);
        uSurnameEditTxt = (EditText) view.findViewById(R.id.userSurnameEditText);
//        uGenderEditTxt = (EditText) view.findViewById(R.id.userGenderEditText);
        uBirthdateTextView = (TextView) view.findViewById(R.id.userBirthDateTextView);
        imgUserData = (ImageView) view.findViewById(R.id.imageViewUserData);
        uGenderMaleRadioBtn = (RadioButton) view.findViewById(R.id.userGenderRadioBtnMale);
        uGenderFemaleRadioBtn = (RadioButton) view.findViewById(R.id.userGenderRadioBtnFemale);

        //PW
        uPasswordEditTxt = (EditText) view.findViewById(R.id.userPasswordEditText);
        uPasswordLbl = (TextView) view.findViewById(R.id.textViewPassword);
        uPasswordConfirmEditTxt = (EditText) view.findViewById(R.id.userPasswordConfirmEditText);
        uPasswordConfirmLbl = (TextView) view.findViewById(R.id.textViewPasswordConfirm);

        //Due button
        sendData = (Button) view.findViewById(R.id.buttonSendUserData);
        uploadImg = (Button) view.findViewById(R.id.buttonUploadUserImg);

        if (isEdit) {     //Se si vuole modificare i dati di un user
            User u = HomeActivity.dbManager.getUser(idUser);    //Raccolta dati dell'utente

            uUsernameEditTxt.setText(u.username);
            uNameEditTxt.setText(u.name);
            uSurnameEditTxt.setText(u.surname);
//            uGenderEditTxt.setText(u.gender);
            if(u.gender.equals("male")){
                uGenderMaleRadioBtn.setChecked(true);
            } else {
                uGenderFemaleRadioBtn.setChecked(true);
            }

            //Gestione data
            newBirthDate = u.birthdate;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM y");
            uBirthdateTextView.setText(dateFormat.format(u.birthdate));
            dateFormat = new SimpleDateFormat("y-MM-dd");
            newDate = dateFormat.format(u.birthdate);

            //Si nascondono i campi delle pw
            uPasswordEditTxt.setVisibility(View.GONE);
            uPasswordLbl.setVisibility(View.GONE);
            uPasswordConfirmEditTxt.setVisibility(View.GONE);
            uPasswordConfirmLbl.setVisibility(View.GONE);

            //Memorizzo percorso ultima img usata
            oldImgPath = u.profilepic;

//            //All'avvio, si carica l'img che si ha nel profilo
//            if(oldImgPath != null && !oldImgPath.isEmpty()) {
//                Picasso.with(view.getContext())
//                        .load(oldImgPath)
//                        .placeholder(R.drawable.img)
//                        .transform(new CropCircleTransformation())
//                        .into(imgUserData);
//            } else {
//                Picasso.with(view.getContext())
//                        .load(R.drawable.defaultuser)
//                        .placeholder(R.drawable.img)
//                        .transform(new CropCircleTransformation())
//                        .into(imgUserData);
//            }

            //Richiama la sequenza di azioni per aggiornare l'user
            sendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chosenImgUri != null) {
                        uploadImage();
                    } else {
                        //Se non si è scelta una nuova img, passare subito all'edit utente
                        updateUser(null);
                    }
                }
            });

            getActivity().setTitle("Modifica utente");
        } else {
            //Cancella token
            shPref.edit().remove("Token")
                        .remove("IdUser")
                        .remove("Username")
                        .remove("Name")
                        .remove("Surname")
                        .apply();

            //Tenere chiuso drawer
            drawer = ((HomeActivity)view.getContext()).drawer;
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            //Richiama la sequenza di azioni per aggiungere l'user
            sendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if (chosenImgUri != null){
                   uploadImage();
                } else {
                    //Se non si è scelta una nuova img, passare subito all'edit utente
                   insertUser(null);
                }
                }
            });

            getActivity().setTitle("Nuovo utente");
        }

        //Button di upload img. Chiede all'user da dove scegliere l'img
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);  //Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Seleziona immagine profilo"), PICK_IMAGE_REQUEST);
            }
        });

        changeUserBirthDate = (ImageButton) view .findViewById(R.id.changeUserBirthDateButton);
        changeUserBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
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

    @Override
    public void onResume() {
        super.onResume();
        String loadingImg = "";

        if(!isEdit) {   //Se si sta creando un nuovo utente, Back riporta al login
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        return true;
                    }
                    return false;
                }
            });
        }

        //ripristina uri nell'imageView
        if(chosenImgUri != null){
            loadingImg = chosenImgUri.toString();
        } else {
            loadingImg = oldImgPath;
        }

        if(oldImgPath != null && !oldImgPath.isEmpty()) {
            Picasso.with(getActivity().getBaseContext())
                    .load(loadingImg)
                    .placeholder(R.drawable.defaultuser)
                    .resize(512, 512)
                    .transform(new CropCircleTransformation())
                    .into(imgUserData);
        } else {
            Picasso.with(getActivity().getBaseContext())
                    .load(R.drawable.defaultuser)
                    .placeholder(R.drawable.defaultuser)
                    .resize(512, 512)
                    .transform(new CropCircleTransformation())
                    .into(imgUserData);
        }
    }

    /**
     * Metodo richiamato al termine della scelta dell'immagine.<br/>
     * Memorizza l'Uri dell'immagine e la mostra nell'ImageView
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            chosenImgUri = data.getData();

            Picasso.with(getActivity().getBaseContext())
                    .load(chosenImgUri)
                    .placeholder(R.drawable.defaultuser)
                    .transform(new CropCircleTransformation())
                    .resize(512, 512)
                    .into((ImageView) getActivity().findViewById(R.id.imageViewUserData));
        }
    }

    private void uploadImage(){
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), chosenImgUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Showing the progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setTitle("Connessione ai Server");
        pDialog.setMessage("Upload immagine...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://webdev.dibris.unige.it/~S3951060/mobile_login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String imgPath = "";
                        boolean success = false;
                        try {
                            JSONObject jObj = new JSONObject(s);
                            imgPath = jObj.getString("imgpath");
                            success = jObj.getBoolean("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(success) {
                            if(isEdit)
                                updateUser(imgPath);
                            else
                                insertUser(imgPath);
                        }

                        //TODO controllare per insuccesso

                        //Disimissing the progress dialog
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("MyPet", "Error volley" + volleyError.getMessage());

                        Toast.makeText(getActivity(), "Errore di rete. Riprovare.", Toast.LENGTH_SHORT).show();

                        //Dismissing the progress dialog
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("uploadImg", image);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void insertUser(String serverPicPath) {
        //Controllo mancato inserimento
        if(uNameEditTxt.getText().toString().isEmpty()){
            uNameEditTxt.setError("Inserire nome");
            uNameEditTxt.requestFocus();
            return;
        }
        String nameTxt = uNameEditTxt.getText().toString();

        if(uSurnameEditTxt.getText().toString().isEmpty()){
            uSurnameEditTxt.setError("Inserire cognome");
            uSurnameEditTxt.requestFocus();
            return;
        }
        String surnameTxt = uSurnameEditTxt.getText().toString();

        if(uUsernameEditTxt.getText().toString().isEmpty()){
            uUsernameEditTxt.setError("Inserire nickname");
            uUsernameEditTxt.requestFocus();
            return;
        }
        String usernameTxt = uUsernameEditTxt.getText().toString();

        if(uPasswordEditTxt.getText().toString().isEmpty() || uPasswordEditTxt.getText().toString().length()<8){
            uPasswordEditTxt.setError("Password non valida");
            uPasswordEditTxt.requestFocus();
            return;
        }
        String passwordTxt = uPasswordEditTxt.getText().toString();

        if(uPasswordConfirmEditTxt.getText().toString().isEmpty() || uPasswordConfirmEditTxt.getText().toString().length()<8){
            uPasswordConfirmEditTxt.setError("Password non valida");
            uPasswordConfirmEditTxt.requestFocus();
            return;
        }
        String passwordConfirmTxt = uPasswordConfirmEditTxt.getText().toString();

        if(!passwordTxt.equals(passwordConfirmTxt)) {
            uPasswordConfirmEditTxt.setError("Le password non corrispondono");
            uPasswordConfirmEditTxt.requestFocus();
            return;
        }

        if(newDate == null || newDate.isEmpty()){
            uBirthdateTextView.requestFocus();
            uBirthdateTextView.setError("Data di nascita mancante");
            return;
        } else {
            uBirthdateTextView.setError(null);   //Ripulisce errori
        }
        String birthdateTxt = newDate;

        //Gender
        String genderTxt;
        if(uGenderFemaleRadioBtn.isChecked()){
            genderTxt = "female";
        } else {
            genderTxt = "male";
        }

        String clientImgPath = "";
        if(chosenImgUri != null) {
            clientImgPath = chosenImgUri.toString();
        } else {
            //TODO set img di default
        }

        //Inviare richiesta al server per l'update
        InsertUserTask insertUser = new InsertUserTask();
        insertUser.execute(usernameTxt, nameTxt, surnameTxt, genderTxt,
                birthdateTxt, passwordTxt, clientImgPath, serverPicPath);
    }


    /**
     * AsyncTask che aggiorna l'utente inviato
     */
    public class InsertUserTask extends AsyncTask<String, Integer, JSONObject> {
        private final User user;

        private ServerComm serverComm;

        private ProgressDialog pDialog;

        InsertUserTask() {
            user = new User();

            pDialog = new ProgressDialog(getActivity());
            serverComm = new ServerComm();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("MyPet", "updateUser start");

            pDialog.setTitle("Insert utente");
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
                String birthdate = p[4];
                String password = p[5];
                user.profilepic = p[6];
                String serverPic = p[7];

                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
                try {
                    user.birthdate = format.parse(birthdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String postArgs = "insertUser=" +
                        "&username=" + user.username +
                        "&name=" + user.name +
                        "&surname=" + user.surname +
                        "&gender=" + user.gender +
                        "&password=" + password +
                        "&birthdate=" + birthdate +
                        "&profilepic=" + serverPic;

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
                    //recupera id
                    user.id = jObj.getString("iduser");

                    //Se va a buon fine, update nel DB locale
                    //TODO controlla buon fine
                    long numRows = HomeActivity.dbManager.insertUser(user);

                    //Aggiorna token nelle SharedPref
                    shPref.edit().putString("Token", jObj.getString("token"))
                            .putString("IdUser", idUser)
                            .putString("Username", user.username)
                            .putString("Name", user.name)
                            .putString("Surname", user.surname)
                            .apply();

                    //sblocca drawer
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);

                    //Vai al profilo
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment, ProfileFragment.newInstance(user.id))
                            .addToBackStack("")
                            .commit();

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

    private void updateUser(String serverPicPath) {
        //Controllo mancato aggirnamento
        if(uNameEditTxt.getText().toString().isEmpty()){
            uNameEditTxt.setError("Inserire nome");
            uNameEditTxt.requestFocus();
            return;
        }
        String nameTxt = uNameEditTxt.getText().toString();

        if(uSurnameEditTxt.getText().toString().isEmpty()){
            uSurnameEditTxt.setError("Inserire cognome");
            uSurnameEditTxt.requestFocus();
            return;
        }
        String surnameTxt = uSurnameEditTxt.getText().toString();

        if(uUsernameEditTxt.getText().toString().isEmpty()){
            uUsernameEditTxt.setError("Inserire nickname");
            uUsernameEditTxt.requestFocus();
            return;
        }
        String usernameTxt = uUsernameEditTxt.getText().toString();

        if(newDate == null || newDate.isEmpty()){
            uBirthdateTextView.requestFocus();
            uBirthdateTextView.setError("Data di nascita mancante");
            return;
        } else {
            uBirthdateTextView.setError(null);   //Ripulisce errori
        }
        String birthdateTxt = newDate;

        //Gender
        String genderTxt;
        if(uGenderFemaleRadioBtn.isChecked()){
            genderTxt = "female";
        } else {
            genderTxt = "male";
        }

        String clientImgPath = "";
        if(chosenImgUri != null)
             clientImgPath = chosenImgUri.toString();
        else
            clientImgPath = oldImgPath;

        //Inviare richiesta al server per l'update
        UpdateUserTask updateUser = new UpdateUserTask(shPref.getString("Token", ""), idUser);
        updateUser.execute(usernameTxt, nameTxt, surnameTxt, genderTxt,
                birthdateTxt, clientImgPath, serverPicPath);
    }


    /**
     * AsyncTask che aggiorna l'utente inviato
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

            pDialog = new ProgressDialog(getActivity());
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
                String birthdate = p[4];
                user.profilepic = p[5];
                String serverPic = p[6];

                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
                try {
                    user.birthdate = format.parse(birthdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String postArgs = "updateUser=" + user.id +
                        "&token=" + uToken +
                        "&username=" + user.username +
                        "&name=" + user.name +
                        "&surname=" + user.surname +
                        "&gender=" + user.gender +
                        "&birthdate=" + birthdate +
                        "&profilepic=" + serverPic;

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

                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    //Torna al fragment precedente
                    getFragmentManager().popBackStack();

                } else {

                    //TODO Altrimenti indicare l'errore all'utente
                }
            } catch(Exception e) {
                e.fillInStackTrace();
            }
        }

        @Override
        protected void onCancelled() {

            //TODO Indicare l'errore all'utente

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    public int year;
    public int month;
    public int day;

    public DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int Year, int monthOfYear, int dayOfMonth) {
            newDate = new StringBuilder()
                    .append(Year).append("-")
                    .append(monthOfYear+1).append("-")
                    .append(dayOfMonth).toString();

            SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
            try {
                newBirthDate = format.parse(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            format = new SimpleDateFormat("dd MMMM y");
            uBirthdateTextView.setText(format.format(newBirthDate));
        }
    };

    public void showDatePickerDialog(View v) {
        //Imposta il datepicker alla data precedente, o a quella corrente se non esiste
        Calendar c = Calendar.getInstance();
        if(newBirthDate != null){
            c.setTime(newBirthDate);
        } else {
//            c = Calendar.getInstance();
        }
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        Dialog newD = new DatePickerDialog(getActivity(), datePickerListener, year, month, day);
        newD.show();
    }

}