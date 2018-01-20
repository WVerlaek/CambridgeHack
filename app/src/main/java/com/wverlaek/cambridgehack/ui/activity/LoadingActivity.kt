package com.wverlaek.cambridgehack.ui.activity

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.wverlaek.cambridgehack.R
import com.wverlaek.cambridgehack.util.Permissions
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class LoadingActivity : AppCompatActivity() {

    private val REQUEST_CAMERA = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // request camera permission
        if (!Permissions.hasCameraPermission(this)) {
            Permissions.requestCameraPermission(this, REQUEST_CAMERA)
        } else {
            onPermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // close app
                toast("Camera permission required")
                finish()
            } else {
                onPermissionGranted()
            }
        }
    }

    private fun onPermissionGranted() {
        startActivity(intentFor<MainActivity>())
        finish()
    }
}
