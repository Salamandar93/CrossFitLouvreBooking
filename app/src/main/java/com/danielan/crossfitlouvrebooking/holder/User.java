package com.danielan.crossfitlouvrebooking.holder;

import java.io.Serializable;

/**
 * Created by Daniel AN on 13/09/2015.
 */
public class User implements Serializable {
    private String mUsername;
    private String mPassword;

    public User(String username, String password) {
        this.mUsername = username;
        this.mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }
}
