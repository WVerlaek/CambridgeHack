package com.wverlaek.cambridgehack.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.database.DatabaseError
import com.microsoft.projectoxford.face.contract.Candidate
import com.microsoft.projectoxford.face.contract.Face
import com.wverlaek.cambridgehack.R
import com.wverlaek.cambridgehack.database.ProfileListener
import com.wverlaek.cambridgehack.database.Repository
import com.wverlaek.cambridgehack.database.models.Profile
import com.wverlaek.cambridgehack.ui.activity.ProfileActivity
import com.wverlaek.cambridgehack.ui.activity.ShowProfileActivity
import com.wverlaek.cambridgehack.util.Constants
import kotlinx.android.synthetic.main.profile_icon_view.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * Created by WVerl on 20-1-2018.
 */
class ProfileIconView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val root: View = LayoutInflater.from(context)
            .inflate(R.layout.profile_icon_view, this, true)
    private val boxView: View
    private val nameText: TextView

    private var profile: Profile? = null

    private val extraSize = dip(Constants.PROFILE_ICON_EXTRA_SIZE)

    init {
        boxView = root.bounding_box
        nameText = root.name

        root.root.setOnClickListener {
            profile?.let {
                context.startActivity(ShowProfileActivity.createIntent(context, it.uid))
            }
            if (profile == null) {
                context.toast("Unknown user!")
            }
        }
    }

    fun setProfile(profile: Profile?, face: Face) {
        this.profile = profile

        val rect = face.faceRectangle
        boxView.layoutParams = ConstraintLayout.LayoutParams(rect.width + 2 * extraSize, rect.height + 2 * extraSize)

        nameText.text = profile?.run { displayName } ?: "Anonymous"

        invalidate()
    }
}