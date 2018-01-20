package com.wverlaek.cambridgehack.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.microsoft.projectoxford.face.contract.Face

/**
 * Created by WVerl on 20-1-2018.
 */
class FacesView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var faces = emptyList<Face>()
    private val paint = Paint()

    fun setFaces(faces: List<Face>) {
        this.faces = faces
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.textSize = 20f
        for (face in faces) {
            val rect = face.faceRectangle

            canvas.drawRect(rect.left.toFloat(),
                    rect.top.toFloat(),
                    (rect.left + rect.width).toFloat(),
                    (rect.top + rect.height).toFloat(),
                    paint)
            canvas.drawText(face.faceId.toString(), rect.left.toFloat(), rect.top.toFloat(), paint)

            Log.d("FacesView", "${rect.left}, ${rect.top}")
        }
    }
}