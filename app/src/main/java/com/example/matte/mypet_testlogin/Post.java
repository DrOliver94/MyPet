package com.example.matte.mypet_testlogin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Contiene le informazioni di un singolo post
 *
 */
public class Post {
    public String id;
    public String idAuthor;
    public String nameAuthor;
    public String picture;
    public String text;
    public String date;
    public String place;
    public ArrayList<User> users;
    public ArrayList<Animal> animals;

    public Post() {}

    public Post(String idPost, String text, String date, String place, String pic) {
        this.id = idPost;
        this.text = text;
        this.date = date;
        this.place = place;
        this.picture = pic;
    }

    public Post(String idPost, JSONObject jObjPost) {
        try {
            id = idPost;
            idAuthor = jObjPost.getString("author");
            nameAuthor = jObjPost.getString("nameauthor");
            //TODO picAuthor
            //TODO pic
            text = jObjPost.getString("text");
            date = jObjPost.getString("date");
            place = jObjPost.getString("place");

            JSONObject jUsers = jObjPost.getJSONObject("users");
            JSONArray idUsers = jUsers.names();                 //recupera elenco ID degli utenti
            for (int i = 0; i < idUsers.length(); i++) {        //per ogni utente nell'obj
                String idUser = (String) idUsers.get(i);        //recupera ID
                JSONObject jUser = jUsers.getJSONObject(idUser);//recupera utente
                User u = new User(idUser, jUser);               //Crea obj
                users.add(u);                                   //Inserisce nell'arrayList
            }

            JSONObject jPets = jObjPost.getJSONObject("pets");
            JSONArray idPets = jPets.names();                   //recupera elenco ID degli animali
            for (int i = 0; i < idPets.length(); i++) {         //per ogni animale nell'obj
                String idPet = (String) idPets.get(i);          //recupera ID
                JSONObject jPet = jPets.getJSONObject(idPet);   //recupera animale
                Animal p = new Animal(idPet, jPet);             //Crea obj
                animals.add(p);                                 //Inserisce nel l'arrayList
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO memorizzare in un array gli user e gli animal taggati nel post = due ArrayList
    //TODO scrivere metodi che dati gli id possano recuperare le info dei suddetti (meglio in Post o in User e Animal?)
}
