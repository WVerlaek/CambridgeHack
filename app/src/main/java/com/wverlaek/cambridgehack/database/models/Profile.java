package com.wverlaek.cambridgehack.database.models;

import android.support.annotation.NonNull;

/**
 * Created by rolf on 1/20/18.
 */

public class Profile {
    public String uid = null;

    public String firstName;
    public String lastName;
    public String title;
    public String organization;
    public String linkedInName;
    public String githubName;
    public String facebookName;
    public String personId;
    public String email;

    //TODO
//    public Image getProfilePhoto() {
//
//    }

    public Profile() {}

//    public Profile(String firstName, String lastNamet, String facebookLink, String background) {
//        this.name = name;
//        this.facebookLink = facebookLink;
//        this.background = background;
//    }

    public String getDisplayName() {
        return firstName + " " + lastName;
    }
}
