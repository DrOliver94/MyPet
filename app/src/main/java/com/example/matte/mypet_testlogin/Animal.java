package com.example.matte.mypet_testlogin;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Memorizza un animale
 */
public class Animal {
    public String id;
    public String name;
    public String species;
    public String gender;
    public String profilepic;
    public Date birthdate;

    public Animal(){}

    public Animal(String idPet, JSONObject jObjPet){
        try {
            id = idPet;
            name = jObjPet.getString("name");
            if(!jObjPet.isNull("species"))
                species = jObjPet.getString("species");
            if(!jObjPet.isNull("gender"))
                gender = jObjPet.getString("gender");
            if(!jObjPet.isNull("birthdate")){
                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
                birthdate = format.parse(jObjPet.getString("birthdate"));
            }
            if(!jObjPet.isNull("profilePic"))
                profilepic = HomeActivity.IMG_BASEURL + jObjPet.getString("profilePic");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
