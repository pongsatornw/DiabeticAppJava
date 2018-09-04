package com.example.yggdrasil.realdiabeticapp.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Yggdrasil on 28/2/2561.
 */

public class NotifyModel extends RealmObject{

    @PrimaryKey
    private String Email;
    @Required
    private Boolean viaSMS;
    @Required
    private Boolean viaEmail;
    @Required
    private Boolean everyTime;
    @Required
    private Boolean hyper;
    @Required
    private Boolean hypo;
    @Required
    private Boolean hi;
    @Required
    private Boolean lo;


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public Boolean getViaSMS() {
        return viaSMS;
    }

    public void setViaSMS(Boolean viaSMS) {
        this.viaSMS = viaSMS;
    }

    public Boolean getViaEmail() {
        return viaEmail;
    }

    public void setViaEmail(Boolean viaEmail) {
        this.viaEmail = viaEmail;
    }

    public Boolean getEveryTime() {
        return everyTime;
    }

    public void setEveryTime(Boolean everyTime) {
        this.everyTime = everyTime;
    }

    public Boolean getHyper() {
        return hyper;
    }

    public void setHyper(Boolean hyper) {
        this.hyper = hyper;
    }

    public Boolean getHypo() {
        return hypo;
    }

    public void setHypo(Boolean hypo) {
        this.hypo = hypo;
    }

    public Boolean getHi() {
        return hi;
    }

    public void setHi(Boolean hi) {
        this.hi = hi;
    }

    public Boolean getLo() {
        return lo;
    }

    public void setLo(Boolean lo) {
        this.lo = lo;
    }
}
