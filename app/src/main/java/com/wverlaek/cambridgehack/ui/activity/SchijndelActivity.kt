package com.wverlaek.cambridgehack.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.wverlaek.cambridgehack.R
import kotlinx.android.synthetic.main.activity_schijndel.*
import org.jetbrains.anko.intentFor

class SchijndelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schijndel)

        scanBtn.setOnClickListener {
            startActivity(intentFor<FaceScanActivity>())
        }

        logoutBtn.setOnClickListener {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        startActivity(intentFor<MainActivity>())
                        finish()
                    }
        }
    }
}
