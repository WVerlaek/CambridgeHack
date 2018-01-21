package com.wverlaek.cambridgehack.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import com.wverlaek.cambridgehack.database.models.github.Repo
import com.wverlaek.cambridgehack.database.models.github.User
import com.wverlaek.cambridgehack.network.GithubService
import com.wverlaek.cambridgehack.util.Listener
import kotlinx.android.synthetic.main.activity_show_profile.*
import kotlinx.android.synthetic.main.github_repo_item.view.*
import org.jetbrains.anko.intentFor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

                if (!prof.facebookName.isEmpty()) {
                    fb_button.visibility = View.VISIBLE
                }
                if (!prof.linkedInName.isEmpty()) {
                    linkedin_button.visibility = View.VISIBLE
                }
                if (!prof.githubName.isEmpty()) {
                    github_button.visibility = View.VISIBLE
                }

                fb_button.setOnClickListener {
//                    try {
//                        openUrl("fb://profile/${prof.facebookName}")
//                    } catch (e: Exception) {
                        openUrl("https://facebook.com/${prof.facebookName}")
//                    }
                }
                linkedin_button.setOnClickListener {
//                    try {
//                        openUrl("linkedin://profile/${prof.linkedInName}")
//                    } catch (e: Exception) {
//                        Log.e("ShowProfileActivity", "linkedin error", e)
                        openUrl("https://linkedin.com/in/${prof.linkedInName}")
//                    }
                }
                github_button.setOnClickListener {
                    openUrl("https://github.com/${prof.githubName}")
                }
                GithubService.create().apply {
                    getUser(prof.githubName).enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>?, response: Response<User>) {
                            if (response.isSuccessful) {
                                response.body()?.apply {
                                    if (!isFinishing) {
                                        github_name.text = login
                                        Glide.with(this@ShowProfileActivity)
                                                .load(Uri.parse(avatar_url))
                                                .apply(RequestOptions.circleCropTransform())
                                                .transition(DrawableTransitionOptions.withCrossFade())
                                                .into(github_profile_icon)
                                        github_nr_repos.text = "$public_repos public repositories"

                                        github_user_item.setOnClickListener {
                                            openUrl(html_url)
                                        }
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<User>?, t: Throwable?) {

                        }
                    })
                    getRepos(prof.githubName).enqueue(object : Callback<List<Repo>> {
                        override fun onFailure(call: Call<List<Repo>>?, t: Throwable?) {

                        }

                        override fun onResponse(call: Call<List<Repo>>?, response: Response<List<Repo>>) {
                            if (response.isSuccessful) {
                                response.body()?.apply {
                                    github_repos.removeAllViews()
                                    github_featured.visibility = View.VISIBLE
                                    for (gitRepo in this.take(3)) {
                                        val view = LayoutInflater.from(this@ShowProfileActivity)
                                                .inflate(R.layout.github_repo_item, github_repos, false)
                                        view.repo_name.text = gitRepo.name
                                        view.setOnClickListener {
                                            openUrl(gitRepo.html_url)
                                        }
                                        github_repos.addView(view)
                                    }
                                }
                            }
                        }
                    })
                }
            }

            override fun onError(de: DatabaseError?) {}
        })

        imageRepo.getProfileIconUri(profileId, object : Listener<Uri> {
            override fun onComplete(result: Uri) {
                if (!isFinishing) {
                    Glide.with(this@ShowProfileActivity)
                            .load(result)
                            .apply(RequestOptions.circleCropTransform())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(imageView)
                }
            }

            override fun onError() {
                if (!isFinishing) {
                    Glide.with(this@ShowProfileActivity)
                            .load(R.mipmap.ic_launcher_round)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(imageView)
                }
            }
        })

        close_button.setOnClickListener {
            finish()
        }
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
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
