package com.example.matte.mypet_testlogin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.DatePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 *
 * Use the {@link AnimalDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnimalDataFragment extends Fragment {
    private static final String ARG_PARAM1 = "idAnimal";
    private static final String ARG_PARAM2 = "isEdit";

    private final int PICK_IMAGE_REQUEST = 1;
    private Uri chosenImgUri;
    private String oldImgPath;

    private String idAnim;
    private String idUser;

    private Boolean isEdit;

    private EditText aNameEditTxt;
    private EditText aSpeciesEditTxt;
    private TextView aBirthdateTextView;
    private EditText aGenderEditTxt;
    private Button sendData;
    private Button uploadImg;
    private Button changeAnimalBirthDate;
    private ImageView imgAnimalData;

    private Bitmap bitmap;

    private ProgressDialog pDialog;

    private SharedPreferences shPref;

    public AnimalDataFragment() {}

    /**
     * Factory method per il fragment per editare/inserire i dati di un animale
     *
     * @param idAnimal id dell'animale
     * @param isEdit true se si sta modificando un animale esistente <br/>
     *               false se si vuole creare un nuovo animale
     * @return A new instance of fragment AnimalDataFragment.
     */
    public static AnimalDataFragment newInstance(String idAnimal, Boolean isEdit) {
        AnimalDataFragment fragment = new AnimalDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, idAnimal);
        args.putBoolean(ARG_PARAM2, isEdit);
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

        //Riferimenti UI
        aNameEditTxt = (EditText) view.findViewById(R.id.animalNameEditText);
        aSpeciesEditTxt = (EditText) view.findViewById(R.id.animalSpeciesEditText);
        aBirthdateTextView = (TextView) view.findViewById(R.id.animalBirthDateTextView);
        aGenderEditTxt = (EditText) view.findViewById(R.id.animalGenderEditText);
        imgAnimalData = (ImageView) view.findViewById(R.id.imageViewAnimalData);

        //Due button
        sendData = (Button) view.findViewById(R.id.buttonSendAnimData);
        uploadImg = (Button) view .findViewById(R.id.buttonUploadAnimalImg);

        if(isEdit){ //Se si modifica un animale => caricare negli edittext i dati dell'animale
            Animal a = HomeActivity.dbManager.getAnimal(idAnim);

            aNameEditTxt.setText(a.name);
            aSpeciesEditTxt.setText(a.species);
            aGenderEditTxt.setText(a.gender);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM y");
            newDate = dateFormat.format(a.birthdate);
            aBirthdateTextView.setText(newDate);

            //Memorizzo percorso ultima img usata
            oldImgPath = a.profilepic;

            //All'avvio, si carica l'img che si ha nel profilo
            Picasso.with(view.getContext())
                    .load(oldImgPath)
                    .placeholder(R.drawable.img)
                    .transform(new CropCircleTransformation())
                    .resize(512, 512)
                    .centerCrop()
                    .into(imgAnimalData);

            sendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chosenImgUri != null)
                        uploadImage();
                    else {
                        //Se non si è scelta una nuova img, passare subito all'edit utente
                        updateAnimal(null);
                    }
                }
            });

            getActivity().setTitle("Modifica Animale");
        } else {    //Si crea un nuovo animale
            sendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chosenImgUri != null)
                        uploadImage();
                    else {
                        //Se non si è scelta una nuova img, passare subito all'edit utente
                        insertAnimal(null);
                    }
                }
            });

            Picasso.with(view.getContext())
                    .load(R.drawable.img)
                    .transform(new CropCircleTransformation())
                    .resize(512, 512)
                    .centerCrop()
                    .into(imgAnimalData);

            getActivity().setTitle("Nuovo Animale");
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

        changeAnimalBirthDate = (Button) view .findViewById(R.id.changeAnimalBirthDateButton);
        changeAnimalBirthDate.setOnClickListener(new View.OnClickListener() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            //Memorizzo l'Uri dell'img scelta
            chosenImgUri = data.getData();

            //La mostro nell'ImageView
            Picasso.with(getActivity().getBaseContext())
                    .load(chosenImgUri)
                    .placeholder(R.drawable.img)
                    .transform(new CropCircleTransformation())
                    .resize(512, 512)
                    .centerCrop()
                    .into((ImageView) getActivity().findViewById(R.id.imageViewAnimalData));
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
                                updateAnimal(imgPath);
                            else
                                insertAnimal(imgPath);
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
                        Log.d("MyPet", "Error volley ");
                        volleyError.printStackTrace();

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

    private void insertAnimal(String serverPicPath){
        //Recuperare dati, fare controlli se necessario

        //TODO fare controlli. usare TextView.setError()
        String nameTxt = aNameEditTxt.getText().toString();
        String speciesTxt = aSpeciesEditTxt.getText().toString();
        String genderTxt = aGenderEditTxt.getText().toString();
        String birthdateTxt = aBirthdateTextView.getText().toString();   //TODO gestire data

        String clientImgPath = "";
        if(chosenImgUri != null) {
            clientImgPath = chosenImgUri.toString();
        } else {
            //TODO set img di default
        }

        //Inviare richiesta al server per l'update
        InsertAnimalTask insertAnim = new InsertAnimalTask(shPref.getString("Token", ""), idUser);
        insertAnim.execute(nameTxt, speciesTxt, genderTxt, birthdateTxt, clientImgPath, serverPicPath);
    }

    private void updateAnimal(String serverPicPath){
        //Recuperare dati
        //TODO fai controlli
        String nameTxt = aNameEditTxt.getText().toString();
        String speciesTxt = aSpeciesEditTxt.getText().toString();
        String genderTxt = aGenderEditTxt.getText().toString();
        String birthdateTxt = newDate;

        String clientImgPath = "";
        if(chosenImgUri != null)
            clientImgPath = chosenImgUri.toString();    //img scelta
        else
            clientImgPath = oldImgPath; //img non modificata, si lascia quella già presente

        //Inviare richiesta al server per l'update
        UpdateAnimalTask updateAnim = new UpdateAnimalTask(shPref.getString("Token", ""), idAnim, idUser);
        updateAnim.execute(nameTxt, speciesTxt, genderTxt, birthdateTxt,
                clientImgPath, serverPicPath);
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

        InsertAnimalTask(String token, String idUser) {
            anim = new Animal();

            this.uToken = token;
            this.idUser = idUser;

            pDialog = new ProgressDialog(getActivity());
            serverComm = new ServerComm();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("MyPet", "insertAnimal start");

            pDialog.setTitle("Insert animale");
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
                anim.profilepic = p[4];
                String serverPic = p[5];

                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
                try {
                    anim.birthdate = format.parse(p[3]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String postArgs = "insertAnimal=" + anim.id +
                        "&iduser=" + idUser +
                        "&token=" + uToken +
                        "&name=" + anim.name +
                        "&species=" + anim.species +
                        "&gender=" + anim.gender +
                        "&birthdate=" + anim.birthdate +
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
                    //Se va a buon fine, recuperra l'ID e fa update nel DB locale
                    anim.id = jObj.getString("idpet");

                    long idNewAnim = HomeActivity.dbManager.insertAnimal(anim, idUser); //FIXME Non dovrebbe essere il nuovo id quello...

                    //Aggiorna token nelle SharedPref
                    shPref.edit().putString("Token", jObj.getString("token")).apply();

                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                    //gira al fragment di profilo animale
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment, AnimalProfileFragment.newInstance(anim.id))
                            .addToBackStack(null)
                            .commit();
                    return;

                } else {

                    //TODO Altrimenti indicare l'errore all'utente. Aggiorna token.
                }
            } catch(Exception e) {
                e.fillInStackTrace();
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

            pDialog = new ProgressDialog(getActivity());
            serverComm = new ServerComm();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("MyPet", "updateAnimal start");

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
                String birthdate = p[3];
                anim.profilepic = p[4];
                String serverPic = p[5];

                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
                try {
                    anim.birthdate = format.parse(birthdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String postArgs = "updateAnimal=" + anim.id +
                        "&iduser=" + idUser +
                        "&token=" + uToken +
                        "&name=" + anim.name +
                        "&species=" + anim.species +
                        "&gender=" + anim.gender +
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
                    long numRows = HomeActivity.dbManager.updateAnimal(anim);

                    //Aggiorna token nelle SharedPref
                    shPref.edit().putString("Token", jObj.getString("token")).apply();

                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    //Torna al fragment precedente
                    getFragmentManager().popBackStack();

                } else {

                    // TODO Altrimenti indicare l'errore all'utente
                }
            } catch(Exception e) {
                e.fillInStackTrace();
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

            SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
            try {
                newBirthDate = format.parse(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            format = new SimpleDateFormat("dd MMMM y");
            aBirthdateTextView.setText(format.format(newBirthDate));

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
