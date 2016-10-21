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
    public String birthdate; //TODO salvare in formato decente
    public String profilepic;

    public User(){}

    public User(String idUser, JSONObject jObjUser){
        try {
            id = idUser;
            username = jObjUser.getString("username");
            if(jObjUser.has("name"))
                name = jObjUser.getString("name");
            if(jObjUser.has("surname"))
                surname = jObjUser.getString("surname");
            if(jObjUser.has("gender"))
                gender = jObjUser.getString("gender");
            if(jObjUser.has("birthDate"))
                birthdate = jObjUser.getString("birthDate");
            if(jObjUser.has("profilePic"))
                profilepic = jObjUser.getString("profilePic");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
