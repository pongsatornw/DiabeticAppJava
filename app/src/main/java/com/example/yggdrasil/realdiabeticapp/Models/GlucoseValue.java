package com.example.yggdrasil.realdiabeticapp.Models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by Yggdrasil on 7/2/2561.
 */

public class GlucoseValue extends RealmObject {

    @Required
    private String Email;
    @Required
    private String Value;
    @Required
    private Date Date;
    @Required
    private String Time;
    @Required
    private String Measured;
    @Required
    private String Status;
    @Required Boolean Update;

    public Boolean getUpdate() {
        return Update;
    }

    public void setUpdate(Boolean update) {
        this.Update = update;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getMeasured() {
        return Measured;
    }

    public void setMeasured(String measured) {
        Measured = measured;
    }

}
