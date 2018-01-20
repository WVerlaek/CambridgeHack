package com.wverlaek.cambridgehack.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.database.DatabaseError
import com.wverlaek.cambridgehack.R
import com.wverlaek.cambridgehack.database.ProfileListener
import com.wverlaek.cambridgehack.database.Repository
import com.wverlaek.cambridgehack.database.models.Profile
import kotlinx.android.synthetic.main.content_profile.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.wverlaek.cambridgehack.detection.FaceDetection
import java.util.*


class ProfileActivity : AppCompatActivity() {
    val TAG = "PROFILE"
    val PICK_IMAGE = 1
    var repo : Repository = Repository()
    private lateinit var uid: String
    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.getReference()


    companion object {
        private val UID_TAG = "UID"

        fun createIntent(context: Context, uid: String): Intent {
            return context.intentFor<ProfileActivity>(UID_TAG to uid)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (intent == null || !intent.hasExtra(UID_TAG)) return
        uid = intent.getStringExtra(UID_TAG)

        repo.getProfile(uid, object : ProfileListener {
            override fun retrieveDone(prof: Profile?) {
                if (prof != null) {
                    startActivity(intentFor<FaceScanActivity>())
                    // TODO: start activity real app
                    finish()
                } else {
                    loadingFrame.visibility = View.GONE

                    btn_submit.setOnClickListener {
                        val newProf = Profile();

                        newProf.uid = uid
                        newProf.firstName = first_name_field.text.toString()
                        newProf.lastName = last_name_field.text.toString()
                        newProf.title = title_field.text.toString()
                        newProf.organization = organization_field.text.toString()
                        newProf.facebookName = facebook_field.text.toString()
                        newProf.githubName = github_field.text.toString()
                        newProf.linkedInName = linkedIn_field.text.toString()

                        repo.updateProfile(newProf)
                        toast("Created your profile")
                        startActivity(intentFor<FaceScanActivity>())
                        // TODO: start activity real app
                        finish()
                    }

                    btn_photo.setOnClickListener {
                        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
                        getIntent.type = "image/*"

                        val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        pickIntent.type = "image/*"

                        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

                        startActivityForResult(chooserIntent, PICK_IMAGE)
                    }
                }
            }

            override fun onError(de: DatabaseError?) {
                toast("Firebase error on retrieving profile $uid:$de")
            }
        })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d(TAG, "onActivityResult")
        if (requestCode == PICK_IMAGE) {
            val uri : Uri = data.getData()
            Log.d(TAG, "selected image: " + uri)
            val ref = storageRef.child("images/" + uid)

            FaceDetection().uploadImage(UUID.fromString("f1cbfc9b-0840-4fa3-87eb-171f81cec429"),
                    object : com.wverlaek.cambridgehack.util.Listener<Unit?> {
                        override fun onComplete(result: Unit?) {}
                        override fun onError() {}
                    },
                    getContentResolver().openInputStream(uri))

            ref.putFile(uri)

            toast("Your picture is stored ")

            profile_photo.setImageURI(uri)
        }
    }
}
