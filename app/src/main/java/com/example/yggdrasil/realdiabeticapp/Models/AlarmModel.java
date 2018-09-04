package com.example.yggdrasil.realdiabeticapp.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Yggdrasil on 11/2/2561.
 */

public class AlarmModel extends RealmObject{

    @Required
    private String Email;
    @Required
    private String ID;
    @Required
    private String Mode;
    @Required
    private String DatyOfWeek;    // Set for repeat daily;
    @Required
    private String HourOfDay;       // Set for repeat daily;
    @Required
    private String MinuteOfHour;    // Set for repeat daily and



    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDatyOfWeek() {
        return DatyOfWeek;
    }

    public void setDatyOfWeek(String datyOfWeek) {
        DatyOfWeek = datyOfWeek;
    }

    public String getHourOfDay() {
        return HourOfDay;
    }

    public void setHourOfDay(String hourOfDay) {
        HourOfDay = hourOfDay;
    }

    public String getMinuteOfHour() {
        return MinuteOfHour;
    }

    public void setMinuteOfHour(String minuteOfHour) {
        MinuteOfHour = minuteOfHour;
    }

}
