package com.wverlaek.cambridgehack.database

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.wverlaek.cambridgehack.util.Listener

/**
 * Created by WVerl on 21-1-2018.
 */
class ImageRepository {

    private val storage = FirebaseStorage.getInstance()
    private val imagesReference = storage.getReference("/images")

    fun getProfileIconUri(profileId: String, listener: Listener<Uri>) {
        imagesReference.child(profileId).downloadUrl.addOnSuccessListener {
            uri -> listener.onComplete(uri)
        }.addOnFailureListener {
            listener.onError()
        }
    }
}