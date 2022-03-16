package org.eagle.passwordmanager.controller;

import org.eagle.passwordmanager.model.User;

public abstract class BaseController {
    protected static User user;//This user will be used by all controllers that extend BaseController

    private String fxmlName;


    public void setFxmlName(String fxmlName) {
        this.fxmlName = fxmlName;
    }

    public String getFxmlName() {
        return fxmlName;
    }


}
