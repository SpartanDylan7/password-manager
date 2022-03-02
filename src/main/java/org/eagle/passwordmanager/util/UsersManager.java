package org.eagle.passwordmanager.util;

import org.eagle.passwordmanager.model.User;

public class UsersManager {
    private static UsersManager INSTANCE;
    private static String userPasswordHash;
    private static User currentUser;

    private UsersManager() {

    }

    public static UsersManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UsersManager();
        }
        return INSTANCE;
    }


    public void setCurrentUser(User user){
        currentUser = user;
        userPasswordHash = currentUser.getPasswordHash();
    }


    public String getUserPasswordHash() {
        return userPasswordHash;
    }


}
