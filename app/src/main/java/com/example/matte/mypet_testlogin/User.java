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
    public String birthdate; //TODO salvare in formato decente (SimpleDateFormat. suppressLint potrebbe servire)
    public String profilepic;

    public User(){}

    public User(String idUser, JSONObject jObjUser){
        try {
            id = idUser;
            username = jObjUser.getString("username");
            if(!jObjUser.isNull("name"))
                name = jObjUser.getString("name");
            if(!jObjUser.isNull("surname"))
                surname = jObjUser.getString("surname");
            if(!jObjUser.isNull("gender"))
                gender = jObjUser.getString("gender");
            if(!jObjUser.isNull("birthDate"))
                birthdate = jObjUser.getString("birthDate");
            if(!jObjUser.isNull("profilePic"))
                profilepic = HomeActivity.IMG_BASEURL + jObjUser.getString("profilePic");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
