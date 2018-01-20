package com.wverlaek.cambridgehack.database;

import android.util.Log;

import com.wverlaek.cambridgehack.database.models.Profile;

/**
 * Created by rolf on 1/20/18.
 */

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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
                        prof.uid = uid;

                        Log.d(TAG, "onDataChange " + uid);

                        // [START_EXCLUDE]
                        if (prof == null) {
                            // Profile is null, error out
                            Log.e(TAG, "Profile " + uid + " is unexpectedly null");
//                            Toast.makeText(NewPostActivity.this,
//                                    "Error: could not fetch user.",
//                                    Toast.LENGTH_SHORT).show();
                        } else {
                            list.retrieveDone(prof);
                        }
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getProfile:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }


}
