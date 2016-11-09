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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

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
    private EditText pPlaceEditTxt;
    private Button sendData;
    private Button uploadImg;
    private ImageView imgPostData;

    private ArrayList<User> friends;
    private ArrayList<User> taggedFriends;
    private ArrayList<Animal> taggedAnimals;

    private ProgressDialog pDialog;

    private SharedPreferences shPref;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_data, container, false);

        pTextEditTxt = (EditText) view.findViewById(R.id.postTextEditText);
        pPlaceEditTxt = (EditText) view.findViewById(R.id.postPlaceEditText);

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

        Picasso.with(view.getContext())
                .load(R.drawable.img)
                .resize(750, 750)
                .centerInside()
                .into((ImageView) view.findViewById(R.id.imageViewPostData));

        //Test multispinner
        MultiCustomSpinner multiSpinner = (MultiCustomSpinner) view.findViewById(R.id.multispinner);

        ArrayList<String> items = new ArrayList<>();
        for(User u : friends){
            items.add(u.username);
        }

        multiSpinner.setItems(items, "Default", new MultiCustomSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                ArrayList<User> tagged = new ArrayList<>();
                for(int i=0; i<selected.length; i++){
                    if(selected[i])
                        tagged.add(friends.get(i));     //se è stato selezionato, inserisce l'i-esimo amico in tagged
                }
                taggedFriends = tagged;
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
                    .placeholder(R.drawable.img)
                    .resize(750, 750)
                    .centerInside()
                    .into((ImageView) getActivity().findViewById(R.id.imageViewPostData));
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
                            insertPost(imgPath);
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
        String pPlaceTxt = pPlaceEditTxt.getText().toString();
        String pDateTxt = null;    //gestire data (fatto su server)

        String clientImgPath = "";
        if(chosenImgUri != null) {
            clientImgPath = chosenImgUri.toString();
        } else {
            //TODO set img di default
        }

        //Inviare richiesta al server per l'update
        InsertPostTask insertAnim = new InsertPostTask(shPref.getString("Token", ""), idUser);
        insertAnim.execute(pTextTxt, pPlaceTxt, pDateTxt, clientImgPath, serverPicPath);
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
                post.place = p[1];
                post.picture = p[3];
                String serverPic = p[4];

                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd H:m:s");
                try {
                    post.date = format.parse(p[2]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String postArgs = "insertPost=" +
                        "&iduser=" + idUser +
                        "&token=" + uToken +
                        "&text=" + post.text +
                        "&place=" + post.place +
                        "&date=" + post.date +
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

                    long idNewAnim = HomeActivity.dbManager.insertPost(post); //FIXME Non dovrebbe essere il nuovo id quello...

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

                } else {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
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

}
