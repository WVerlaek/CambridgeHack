package com.wverlaek.cambridgehack.database;

import com.wverlaek.cambridgehack.database.models.Profile;

/**
 * Created by rolf on 1/20/18.
 */

public interface ProfileListener {
    void retrieveDone(Profile prof);
}
