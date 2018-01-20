package com.wverlaek.cambridgehack.database.models;

import android.support.annotation.NonNull;

/**
 * Created by rolf on 1/20/18.
 */

public class Profile {
    public String uid = null;
    public String name;
    public String facebookLink;

    public Profile() {}

    public Profile(String name, String facebookLink) {
        this.name = name;
        this.facebookLink = facebookLink;
    }
}
