package com.challenge.fidoreader.fagment


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.challenge.fidoreader.R


class FingerItemFragment : LinearLayout {
    var textview: TextView? = null
    var imageView: ImageView? = null

    constructor(context: Context) : super(context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.fragment_finger_list, this, true)
        textview = findViewById(R.id.textViewfp)
        imageView = findViewById(R.id.imageViewfp)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun setName(name: String?) {
        textview!!.text = name
    }

    fun setImage(resid: Int) {
        imageView!!.setImageResource(resid)
    }
}
