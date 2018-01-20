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

    //TODO
//    public Image getProfilePhoto() {
//
//    }
    private String personId;

    public Profile() {}

//    public Profile(String firstName, String lastNamet, String facebookLink, String background) {
//        this.name = name;
//        this.facebookLink = facebookLink;
//        this.background = background;
//    }
}
