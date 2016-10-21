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
            species = jObjPet.getString("species");
            gender = jObjPet.getString("gender");
            birthdate = jObjPet.getString("birthdate");
            //TODO pic
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
