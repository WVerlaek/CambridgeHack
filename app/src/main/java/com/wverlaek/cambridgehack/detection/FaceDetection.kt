package com.wverlaek.cambridgehack.detection

import com.microsoft.projectoxford.face.FaceServiceRestClient
import com.microsoft.projectoxford.face.contract.*
import com.microsoft.projectoxford.face.rest.ClientException
import com.wverlaek.cambridgehack.ui.view.Picture
import com.wverlaek.cambridgehack.util.Constants
import com.wverlaek.cambridgehack.util.Listener
import org.jetbrains.anko.doAsync
import java.io.ByteArrayInputStream

/**
 * Created by WVerl on 20-1-2018.
 */
class FaceDetection {

    private val faceClient = FaceServiceRestClient(Constants.MS_API_LOCATION, Constants.MS_API_KEY)

    fun detectFaces(picture: Picture, listener: Listener<List<Face>?>) {
        doAsync {
            try {
                val faces = faceClient.detect(ByteArrayInputStream(picture.jpegData), true, false, arrayOf())
                listener.onComplete(faces.asList())
            } catch (e: ClientException) {
                listener.onComplete(null)
            }
        }
    }

    fun identifyFaces(faces: List<Face>, listener: Listener<List<IdentifyResult>?>) {
        doAsync {
            try {
                faceClient.identity("h_c", faces.map { it.faceId }.toTypedArray(), 10 /*TODO*/)
                listener.onComplete(null)//todo
            } catch (e: ClientException) {
                listener.onComplete(null)
            }
        }
    }


}