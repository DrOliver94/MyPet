package com.example.matte.mypet_testlogin;

import org.json.JSONObject;

/**
 * Memorizza un animale
 */
public class Animal {
    public String id;
    public String name;
    public String species;
    public String gender;
    public String profilepic;
    public String birthdate; //TODO trovare miglior tipo di variabile

    public Animal(){}

    public Animal(String idPet, JSONObject jObjPet){
        try {
            id = idPet;
            name = jObjPet.getString("name");
            if(!jObjPet.isNull("species"))
                species = jObjPet.getString("species");
            if(!jObjPet.isNull("gender"))
                gender = jObjPet.getString("gender");
            if(!jObjPet.isNull("birthdate"))
                birthdate = jObjPet.getString("birthdate");
            if(!jObjPet.isNull("profilePic"))
                profilepic = jObjPet.getString("profilePic");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
