package com.wverlaek.cambridgehack.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DatabaseError
import com.wverlaek.cambridgehack.R
import com.wverlaek.cambridgehack.database.ImageRepository
import com.wverlaek.cambridgehack.database.ProfileListener
import com.wverlaek.cambridgehack.database.Repository
import com.wverlaek.cambridgehack.database.models.Profile
import com.wverlaek.cambridgehack.util.Listener
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
        val imageRepo = ImageRepository()

        title = "Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)

        repo.getProfile(profileId, object : ProfileListener {
            override fun retrieveDone(prof: Profile) {
                loadingFrame.visibility = View.GONE
                txtTitle.text = prof.title + " - " + prof.organization
                txtName.text = prof.displayName
                email.text = prof.email
                // show profile
            }

            override fun onError(de: DatabaseError?) {}
        })

        imageRepo.getProfileIconUri(profileId, object : Listener<Uri> {
            override fun onComplete(result: Uri) {
                Glide.with(this@ShowProfileActivity)
                        .load(result)
                        .apply(RequestOptions.circleCropTransform())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView)
            }

            override fun onError() {
                Glide.with(this@ShowProfileActivity)
                        .load(R.mipmap.ic_launcher_round)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView)
            }
        })

        close_button.setOnClickListener {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
