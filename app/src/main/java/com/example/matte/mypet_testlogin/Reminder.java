package com.example.matte.mypet_testlogin;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contiene le informazioni di un singolo promemoria
 */

public class Reminder implements Comparable<Reminder> {
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

    /**
     * Confronta due reminder
     *
     * @param other altro reminder con cui fare il confronto
     * @return 0 se sono non comparabili o hanno stessa data <br/>
     *          1 se il reminder è più recente di other <br/>
     *          -1 se il reminder è più vecchio di other <br/>
     */
    @Override
    public int compareTo(Reminder other) {
        if(other == null)
            return 0;
        if(this.equals(other))
            return 0;
        return -(this.eventtime.compareTo(other.eventtime));
    }
}
