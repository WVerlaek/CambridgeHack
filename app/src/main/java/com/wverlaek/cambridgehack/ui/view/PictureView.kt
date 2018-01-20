package com.wverlaek.cambridgehack.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.microsoft.projectoxford.face.contract.Candidate
import com.microsoft.projectoxford.face.contract.Face

/**
 * Created by WVerl on 20-1-2018.
 */
class PictureView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private var picture: Picture? = null

    fun setPicture(picture: Picture?) {
        this.picture = picture
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        picture?.let {
            canvas.drawBitmap(it.bitmap, 0f, 0f, paint)
        }
    }
}