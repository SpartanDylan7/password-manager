package org.eagle.passwordmanager.controller;

import org.eagle.passwordmanager.model.User;

public abstract class BaseController {
    protected static User user;

    private String fxmlName;


    public void setFxmlName(String fxmlName) {
        this.fxmlName = fxmlName;
    }

    public String getFxmlName() {
        return fxmlName;
    }


}
