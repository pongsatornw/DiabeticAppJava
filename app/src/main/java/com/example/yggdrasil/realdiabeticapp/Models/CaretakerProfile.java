package com.example.yggdrasil.realdiabeticapp.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Yggdrasil on 6/2/2561.
 */

public class CaretakerProfile extends RealmObject {

    @PrimaryKey
    private String Email;
    @Required
    private String cFName;
    @Required
    private String cLName;
    @Required
    private String cEmail;
    @Required
    private String cTel;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getcFName() {
        return cFName;
    }

    public void setcFName(String cFName) {
        this.cFName = cFName;
    }

    public String getcLName() {
        return cLName;
    }

    public void setcLName(String cLName) {
        this.cLName = cLName;
    }

    public String getcEmail() {
        return cEmail;
    }

    public void setcEmail(String cEmail) {
        this.cEmail = cEmail;
    }

    public String getcTel() {
        return cTel;
    }

    public void setcTel(String cTel) {
        this.cTel = cTel;
    }

}
