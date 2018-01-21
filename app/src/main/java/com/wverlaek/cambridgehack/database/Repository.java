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
import com.wverlaek.cambridgehack.util.Listener;

import java.util.ArrayList;
import java.util.List;
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

                        if (prof == null) {
                        } else {
                            prof.uid = uid;
                        }
                        list.retrieveDone(prof);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getProfile:onCancelled", databaseError.toException());
                        list.onError(databaseError);
                    }
                });
    }

    public void updateProfile(final Profile prof) {
        DatabaseReference profiles = mDatabase.child("profiles");
        profiles.child(prof.uid).setValue(prof);
    }

    public void getProfilesByPersonIds(final List<UUID> personIds, final Listener<List<Profile>> listener) {
        mDatabase.child("profiles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Profile> result = new ArrayList<>();
                for (int i = 0; i < personIds.size(); i++) {
                    result.add(null);
                }
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Profile profile = child.getValue(Profile.class);
                    if (profile != null) {
                        for (int i = 0; i < personIds.size(); i++) {
                            UUID uuid = personIds.get(i);
                            if (uuid != null) {
                                if (uuid.toString().equals(profile.personId)) {
                                    result.set(i, profile);
                                }
                            }
                        }
                    }
                }
                listener.onComplete(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError();
            }
        });
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
