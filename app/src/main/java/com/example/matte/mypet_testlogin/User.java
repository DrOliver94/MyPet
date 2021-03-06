package com.example.matte.mypet_testlogin;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Memorizza un utente
 */

public class User implements Comparable<User> {
    public String id;
    public String username;
    public String name;
    public String surname;
    public String gender;
    public Date birthdate;
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
            if(!jObjUser.isNull("birthDate")) {
                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd");
                birthdate = format.parse(jObjUser.getString("birthDate"));
            }
            if(!jObjUser.isNull("profilePic"))
                profilepic = HomeActivity.IMG_BASEURL + jObjUser.getString("profilePic");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Confronta due utenti
     *
     * @param other altro utente con cui fare il confronto
     * @return 0 se other è null o hanno stessa data <br/>
     *          1 se l'utente ha un nome alfabeticamente precedente ad other <br/>
     *          -1 se l'utente ha un nome alfabeticamente successivo ad other <br/>
     */
    @Override
    public int compareTo(User other) {
        if(other == null)
            return 0;
        if(this.equals(other))
            return 0;
        return (this.username.compareTo(other.username));
    }
}
