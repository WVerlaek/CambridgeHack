package com.wverlaek.cambridgehack.ui.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.database.DatabaseError
import com.wverlaek.cambridgehack.R
import com.wverlaek.cambridgehack.database.ProfileListener
import com.wverlaek.cambridgehack.database.Repository
import com.wverlaek.cambridgehack.database.models.Profile
import kotlinx.android.synthetic.main.activity_show_profile.*
import org.jetbrains.anko.intentFor

class ShowProfileActivity : AppCompatActivity() {

    companion object {
        private val PROFILE_ID = "SHOW_PROFILE_ID"

        fun createIntent(context: Context, profileId: String): Intent {
            return context.intentFor<ShowProfileActivity>(PROFILE_ID to profileId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        if (intent == null || !intent.hasExtra(PROFILE_ID)) return

        setContentView(R.layout.activity_show_profile)

        val profileId = intent.getStringExtra(PROFILE_ID)
        val repo = Repository()

        repo.getProfile(profileId, object : ProfileListener {
            override fun retrieveDone(prof: Profile) {
                loadingFrame.visibility = View.GONE
                txtTitle.text = "TODO"
                txtOrg.text = "TODO"
                txtName.text = prof.firstName + " " + prof.lastName
                txtUrl.text = prof.title + " at " + prof.organization
                // show profile
            }

            override fun onError(de: DatabaseError?) {}
        })
    }
}
