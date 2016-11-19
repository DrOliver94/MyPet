package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Contiene le informazioni di un singolo post
 *
 */
public class Post implements Comparable<Post> {
    public String id;
    public String idauthor;
    public String nameauthor;
    public String picauthor;
    public String picture;
    public String text;
    public Date date;
    public Place place;
    public String placeId;
    public String placeAddress;
    public String placeName;
    public LatLng placeLatLon;

    public ArrayList<User> users;
    public ArrayList<Animal> animals;

    public Post() {}

    public Post(String idPost, String text, Date date, Place place, String pic) {
        this.id = idPost;
        this.text = text;
        this.date = date;
        this.place = place;
        this.picture = pic;
    }

    public Post(String idPost, JSONObject jObjPost, GoogleApiHelper gApiHelper) {
        users = new ArrayList<User>();
        animals = new ArrayList<Animal>();
//        String placeId;

        try {
            id = idPost;
            idauthor = jObjPost.getString("author");
            nameauthor = jObjPost.getString("nameauthor");
            picauthor = HomeActivity.IMG_BASEURL + jObjPost.getString("picauthor");
            if(!jObjPost.isNull("pic"))
                picture = HomeActivity.IMG_BASEURL + jObjPost.getString("pic");
            if(!jObjPost.isNull("text"))
                text = jObjPost.getString("text");
            if(!jObjPost.isNull("date")) {
                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd H:m:s");
                date = format.parse(jObjPost.getString("date"));
            }
            if(!jObjPost.isNull("place")) {
                placeId = jObjPost.getString("place");
                setPlace(gApiHelper, placeId);
            }
            if(!jObjPost.isNull("users")) {
                JSONObject jUsers = jObjPost.getJSONObject("users");
                JSONArray idUsers = jUsers.names();                 //recupera elenco ID degli utenti
                for (int i = 0; i < idUsers.length(); i++) {        //per ogni utente nell'obj
                    String idUser = (String) idUsers.get(i);        //recupera ID
                    JSONObject jUser = jUsers.getJSONObject(idUser);//recupera utente
                    User u = new User(idUser, jUser);               //Crea obj
                    users.add(u);                                   //Inserisce nell'arrayList
                }
            }

            if(!jObjPost.isNull("pets")) {
                JSONObject jPets = jObjPost.getJSONObject("pets");
                JSONArray idPets = jPets.names();                   //recupera elenco ID degli animali
                for (int i = 0; i < idPets.length(); i++) {         //per ogni animale nell'obj
                    String idPet = (String) idPets.get(i);          //recupera ID
                    JSONObject jPet = jPets.getJSONObject(idPet);   //recupera animale
                    Animal p = new Animal(idPet, jPet);             //Crea obj
                    animals.add(p);                                 //Inserisce nel l'arrayList
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPlace(GoogleApiHelper gApiHelper, String placeIdSet){
        placeId = placeIdSet;
        if(placeIdSet != null && !placeIdSet.isEmpty()) {
            Places.GeoDataApi
                    .getPlaceById(gApiHelper.getGoogleApiClient(), placeIdSet)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(@NonNull PlaceBuffer places) {
                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                place = (Place) places.get(0);
                                placeAddress = place.getAddress().toString();
                                placeName = place.getName().toString();
                                placeLatLon = place.getLatLng();
                            } else {
                            }
                            places.release();
                        }
                    });
        }
    }

    /**
     * Confronta due post
     *
     * @param other altro post con cui fare il confronto
     * @return 0 se other è null o hanno stessa data <br/>
     *          1 se il post è più recente di other <br/>
     *          -1 se il post è più vecchio di other <br/>
     */
    @Override
    public int compareTo(Post other) {
        if(other == null)
            return 0;
        if(this.equals(other))
            return 0;
        return -(this.date.compareTo(other.date));
    }

}
