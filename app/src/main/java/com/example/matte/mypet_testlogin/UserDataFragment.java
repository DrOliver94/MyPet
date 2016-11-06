package com.example.matte.mypet_testlogin;

import android.app.Activity;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "idUser";
    private static final String ARG_PARAM2 = "isEdit";

    private final int PICK_IMAGE_REQUEST = 1;
    private Uri chosenImgUri;
    private String oldImgPath;

    private String idUser;
    private Boolean isEdit;

    private SharedPreferences shPref;

    private EditText uUsernameEditTxt;
    private EditText uNameEditTxt;
    private EditText uSurnameEditTxt;
    private EditText uGenderEditTxt;
    private EditText uBirthdateEditTxt;
    private Button sendData;
    private Button uploadImg;
    private ImageView imgUserData;
    private EditText uPasswordEditTxt;
    private TextView uPasswordLbl;
    private EditText uPasswordConfirmEditTxt;
    private TextView uPasswordCOnfirmLbl;

    private Bitmap bitmap;

    private ProgressDialog pDialog;

    public UserDataFragment() { }

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

        //Riferimenti UI
        uUsernameEditTxt = (EditText) view.findViewById(R.id.userUsernameEditText);
        uNameEditTxt = (EditText) view.findViewById(R.id.userNameEditText);
        uSurnameEditTxt = (EditText) view.findViewById(R.id.userSurnameEditText);
        uGenderEditTxt = (EditText) view.findViewById(R.id.userGenderEditText);
        uBirthdateEditTxt = (EditText) view.findViewById(R.id.userBirthDateEditText);
        imgUserData = (ImageView) view.findViewById(R.id.imageViewUserData);

        //PW
        uPasswordEditTxt = (EditText) view.findViewById(R.id.userPasswordEditText);
        uPasswordLbl = (TextView) view.findViewById(R.id.textViewPassword);
        uPasswordConfirmEditTxt = (EditText) view.findViewById(R.id.userPasswordConfirmEditText);
        uPasswordCOnfirmLbl = (TextView) view.findViewById(R.id.textViewPasswordConfirm);

        //Due button
        sendData = (Button) view.findViewById(R.id.buttonSendUserData);
        uploadImg = (Button) view.findViewById(R.id.buttonUploadUserImg);

        if (isEdit) {     //Se si vuole modificare i dati di un user
            User u = HomeActivity.dbManager.getUser(idUser);    //Raccolta dati dell'utente
            uUsernameEditTxt.setText(u.username);
            uNameEditTxt.setText(u.name);
            uSurnameEditTxt.setText(u.surname);
            uGenderEditTxt.setText(u.gender);
            uBirthdateEditTxt.setText(u.birthdate);

            //Si nascondono i campi delle pw
            uPasswordEditTxt.setVisibility(View.GONE);
            uPasswordLbl.setVisibility(View.GONE);
            uPasswordConfirmEditTxt.setVisibility(View.GONE);
            uPasswordCOnfirmLbl.setVisibility(View.GONE);

            oldImgPath = u.profilepic;

            //All'avvio, si carica l'img che si ha nel profilo
            Picasso.with(view.getContext())
                    .load(oldImgPath)
                    .placeholder(R.drawable.img)
                    .transform(new CropCircleTransformation())
                    .into(imgUserData);

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

            //TODO cancella token, blocca drawer

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

        //TODO ripristina uri nell'imageView
        if(chosenImgUri != null){
            loadingImg = chosenImgUri.toString();
        } else {
            loadingImg = oldImgPath;
        }

        Picasso.with(getActivity().getBaseContext())
                .load(loadingImg)
                .placeholder(R.drawable.img)
                .transform(new CropCircleTransformation())
                .into((ImageView) getActivity().findViewById(R.id.imageViewUserData));

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
                    .placeholder(R.drawable.img)
                    .transform(new CropCircleTransformation())
                    .into((ImageView) getActivity().findViewById(R.id.imageViewUserData));

// Tentativi per l'upload dell'img
//
//            String imagepath = getPath(chosenImgUri);
//            File f = new File(imagepath);
//******************************
//            File f = new File(chosenImgUri.getPath());
//            f.exists();
//
//            //tenta upload del file
//            uploadFile("http://webdev.dibris.unige.it/~S3951060/testimg.php", f);
//***********************************
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                // Log.d(TAG, String.valueOf(bitmap));
//
//                ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageViewUserData);
//                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//************************************

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

    private void updateUser(String serverPicPath) {
        //Recuperare dati
        //TODO fare controlli. usare TextView.setError()
        String usernameTxt = uUsernameEditTxt.getText().toString();
        String nameTxt = uNameEditTxt.getText().toString();
        String surnameTxt = uSurnameEditTxt.getText().toString();
        String genderTxt = uGenderEditTxt.getText().toString();
        String birthdateTxt = uBirthdateEditTxt.getText().toString();   //TODO gestire data

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
                user.profilepic = p[5];
                String serverPic = p[6];

                String postArgs = "updateUser=" + user.id +
                        "&token=" + uToken +
                        "&username=" + user.username +
                        "&name=" + user.name +
                        "&surname=" + user.surname +
                        "&gender=" + user.gender +
                        "&birthdate=" + user.birthdate +
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

//
//    public static Boolean uploadFile(String serverURL, File file) {
//        final OkHttpClient client = new OkHttpClient();
//
////        final MediaType MEDIA_TYPE = sourceImageFile.endsWith("png") ?
////                MediaType.parse("image/png") : MediaType.parse("image/jpeg");
//
//        try {
//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("img", file.getName(),
//                            RequestBody.create(MediaType.parse("image/jpeg"), file))
//                    .build();
//
//            Request request = new Request.Builder()
//                    .url(serverURL)
//                    .post(requestBody)
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    // Handle the error
//                    Log.d("MyPet", "failed");
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (!response.isSuccessful()) {
//                        // Handle the error
//                    }
//                    // Upload successful
//                }
//            });
//
//            return true;
//        } catch (Exception ex) {
//            // Handle the error
//        }
//        return false;
//    }
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

//    public String getPath(Uri uri) {
//        String result;
//        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            result = uri.getPath();
//        } else {
//            cursor.moveToFirst();
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            result = cursor.getString(idx);
//            cursor.close();
//        }
//        return result;
//    }


    private void insertUser(String serverPicPath) {
        //Recuperare dati
        //TODO fare controlli. usare TextView.setError()
        String usernameTxt = uUsernameEditTxt.getText().toString();
        String nameTxt = uNameEditTxt.getText().toString();
        String surnameTxt = uSurnameEditTxt.getText().toString();
        String genderTxt = uGenderEditTxt.getText().toString();
        String birthdateTxt = uBirthdateEditTxt.getText().toString();   //TODO gestire data
        String passwordTxt = uPasswordEditTxt.getText().toString();

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
        private final String uToken = "";
        private final User user;

        private ServerComm serverComm;

        private ProgressDialog pDialog;

        InsertUserTask() {
            user = new User();

            pDialog = new ProgressDialog(getContext());
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
                user.birthdate = p[4];
                String password = p[5];
                user.profilepic = p[6];
                String serverPic = p[7];

                String postArgs = "insertUser=" +
                        "&username=" + user.username +
                        "&name=" + user.name +
                        "&surname=" + user.surname +
                        "&gender=" + user.gender +
                        "&password=" + password +
                        "&birthdate=" + user.birthdate +
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
                    shPref.edit().putString("Token", jObj.getString("token")).apply();
                    //TODO ripulisci shPref, copia login

                    //Vai al profilo
                    //TODO sblocca drawer
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

}