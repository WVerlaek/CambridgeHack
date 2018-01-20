package com.wverlaek.cambridgehack.ui.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.microsoft.projectoxford.face.contract.Candidate
import com.microsoft.projectoxford.face.contract.Face
import com.microsoft.projectoxford.face.contract.IdentifyResult
import com.wverlaek.cambridgehack.R
import com.wverlaek.cambridgehack.detection.FaceDetection
import com.wverlaek.cambridgehack.ui.view.FaceScanner
import com.wverlaek.cambridgehack.ui.view.Picture
import com.wverlaek.cambridgehack.util.Listener
import kotlinx.android.synthetic.main.activity_face_scan.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.toast

class FaceScanActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var faceScanner: FaceScanner
    private val faceDetection = FaceDetection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_scan)
        faceScanner = FaceScanner(camera)
        fab.setOnClickListener {
            // get picture
            faceScanner.takePicture(object : FaceScanner.OnPictureListener {
                override fun onPicture(picture: Picture?) {
                    picture?.let {
                        detectFaces(it, object : Listener<Map<Face, Candidate?>> {
                            override fun onComplete(result: Map<Face, Candidate?>) {
                                faces_view.setFaces(result.keys.toList())
                            }

                            override fun onError() {
                                faces_view.setFaces(emptyList())
                            }
                        })
                    }
                }
            })
//            Log.d("FaceScanActivity", "analysing")
//            faceScanner.doAnalyse(this)
        }
//        camera_view.addCallback(callback)

        faceScanner.getDetectedFaces().observe(this, Observer {
            val faces = it ?: emptyList()
            faces_view.setFaces(faces)
        })
    }

    private fun detectFaces(picture: Picture, listener: Listener<Map<Face, Candidate?>>) {
        faceDetection.detectFaces(picture, object : Listener<List<Face>> {
            override fun onComplete(result: List<Face>) {
                identifyFaces(result, listener)
            }

            override fun onError() {
                toast("Something went wrong while detecting faces...")
                listener.onError()
            }
        })
    }

    private fun identifyFaces(faces: List<Face>, listener: Listener<Map<Face, Candidate?>>) {
        val listBounded = faces.take(10)
        faceDetection.identifyFaces(listBounded, object : Listener<List<IdentifyResult>> {
            override fun onComplete(result: List<IdentifyResult>) {
                val map = hashMapOf<Face, Candidate?>()
                for (faceResult in result) {
                    val candidates = faceResult.candidates
                    val face = faces.find { it.faceId == faceResult.faceId }
                    face?.let {
                        if (candidates.isEmpty()) {
                            map[face] = null
                        } else {
                            candidates.sortBy { -it.confidence }
                            val best = candidates[0]
                            map[face] = best
                        }
                    }
                }
                listener.onComplete(map)
            }

            override fun onError() {
                toast("Something went wrong while identifying faces...")
                listener.onError()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        faceScanner.resume(this)
    }

    override fun onPause() {
        faceScanner.pause()
        super.onPause()
    }
}
