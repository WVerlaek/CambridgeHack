package com.wverlaek.cambridgehack.ui.activity

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.wverlaek.cambridgehack.R
import com.wverlaek.cambridgehack.database.ImageRepository
import com.wverlaek.cambridgehack.util.Listener
import kotlinx.android.synthetic.main.activity_schijndel.*
import org.jetbrains.anko.intentFor

class SchijndelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schijndel)

        scanBtn.setOnClickListener {
            startActivity(intentFor<FaceScanActivity>())
        }

        FirebaseAuth.getInstance().currentUser?.let {
            val user = it
            ImageRepository().getProfileIconUri(user.uid, object : Listener<Uri> {
                override fun onComplete(result: Uri) {
                    if (!isFinishing) {
                        Glide.with(this@SchijndelActivity)
                                .load(result)
                                .apply(RequestOptions.circleCropTransform())
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(profile_icon)
                    }
                }

                override fun onError() {
                    if (!isFinishing) {
                        Glide.with(this@SchijndelActivity)
                                .load(R.mipmap.ic_launcher_round)
                                .apply(RequestOptions.circleCropTransform())
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(profile_icon)
                    }
                }
            })

            view_profile_button.setOnClickListener {
                startActivity(ShowProfileActivity.createIntent(this, user.uid))
            }

            name.text = it.displayName
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
