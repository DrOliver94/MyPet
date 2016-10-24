package com.example.matte.mypet_testlogin;

import org.json.JSONObject;

/**
 * Contiene le informazioni di un singolo promemoria
 */

public class Reminder {
    String id;
    String iduser;
    String idanim;
    String eventname;
    String eventplace;
    String eventtime;

    public Reminder(){}

    public Reminder(JSONObject jObjReminder){
        try {
            id = jObjReminder.getString("rId");
            if(!jObjReminder.isNull("uId"))
                iduser = jObjReminder.getString("uId");
            if(!jObjReminder.isNull("aId"))
                idanim = jObjReminder.getString("aId");
            if(!jObjReminder.isNull("rName"))
                eventname = jObjReminder.getString("rName");
            if(!jObjReminder.isNull("rPlace"))
                eventplace = jObjReminder.getString("rPlace");
            if(!jObjReminder.isNull("rTime"))
                eventtime = jObjReminder.getString("rTime");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
