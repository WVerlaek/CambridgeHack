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
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import java.net.URI


class ProfileActivity : AppCompatActivity() {
    val TAG = "PROFILE"
    val UID_TAG = "UID"
    val PICK_IMAGE = 1
    var repo : Repository = Repository()
    var storage = FirebaseStorage.getInstance()
    var storageRef = storage.getReference()

    private lateinit var uid: String

    private lateinit var nameText: TextView
    private lateinit var backgroundText: TextView
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (intent == null || !intent.hasExtra(UID_TAG)) return
        uid = intent.getStringExtra(UID_TAG)

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

        val photoSelect : Button = findViewById(R.id.btn_photo)

        photoSelect.setOnClickListener {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d(TAG, "onActivityResult")
        if (requestCode == PICK_IMAGE) {
            val uri : Uri = data.getData()
            Log.d(TAG, "selected image: " + uri)
            val ref = storageRef.child("images/" + uid)

            ref.putFile(uri)

            toast("Your picture is stored ")

            val photo: ImageView = findViewById(R.id.profile_photo)
            photo.setImageURI(uri)
        }
    }


}
