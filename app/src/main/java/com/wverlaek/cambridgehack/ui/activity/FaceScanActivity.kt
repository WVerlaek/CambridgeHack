package com.wverlaek.cambridgehack.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.wverlaek.cambridgehack.R
import kotlinx.android.synthetic.main.activity_face_scan.*

class FaceScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_scan)
    }

    override fun onResume() {
        super.onResume()
        camera_view.start()
    }

    override fun onPause() {
        camera_view.stop()
        super.onPause()
    }
}
