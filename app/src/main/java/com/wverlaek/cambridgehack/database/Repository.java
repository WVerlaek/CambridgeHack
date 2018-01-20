package com.wverlaek.cambridgehack.database;

import android.util.Log;

import com.google.firebase.database.Query;
import com.wverlaek.cambridgehack.database.models.Profile;

/**
 * Created by rolf on 1/20/18.
 */

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;


public class Repository {
    public String TAG = "Repository";
    private DatabaseReference mDatabase;

    public Repository() {
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
    }

    public void getProfile(final String uid, final ProfileListener list) {
        Log.d(TAG, "getProfile " + uid);
        mDatabase.child("profiles").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Profile prof = dataSnapshot.getValue(Profile.class);

                        Log.d(TAG, "onDataChange " + uid);

                        // [START_EXCLUDE]
                        if (prof == null) {
//                            Log.e(TAG, "Profile " + uid + " is unexpectedly null");
                        } else {
                            prof.uid = uid;
                        }
                        list.retrieveDone(prof);
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // [START_EXCLUDE]
                        Log.w(TAG, "getProfile:onCancelled", databaseError.toException());
                        list.onError(databaseError);
                        // [END_EXCLUDE]
                    }
                });
    }

    public void updateProfile(final Profile prof) {
        DatabaseReference profiles = mDatabase.child("profiles");
        profiles.child(prof.uid).setValue(prof);
    }

    public void getProfileByPersonId(final UUID personId, final ProfileListener list) {
        Log.d(TAG, "getPersonId " + personId);
        mDatabase.child("profiles").orderByChild("personId").equalTo(personId.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Profile prof = dataSnapshot.getValue(Profile.class);

                        Log.d(TAG, "onDataChange " + personId.toString());
                        list.retrieveDone(prof);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // [START_EXCLUDE]
                        Log.w(TAG, "getProfile:onCancelled", databaseError.toException());
                        list.onError(databaseError);
                        // [END_EXCLUDE]
                    }
                });
    }
}
