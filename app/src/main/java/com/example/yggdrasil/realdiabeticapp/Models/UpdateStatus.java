package com.example.yggdrasil.realdiabeticapp.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Yggdrasil on 16/4/2561.
 */

public class UpdateStatus extends RealmObject {

    @PrimaryKey
    private String Email;
    @Required
    private Boolean profile;
    @Required
    private Boolean caregiver_profile;
    @Required
    private Boolean glucose_value;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public boolean isProfile() {
        return profile;
    }

    public void setProfile(Boolean profile) {
        this.profile = profile;
    }

    public boolean isCaregiver_profile() {
        return caregiver_profile;
    }

    public void setCaregiver_profile(Boolean caregiver_profile) {
        this.caregiver_profile = caregiver_profile;
    }

    // True is already update, false is waiting for update
    public Boolean isGlucose_value() {
        return glucose_value;
    }

    public void setGlucose_value(Boolean glucose_value) {
        this.glucose_value = glucose_value;
    }
}
