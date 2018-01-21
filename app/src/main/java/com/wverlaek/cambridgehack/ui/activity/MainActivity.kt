package com.wverlaek.cambridgehack.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.wverlaek.cambridgehack.R

class MainActivity : AppCompatActivity() {

    private val rcSignIn = 123
    private val TAG = "MainActivity"
    private lateinit var auth : FirebaseAuth

    private val providers = listOf(
            AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
            AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
            AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
    }

    override fun onResume() {
        super.onResume()
        checkUserLogin()
    }

    private fun checkUserLogin() {
        val user = auth.currentUser
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
            onLoggedIn(user)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            val resp = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                onLoggedIn(auth.currentUser!!)
            } else {
                // Sign in failed, check response for error code
                finish()
            }
        }
    }

    private fun onLoggedIn(user: FirebaseUser) {
        startActivity(ProfileActivity.createIntent(this, user.uid))
    }
}
