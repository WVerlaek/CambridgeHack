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
import com.google.firebase.storage.FirebaseStorage
import com.wverlaek.cambridgehack.detection.FaceDetection
import com.wverlaek.cambridgehack.util.Listener
import pl.aprilapps.easyphotopicker.EasyImage
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import pl.aprilapps.easyphotopicker.DefaultCallback
import java.io.File
import java.io.FileInputStream
import kotlin.collections.ArrayList

class ProfileActivity : AppCompatActivity() {
    val TAG = "PROFILE"
    val PICK_IMAGE = 1
    var repo : Repository = Repository()
    private lateinit var uid: String
    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    private var mArrayFiles = ArrayList<File>()

    companion object {
        private val UID_TAG = "UID"

        fun createIntent(context: Context, uid: String): Intent {
            return context.intentFor<ProfileActivity>(UID_TAG to uid)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        EasyImage.configuration(this)
                .setAllowMultiplePickInGallery(true)

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
                        FaceDetection().createPerson(uid,
                                object : Listener<UUID> {
                                    override fun onComplete(result: UUID) {
                                        val newProf = Profile()
                                        val user = FirebaseAuth.getInstance().currentUser

                                        newProf.uid = uid
                                        newProf.title = title_field.text.toString()
                                        newProf.organization = organization_field.text.toString()
                                        newProf.facebookName = facebook_field.text.toString()
                                        newProf.githubName = github_field.text.toString()
                                        newProf.linkedInName = linkedIn_field.text.toString()
                                        newProf.personId = result.toString()
                                        newProf.email = user?.email ?: ""
                                        newProf.displayName = user?.displayName ?: ""

                                        repo.updateProfile(newProf)

                                        toast("Created your profile")

                                        val faceDetection = FaceDetection()
                                        uploadImages(faceDetection, result, mArrayFiles, object : Listener<Unit> {
                                            override fun onComplete(result: Unit) {
                                                startActivity(intentFor<SchijndelActivity>())
                                                finish()
                                            }

                                            override fun onError() {
                                            }

                                        })
                                    }

                                    override fun onError() {
                                        toast("Failed to create profile")
                                    }
                                })
                    }

                    btn_photo.setOnClickListener {
                        EasyImage.openChooserWithGallery(this@ProfileActivity, "Choose your pictures", PICK_IMAGE)
                    }
                }
            }

            override fun onError(de: DatabaseError?) {
                toast("Firebase error on retrieving profile $uid:$de")
            }
        })
    }

    private fun uploadImages(faceDetection: FaceDetection, personId: UUID, images: List<File>, listener: Listener<Unit>, curIndex: Int = 0) {
        if (curIndex >= images.size) {
            listener.onComplete(Unit)
        } else {
            val image = images[curIndex]
            val inputStream = FileInputStream(image)
            faceDetection.uploadImage(personId,
                    object : Listener<Unit?> {
                        override fun onComplete(result: Unit?) {
                            uploadImages(faceDetection, personId, images, listener, curIndex + 1)
                        }

                        override fun onError() {
                            uploadImages(faceDetection, personId, images, listener, curIndex + 1)
                        }

                    },
                    inputStream)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "onActivityResult")

        EasyImage.handleActivityResult(requestCode, resultCode, data, this,
                object : DefaultCallback() {
            override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
                //Some error handling
                e!!.printStackTrace()
            }

            override fun onImagesPicked(imageFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                Log.d(TAG, "images " + imageFiles.size)
                mArrayFiles = ArrayList(imageFiles)
                val firstUri = android.net.Uri.parse(mArrayFiles.first().toURI().toString())
                val ref = storageRef.child("images/" + uid)
                ref.putFile(firstUri)
                profile_photo.setImageURI(firstUri)
            }

            override fun onCanceled(source: EasyImage.ImageSource?, type: Int) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    val photoFile = EasyImage.lastlyTakenButCanceledPhoto(this@ProfileActivity)
                    photoFile?.delete()
                }
            }
        })
    }
}
