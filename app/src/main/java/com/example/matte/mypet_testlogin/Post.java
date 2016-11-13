package com.example.matte.mypet_testlogin;

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
public class Post {
    public String id;
    public String idauthor;
    public String nameauthor;
    public String picauthor;
    public String picture;
    public String text;
    public Date date;
    public LatLng place;
    public ArrayList<User> users;
    public ArrayList<Animal> animals;

    public Post() {}

    public Post(String idPost, String text, Date date, LatLng place, String pic) {
        this.id = idPost;
        this.text = text;
        this.date = date;
        this.place = place;
        this.picture = pic;
    }

    public Post(String idPost, JSONObject jObjPost) {
        users = new ArrayList<User>();
        animals = new ArrayList<Animal>();

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
            if(!jObjPost.isNull("lat") && !jObjPost.isNull("lon"))
                place = new LatLng(jObjPost.getDouble("lat"), jObjPost.getDouble("lon"));

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

    //TODO memorizzare in un array gli user e gli animal taggati nel post = due ArrayList
    //TODO scrivere metodi che dati gli id possano recuperare le info dei suddetti (meglio in Post o in User e Animal?)
}
