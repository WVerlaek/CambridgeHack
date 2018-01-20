package com.wverlaek.cambridgehack.detection

import com.microsoft.projectoxford.face.FaceServiceRestClient
import com.microsoft.projectoxford.face.contract.*
import com.microsoft.projectoxford.face.rest.ClientException
import com.wverlaek.cambridgehack.ui.view.Picture
import com.wverlaek.cambridgehack.util.Constants
import com.wverlaek.cambridgehack.util.Listener
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.error
import org.jetbrains.anko.uiThread
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by WVerl on 20-1-2018.
 */
class FaceDetection : AnkoLogger {

    private val faceClient = FaceServiceRestClient(Constants.MS_API_LOCATION, Constants.MS_API_KEY)

    fun detectFaces(picture: Picture, listener: Listener<List<Face>>) {
        doAsync {
            try {
                val faces = faceClient.detect(ByteArrayInputStream(picture.jpegData), true, false, arrayOf())
                uiThread { listener.onComplete(faces.asList()) }
            } catch (e: ClientException) {
                error("Error in detectFaces", e)
                uiThread { listener.onError() }
            }
        }
    }

    fun identifyFaces(faces: List<Face>, listener: Listener<List<IdentifyResult>>) {
        doAsync {
            try {
                val result = faceClient.identity(Constants.MS_GROUP_ID, faces.map { it.faceId }.toTypedArray(), 1)
                uiThread { listener.onComplete(result.asList()) }//todo
            } catch (e: ClientException) {
                error("Error in identifyFaces", e)
                uiThread { listener.onError() }
            }
        }
    }

    fun createPerson(name: String, listener: Listener<UUID>) {
        doAsync {
            try {
                val newPerson = faceClient.createPerson("c_h", name, null)
                uiThread { listener.onComplete(newPerson.personId) }
            } catch (e: ClientException) {
                uiThread { listener.onError() }
            }
        }
    }

    fun uploadImage(pid: UUID, listener: Listener<Unit?>, img: InputStream) {
        doAsync {
            try {
                faceClient.addPersonFace("c_h", pid, img, null, null)
                uiThread { listener.onComplete(null) }
            } catch (e: ClientException) {
                uiThread { listener.onError() }
            }
        }
    }

    fun getPersons(listener: Listener<List<Person>>) {
        doAsync {
            try {
                val persons = faceClient.getPersons(Constants.MS_GROUP_ID)
                uiThread { listener.onComplete(persons.asList()) }
            } catch (e: ClientException) {
                uiThread { listener.onError() }
            }
        }
    }

    fun getPerson(personId: UUID, listener: Listener<Person>) {
        doAsync {
            try {
                val person = faceClient.getPerson(Constants.MS_GROUP_ID, personId)
                uiThread { listener.onComplete(person) }
            } catch (e: ClientException) {
                uiThread { listener.onError() }
            }
        }
    }

}