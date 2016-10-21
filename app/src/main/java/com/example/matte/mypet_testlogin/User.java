package com.example.matte.mypet_testlogin;

import org.json.JSONObject;

/**
 * Memorizza un utente
 */

public class User {
    public String id;
    public String username;
    public String name;
    public String surname;
    public String gender;
    public String birthDate; //TODO salvare in formato decente
    //TODO pic
    public String profilepic;

    public User(){}

    public User(String idUser, JSONObject jObjUser){
        try {
            id = idUser;
            username = jObjUser.getString("username");
            name = jObjUser.getString("name");
            surname = jObjUser.getString("surname");
            gender = jObjUser.getString("gender");
            birthDate = jObjUser.getString("birthDate");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
