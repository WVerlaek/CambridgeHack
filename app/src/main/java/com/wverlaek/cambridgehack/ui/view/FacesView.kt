package com.wverlaek.cambridgehack.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.microsoft.projectoxford.face.contract.Candidate
import com.microsoft.projectoxford.face.contract.Face
import com.wverlaek.cambridgehack.database.models.Profile
import com.wverlaek.cambridgehack.util.Constants
import org.jetbrains.anko.dip

/**
 * Created by WVerl on 20-1-2018.
 */
class FacesView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var faces = emptyMap<Face, Profile?>()

    private val extraSize = dip(Constants.PROFILE_ICON_EXTRA_SIZE)

    fun setFaces(faces: Map<Face, Profile?>) {
        this.faces = faces


        removeAllViews()
        for ((face, profile) in faces) {
            addView(face, profile)
        }
    }

    private fun addView(face: Face, profile: Profile?) {
        val view = ProfileIconView(context)
        view.setProfile(profile, face)
        view.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            leftMargin = face.faceRectangle.left - extraSize
            topMargin = face.faceRectangle.top - extraSize
        }
        addView(view)
    }

//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        paint.style = Paint.Style.STROKE
//        paint.color = Color.RED
//        paint.textSize = 20f
//        for ((face, candidate) in faces) {
//            val rect = face.faceRectangle
//
//            canvas.drawRect(rect.left.toFloat(),
//                    rect.top.toFloat(),
//                    (rect.left + rect.width).toFloat(),
//                    (rect.top + rect.height).toFloat(),
//                    paint)
//
//            candidate?.let {
//                canvas.drawText(it.personId.toString(), rect.left.toFloat(), rect.top.toFloat(), paint)
//
//            }
//
//            Log.d("FacesView", "${rect.left}, ${rect.top}")
//        }
//    }
}