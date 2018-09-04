package com.example.yggdrasil.realdiabeticapp.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Yggdrasil on 6/2/2561.
 */

public class UserAccount extends RealmObject {

    @PrimaryKey
    private String Email;
    @Required
    private String Password;
    @Required
    private Boolean update;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }
}
