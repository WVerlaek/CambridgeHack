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

    public String name;
    public String facebookLink;
    public String background;

    public Profile() {}

    public Profile(String name, String facebookLink, String background) {
        this.name = name;
        this.facebookLink = facebookLink;
        this.background = background;
    }
}
