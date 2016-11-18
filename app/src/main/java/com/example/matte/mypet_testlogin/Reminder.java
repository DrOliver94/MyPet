package com.example.matte.mypet_testlogin;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contiene le informazioni di un singolo promemoria
 */

public class Reminder {
    public String id;
    public String iduser;
    public String idanim;
    public String eventname;
    public String eventplace;
    public Date eventtime;
    public String animname;
    public String animpic;

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
            if(!jObjReminder.isNull("rTime")) {
                SimpleDateFormat format = new SimpleDateFormat("y-MM-dd HH:mm:ss");
                eventtime = format.parse(jObjReminder.getString("rTime"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
