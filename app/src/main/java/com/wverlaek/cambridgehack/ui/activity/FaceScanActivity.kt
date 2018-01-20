package com.wverlaek.cambridgehack.ui.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.wverlaek.cambridgehack.R
import com.wverlaek.cambridgehack.ui.view.FaceScanner
import kotlinx.android.synthetic.main.activity_face_scan.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug

class FaceScanActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var faceScanner: FaceScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_scan)
        faceScanner = FaceScanner(camera)
        fab.setOnClickListener {
//            Log.d("FaceScanActivity", "analysing")
//            faceScanner.doAnalyse(this)
        }
//        camera_view.addCallback(callback)

        faceScanner.getDetectedFaces().observe(this, Observer {
            val faces = it ?: emptyList()
            faces_view.setFaces(faces)
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
