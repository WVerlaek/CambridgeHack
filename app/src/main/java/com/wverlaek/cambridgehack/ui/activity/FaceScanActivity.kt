package com.wverlaek.cambridgehack.ui.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.microsoft.projectoxford.face.contract.Candidate
import com.microsoft.projectoxford.face.contract.Face
import com.microsoft.projectoxford.face.contract.IdentifyResult
import com.wverlaek.cambridgehack.R
import com.wverlaek.cambridgehack.database.Repository
import com.wverlaek.cambridgehack.database.models.Profile
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
    private val repo = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_scan)
        faceScanner = FaceScanner(camera)
        fab.setOnClickListener {
            setFabEnabled(false)
            loading_overlay.visibility = View.VISIBLE

            // get picture
            faceScanner.takePicture(object : FaceScanner.OnPictureListener {
                override fun onPicture(picture: Picture?) {
                    picture?.let {


                        detectFaces(it, object : Listener<Map<Face, Profile?>> {
                            override fun onComplete(result: Map<Face, Profile?>) {
                                if (result.isEmpty()) {
                                    onError()
                                } else {
                                    loading_overlay.visibility = View.GONE
                                    faces_view.setFaces(result)
                                    snapshot.setPicture(it)

                                    setFabEnabled(true)
                                    close_app.show()
                                }
                            }

                            override fun onError() {
                                toast("No profiles detected")
                                faces_view.setFaces(emptyMap())
                                loading_overlay.visibility = View.GONE
                                snapshot.setPicture(null)

                                setFabEnabled(true)
                                close_app.hide()
                            }
                        })
                    }
                }
            })
//            Log.d("FaceScanActivity", "analysing")
//            faceScanner.doAnalyse(this)
        }

        close_app.setOnClickListener {
            close_app.hide()
            faces_view.setFaces(emptyMap())
            snapshot.setPicture(null)
        }
//        camera_view.addCallback(callback)
    }

    private fun setFabEnabled(enabled: Boolean) {
        fab.isEnabled = enabled
        fab.setImageResource(if (enabled) R.drawable.ic_search_black_24dp else R.drawable.ic_hourglass_full_black_24dp)
    }

    private fun detectFaces(picture: Picture, listener: Listener<Map<Face, Profile?>>) {
        faceDetection.detectFaces(picture, object : Listener<List<Face>> {
            override fun onComplete(result: List<Face>) {
                identifyFaces(result, listener)
            }

            override fun onError() {
//                toast("Something went wrong while detecting faces...")
                listener.onError()
            }
        })
    }

    private fun identifyFaces(faces: List<Face>, listener: Listener<Map<Face, Profile?>>) {
        val listBounded = faces.take(10)
        faceDetection.identifyFaces(listBounded, object : Listener<List<IdentifyResult>> {
            override fun onComplete(result: List<IdentifyResult>) {
                val map = hashMapOf<Face, Profile?>()

                val bestCandidates = result.map {
                    val candidates = it.candidates
                    if (candidates.isEmpty()) {
                        null
                    } else {
                        candidates.sortBy { -it.confidence }
                        candidates[0]
                    }
                }

                val candidatesForFaces = faces.map {
                    val face = it
                    val candidateIdx = result.indexOfFirst { it.faceId == face.faceId }

                    bestCandidates[candidateIdx]
                }

                val personIds = candidatesForFaces.map { it?.personId }

                repo.getProfilesByPersonIds(personIds, object : Listener<List<Profile?>> {
                    override fun onComplete(result: List<Profile?>) {
                        for (i in 0 until faces.size) {
                            map[faces[i]] = result[i]
                        }

                        listener.onComplete(map)
                    }

                    override fun onError() {
                        listener.onError()
                    }
                })
            }

            override fun onError() {
//                toast("Something went wrong while identifying faces...")
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
