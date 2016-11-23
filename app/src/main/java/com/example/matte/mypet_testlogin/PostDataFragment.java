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
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostDataFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final int PICK_IMAGE_REQUEST = 1;
    private Uri chosenImgUri;
    private Bitmap bitmap;

    private String idUser;

    private EditText pTextEditTxt;
    private TextView pPlaceTextView;
    private Button sendData;
    private Button uploadImg;
    private ImageView imgPostData;

    private ArrayList<User> friends;
    private ArrayList<User> taggedFriends;
    private ArrayList<Animal> animals;
    private ArrayList<Animal> taggedAnimals;

    private ProgressDialog pDialog;

    private SharedPreferences shPref;

    private ImageButton chooseLocation;
    private int PLACE_PICKER_REQUEST = 81293;
    public Place chosenPlace;
    private LatLng chosenLocation;

    public PostDataFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostDataFragment newInstance(String param1, String param2) {
        PostDataFragment fragment = new PostDataFragment();
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

        friends = HomeActivity.dbManager.getFriendsByUser(idUser);
        animals = HomeActivity.dbManager.getAnimalsByOwner(idUser);

        taggedAnimals = new ArrayList<>();
        taggedFriends = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_data, container, false);

        pTextEditTxt = (EditText) view.findViewById(R.id.postTextEditText);
        pPlaceTextView = (TextView) view.findViewById(R.id.postPlaceTextView);

        sendData = (Button) view.findViewById(R.id.buttonInsertPost);
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chosenImgUri != null)
                    uploadImage();
                else {
                    //Se non si è scelta una nuova img, passare subito all'edit utente
                    insertPost(null);
                }
            }
        });

        uploadImg = (Button) view.findViewById(R.id.buttonUploadPostImg);
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

        chooseLocation = (ImageButton) view.findViewById(R.id.buttonChooseLocation);
        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getActivity(), MapsActivity.class);
//                i.putExtra("com.example.matte.mypet_testlogin.isChoosingLoc", true);
//                startActivity(i);
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        Picasso.with(view.getContext())
                .load(R.drawable.defaultimg)
                .resize(750, 750)
                .centerInside()
                .into((ImageView) view.findViewById(R.id.imageViewPostData));

        //Test multispinner
        MultiCustomSpinner multiSpinnerUsers = (MultiCustomSpinner) view.findViewById(R.id.multispinUsers);

        ArrayList<String> friendsName = new ArrayList<>();
        for(User u : friends){
            friendsName.add(u.username);
        }

        multiSpinnerUsers.setItems(friendsName, "Selezione vuota", new MultiCustomSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
            ArrayList<User> tFriends = new ArrayList<>();
            for(int i=0; i<selected.length; i++){
                if(selected[i])
                    tFriends.add(friends.get(i));     //se è stato selezionato, inserisce l'i-esimo amico in tagged
            }
            taggedFriends = tFriends;
            }
        });

        MultiCustomSpinner multiSpinnerAnimals = (MultiCustomSpinner) view.findViewById(R.id.multispinAnimals);

        ArrayList<String> animalsName = new ArrayList<>();
        for(Animal a : animals){
            animalsName.add(a.name);
        }

        multiSpinnerAnimals.setItems(animalsName, "Selezione vuota", new MultiCustomSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                ArrayList<Animal> tAnimals = new ArrayList<>();
                for(int i=0; i<selected.length; i++){
                    if(selected[i])
                        tAnimals.add(animals.get(i));     //se è stato selezionato, inserisce l'i-esimo animale in tagged
                }
                taggedAnimals = tAnimals;
                return;
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
                    .placeholder(R.drawable.defaultimg)
                    .resize(750, 750)
                    .centerInside()
                    .into((ImageView) getActivity().findViewById(R.id.imageViewPostData));

        } else if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK){
            //Se si è scelto un luogo, memorizzarne la posizione
            chosenPlace = PlacePicker.getPlace(getActivity(), data);
            pPlaceTextView.setText(chosenPlace.getName());
            chosenLocation = chosenPlace.getLatLng();
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
                        String errorJson = "";
                        try {
                            JSONObject jObj = new JSONObject(s);
                            imgPath = jObj.getString("imgpath");
                            success = jObj.getBoolean("success");
                            if(!jObj.isNull("error")) {
                                errorJson = jObj.getString("error");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Disimissing the progress dialog
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        if(success) {
                            insertPost(imgPath);
                        } else {
                            Toast.makeText(getActivity(), errorJson, Toast.LENGTH_SHORT).show();
                        }

                        //TODO controllare per insuccesso
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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

    private void insertPost(String serverPicPath){
        //Recuperare dati, fare controlli se necessario
        //TODO fare controlli. usare TextView.setError()
        String pTextTxt = pTextEditTxt.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("y-MM-dd HH:mm:ss");
        String pDateTxt = dateFormat.format(Calendar.getInstance().getTime());
//        String pDateTxt = "2016-11-28 11:21:42";

        String clientImgPath = "";
        if(chosenImgUri != null) {
            clientImgPath = chosenImgUri.toString();
        } else {
            //TODO set img di default
        }

        //non passa place se non è stato scelto un luogo
        String chosenPlaceId = "";
        if(chosenPlace != null && chosenPlace.isDataValid()){
            chosenPlaceId = chosenPlace.getId();
        }

        //Inviare richiesta al server per l'update
        InsertPostTask insertPost = new InsertPostTask(shPref.getString("Token", ""), idUser);
        insertPost.execute(pTextTxt, pDateTxt, clientImgPath, serverPicPath, chosenPlaceId);
//        String.valueOf(chosenLocation.latitude), String.valueOf(chosenLocation.longitude)
    }

    /**
     * Inserisce l'animale inviato
     */
    public class InsertPostTask extends AsyncTask<String, Integer, JSONObject> {
        private final String uToken;
        private final String idUser;

        private Post post;

        private ServerComm serverComm;

        private ProgressDialog pDialog;

        InsertPostTask(String token, String idUser) {
            this.uToken = token;
            this.idUser = idUser;

            post = new Post();
            post.idauthor = idUser;

            pDialog = new ProgressDialog(getActivity());
            serverComm = new ServerComm();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("MyPet", "insertPost start");

            pDialog.setTitle("Insert post");
            pDialog.setMessage("Richiesta al server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... p) {
            try {
                post.text = p[0];
                String dateStr = p[1];
                post.picture = p[2];
                String serverPic = p[3];
//                post.place = new LatLng(Double.parseDouble(p[4]), Double.parseDouble(p[5]));
//                post.setPlace(p[4]);
                post.place = chosenPlace;
                if(chosenPlace != null && chosenPlace.isDataValid()) {
                    post.placeId = chosenPlace.getId();
                }
                post.animals = taggedAnimals;   //Sono null se vuoti
                post.users = taggedFriends;

                String jsonTagFriends = new String();
                String jsonTagAnimals = new String();
                try {
                    //Creazione JSON per elenco user taggati
                    if(taggedFriends!=null) {
                        StringWriter strWriter = new StringWriter();
                        JsonWriter writer = new JsonWriter(strWriter);
                        writer.beginArray();
                        for (User u : taggedFriends) {
                            writer.value(u.id);
                        }
                        writer.endArray();
                        writer.close();
                        jsonTagFriends = strWriter.toString();
                    }
                    //Creazione JSON per elenco animali taggati
                    if(taggedAnimals != null) {
                        StringWriter strWriter = new StringWriter();
                        JsonWriter writer = new JsonWriter(strWriter);
                        writer.beginArray();
                        for (Animal a : taggedAnimals) {
                            writer.value(a.id);
                        }
                        writer.endArray();
                        writer.close();
                        jsonTagAnimals = strWriter.toString();
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }


                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd HH:mm:ss");
                try {
                    post.date = format.parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String postArgs = "insertPost=" +
                        "&iduser=" + idUser +
                        "&token=" + uToken +
                        "&text=" + post.text +
                        "&place=" + post.placeId +
                        "&date=" + dateStr +
                        "&tagfriends=" + jsonTagFriends +
                        "&taganim=" + jsonTagAnimals +
                        "&picture=" + serverPic;

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
                    post.id = jObj.getString("idpost");

                    long idNewAnim = HomeActivity.dbManager.insertPost(post);

                    //Aggiorna token nelle SharedPref
                    shPref.edit().putString("Token", jObj.getString("token")).apply();

                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                    //gira al feed
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment, new FeedFragment())
                            .addToBackStack(null)
                            .commit();
                    return;

                } else {    //Problema sul server
                    if(!jObj.isNull("token") && jObj.getBoolean("token")) {
                        shPref.edit().putString("Token", jObj.getString("token")).apply();
                    }

                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                    if(!jObj.isNull("token") && jObj.getBoolean("token")) {
                        Toast.makeText(getActivity(), jObj.getString("error"), Toast.LENGTH_SHORT).show();
                    }

                    //TODO Altrimenti indicare l'errore all'utente

                    return;
                }
            } catch(Exception e) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                Toast.makeText(getActivity(), "Errore", Toast.LENGTH_SHORT).show();
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

}
