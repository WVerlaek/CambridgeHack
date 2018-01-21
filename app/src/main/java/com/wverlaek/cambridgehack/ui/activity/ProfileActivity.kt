package com.wverlaek.cambridgehack.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
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
import com.wverlaek.cambridgehack.util.Listener
import org.jetbrains.anko.custom.onUiThread
import java.util.*
import android.support.annotation.NonNull
import com.firebase.ui.auth.AuthUI




class ProfileActivity : AppCompatActivity() {
    val TAG = "PROFILE"
    val PICK_IMAGE = 1
    var repo : Repository = Repository()
    private lateinit var uid: String
    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.getReference()
    private var mArrayUri = ArrayList<Uri>()


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
                    startActivity(intentFor<SchijndelActivity>())
                    finish()
                } else {
                    loadingFrame.visibility = View.GONE

                    btn_submit.setOnClickListener {
                        FaceDetection().createPerson(last_name_field.text.toString(),
                                object:Listener<UUID> {
                                    override fun onComplete(result: UUID) {
                                        val newProf = Profile();

                                        newProf.uid = uid
                                        newProf.firstName = first_name_field.text.toString()
                                        newProf.lastName = last_name_field.text.toString()
                                        newProf.title = title_field.text.toString()
                                        newProf.organization = organization_field.text.toString()
                                        newProf.facebookName = facebook_field.text.toString()
                                        newProf.githubName = github_field.text.toString()
                                        newProf.linkedInName = linkedIn_field.text.toString()
                                        newProf.personId = result.toString()

                                        repo.updateProfile(newProf)

                                        toast("Created your profile")

                                        val faceDetection = FaceDetection()
                                        for (uri in mArrayUri) {
                                            val inputStream = getContentResolver().openInputStream(uri)
                                            faceDetection.uploadImage(result,
                                                    object:Listener<Unit?>{
                                                        override fun onComplete(result: Unit?) {

                                                        }

                                                        override fun onError() {
                                                            toast("error")
                                                        }

                                                    },
                                                    inputStream)
                                        }

                                        startActivity(intentFor<SchijndelActivity>())
                                        finish()
                                    }

                                    override fun onError() {
                                        toast("Failed to create profile")
                                    }
                                })

                    }

                    btn_photo.setOnClickListener {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        intent.action = Intent.ACTION_GET_CONTENT
                        val chooserIntent = Intent.createChooser(intent, "Select Image")

                        startActivityForResult(chooserIntent, PICK_IMAGE)
                    }
                }
            }

            override fun onError(de: DatabaseError?) {
                toast("Firebase error on retrieving profile $uid:$de")
            }
        })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult")
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            mArrayUri = ArrayList()
            if (data.getData() != null) {
                val uri: Uri = data.getData()
                Log.d(TAG, "selected image: " + uri)
                val ref = storageRef.child("images/" + uid)
                ref.putFile(uri)

                toast("Your picture is stored ")

                mArrayUri.add(uri)
            } else if (data.getClipData() != null) {
                val mClipData = data.clipData

                for (i in 0 until mClipData.itemCount) {
                    val item = mClipData.getItemAt(i)
                    val uri = item.uri
                    mArrayUri.add(uri)
                }
                val ref = storageRef.child("images/" + uid)
                ref.putFile(mArrayUri.first())
            }
            profile_photo.setImageURI(mArrayUri.first())
        }
    }
}
