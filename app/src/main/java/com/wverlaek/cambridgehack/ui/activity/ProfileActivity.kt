package com.wverlaek.cambridgehack.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.system.Os.bind
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseError
import com.wverlaek.cambridgehack.R
import com.wverlaek.cambridgehack.database.ProfileListener
import com.wverlaek.cambridgehack.database.Repository
import com.wverlaek.cambridgehack.database.models.Profile
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.toast

class ProfileActivity : AppCompatActivity() {
    val TAG = "PROFILE"
    val UID_TAG = "UID"
    var repo : Repository = Repository()
    private lateinit var nameText: TextView
    private lateinit var backgroundText: TextView
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (intent == null || !intent.hasExtra(UID_TAG)) return
        val uid = intent.getStringExtra(UID_TAG)

        nameText = findViewById(R.id.name_field)
        backgroundText = findViewById(R.id.background_field)

        repo.getProfile(uid, object : ProfileListener {
            override fun retrieveDone(prof: Profile?) {
                if (prof != null) {
                    nameText.setText(prof.name)
                    backgroundText.setText(prof.background)
                    Log.d(TAG, "name " + prof.name + " link " + prof.facebookLink + " uid " + prof.uid)
                } else {
                    // nothing, no profile data to set
                }
            }

            override fun onError(de: DatabaseError?) {
                toast("Firebase error on retrieving profile $uid:$de")
            }
        })

        btnSubmit = findViewById(R.id.btn_submit)

        btnSubmit.setOnClickListener {
            var prof = Profile(nameText.text.toString(), "no link",
                    backgroundText.text.toString())
            prof.uid = uid
            repo.updateProfile(prof)
            toast("Updated your profile")
            finish()
        }
    }



}
