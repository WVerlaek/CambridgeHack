package com.wverlaek.cambridgehack

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.*
import com.google.firebase.auth.FirebaseAuth
import java.util.Arrays
import android.content.Intent
import android.util.Log
import com.google.firebase.database.DatabaseError
import com.wverlaek.cambridgehack.database.ProfileListener
import com.wverlaek.cambridgehack.database.Repository
import com.wverlaek.cambridgehack.database.models.Profile



class MainActivity : AppCompatActivity() {
    private val rcSignIn = 123
    val TAG: String  = "Main";


    private val providers = listOf(
            AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
//            AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
//            AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        tryAccess()

    }

    private fun tryAccess() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAllowNewEmailAccounts(true)
                            .setAvailableProviders(providers)
                            .build(),
                    rcSignIn)
        } else {
            // continue to main activity / app
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
//                tryAccess()
            } else {
                // Sign in failed, check response for error code
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val resp = Repository()

        resp.getProfile("rolf", object : ProfileListener {
            override fun retrieveDone(prof: Profile) {
                Log.d(TAG, "name " + prof.name + " link " + prof.facebookLink + " uid " + prof.uid)
            }

            override fun onError(de: DatabaseError?) {
            }
        })
    }
}