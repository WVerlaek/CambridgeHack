package com.wverlaek.cambridgehack.detection

import com.microsoft.projectoxford.face.FaceServiceRestClient
import com.microsoft.projectoxford.face.contract.*
import com.microsoft.projectoxford.face.rest.ClientException
import com.wverlaek.cambridgehack.ui.view.Picture
import com.wverlaek.cambridgehack.util.Constants
import com.wverlaek.cambridgehack.util.Listener
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

/**
 * Created by WVerl on 20-1-2018.
 */
class FaceDetection {

    private val faceClient = FaceServiceRestClient(Constants.MS_API_LOCATION, Constants.MS_API_KEY)

    fun detectFaces(picture: Picture, listener: Listener<List<Face>>) {
        doAsync {
            try {
                val faces = faceClient.detect(ByteArrayInputStream(picture.jpegData), true, false, arrayOf())

                uiThread { listener.onComplete(faces.asList()) }
            } catch (e: ClientException) {
                uiThread { listener.onError() }
            }
        }
    }

    fun identifyFaces(faces: List<Face>, listener: Listener<List<IdentifyResult>>) {
        doAsync {
            try {
                faceClient.identity("h_c", faces.map { it.faceId }.toTypedArray(), 10 /*TODO*/)
                uiThread { listener.onComplete(emptyList()) }//todo
            } catch (e: ClientException) {
                uiThread { listener.onError() }
            }
        }
    }

    fun createPerson(name: String, listener: Listener<UUID>) {
        doAsync {
            try {
                val newPerson = faceClient.createPerson("h_c", name, null)
                uiThread { listener.onComplete(newPerson.personId) }
            } catch (e: ClientException) {
                uiThread { listener.onError() }
            }
        }
    }

    fun uploadImage(uuid: UUID, listener: Listener<Unit?>, img: InputStream) {
        doAsync {
            try {
                faceClient.addPersonFace("h_c", uuid, img, null, null)
                uiThread { listener.onComplete(null) }
            } catch (e: ClientException) {
                uiThread { listener.onError() }
            }
        }
    }
}