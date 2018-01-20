package com.wverlaek.cambridgehack.database.models;

import android.support.annotation.NonNull;

/**
 * Created by rolf on 1/20/18.
 */

public class Profile {
    public String uid = null;

    public String getName() {
        return name;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public String getBackground() {
        return background;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String name;
    public String facebookLink;
    public String background;
    private String personId;

    public Profile() {}

    public Profile(String name, String facebookLink, String background) {
        this.name = name;
        this.facebookLink = facebookLink;
        this.background = background;
    }
}
