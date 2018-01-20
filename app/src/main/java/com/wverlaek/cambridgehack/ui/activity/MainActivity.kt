package com.wverlaek.cambridgehack.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.wverlaek.cambridgehack.R
import com.wverlaek.cambridgehack.database.ProfileListener
import com.wverlaek.cambridgehack.database.Repository
import com.wverlaek.cambridgehack.database.models.Profile
import com.wverlaek.cambridgehack.util.Permissions
import org.jetbrains.anko.intentFor


class MainActivity : AppCompatActivity() {

    private val rcSignIn = 123
    private val TAG = "Main"

    private val REQUEST_CAMERA = 1

    private val providers = listOf(
            AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
//            AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
//            AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Permissions.hasCameraPermission(this)) {
            Permissions.requestCameraPermission(this, REQUEST_CAMERA)
        } else {
            openNextActivity()
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // close app
                finish()
            } else {
                openNextActivity()
            }
        }
    }

    private fun openNextActivity() {
        var profInt = Intent(this, ProfileActivity::class.java)
        profInt.putExtra("UID", "gvd")
        startActivity(profInt)
    }
}
