package com.wverlaek.cambridgehack.ui.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import com.microsoft.projectoxford.face.FaceServiceRestClient
import com.microsoft.projectoxford.face.contract.Face
import com.microsoft.projectoxford.face.rest.ClientException
import com.wverlaek.cambridgehack.database.models.Profile
import com.wverlaek.cambridgehack.util.Constants
import org.jetbrains.anko.*
import java.io.ByteArrayInputStream
import java.util.*

/**
 * Created by WVerl on 20-1-2018.
 */
class FaceScanner(val container: ViewGroup) : AnkoLogger {
    private val TAG = "FaceScanner"

    private var camera: Camera? = null
    private var preview: CameraPreview? = null

    private val handler = Handler(Looper.getMainLooper())

    private val mutableFaces = MutableLiveData<List<Face>>()
    private val mutableError = MutableLiveData<Throwable>()


    private var timer: Timer? = null
    private val POLL_RATE = 1500L

    private var takingPicture = false

    private val faceClient = FaceServiceRestClient(Constants.MS_API_LOCATION, Constants.MS_API_KEY)

    init {
        mutableFaces.value = null
        mutableError.value = null
    }

    fun pause() {
        camera?.apply {
            setPreviewCallback(null)
            release()
        }
        camera = null

        timer?.cancel()
        timer = null
    }

    fun resume(context: Context) {
        camera = getCameraInstance()

        preview = CameraPreview(context, camera)
        container.removeAllViews()
        container.addView(preview)

        timer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    doAnalyse(context)
                }
            }, 500L, POLL_RATE)
        }
    }

    fun getDetectedFaces(): LiveData<List<Face>> = mutableFaces
    fun getError(): LiveData<Throwable> = mutableError

    fun doAnalyse(context: Context) {
        takePicture(object : OnPictureListener {
            override fun onPicture(picture: Picture?) {
                picture?.let {
                    doAsync {
                        try {
                            val result = faceClient.detect(ByteArrayInputStream(it.jpegData), true, false, arrayOf())
                            Log.d(TAG, "Detected ${result.size} faces.")
                            mutableFaces.postValue(result.asList())
                            mutableError.postValue(null)
                        } catch (e: ClientException) {
                            mutableError.postValue(e)
                        }
                    }

                }
            }
        })
    }

    private fun takePicture(listener: OnPictureListener) {
        if (!takingPicture) {
            takingPicture = true
            val picture = preview?.previewPicture
            takingPicture = false

            listener.onPicture(picture)
        }
    }

    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open().apply { setDisplayOrientation(90) }
        } catch (e: Exception) {
            error("Error loading camera.", e)
            null
        }
    }

    @FunctionalInterface
    interface OnPictureListener {
        fun onPicture(picture: Picture?)
    }
}