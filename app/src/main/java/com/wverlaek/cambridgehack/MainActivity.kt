package com.wverlaek.cambridgehack

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.wverlaek.cambridgehack.database.ProfileListener
import com.wverlaek.cambridgehack.database.Repository
import com.wverlaek.cambridgehack.database.models.Profile

class MainActivity : AppCompatActivity() {
    val TAG: String  = "Main";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val resp = Repository();

        resp.getProfile("rolf", object : ProfileListener {
            override fun retrieveDone(prof: Profile) {
                Log.d(TAG, "name " + prof.name + " link " + prof.facebookLink + " uid " + prof.uid);
            }
        });
    }
}
