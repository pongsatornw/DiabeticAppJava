package com.example.yggdrasil.realdiabeticapp.Models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Yggdrasil on 6/2/2561.
 */

public class UserProfile extends RealmObject {

    @PrimaryKey
    private String Email;
    @Required
    private String FName;
    @Required
    private String LName;
    @Required
    private String Gender;
    @Required
    private Date Birthdate;
    @Required
    private String Tel;

    public String getTel() {return Tel;}

    public void setTel(String tel) { Tel = tel;}

    public String getEmail() { return Email;}

    public void setEmail(String email) {
        Email = email;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public Date getBirthdate() {
        return Birthdate;
    }

    public void setBirthdate(Date birthdate) {
        Birthdate = birthdate;
    }
}
