package com.challenge.fidoreader.fagment

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.challenge.fidoreader.R

class CredItemFragment : LinearLayout{

    var textview: TextView
    var textview2: TextView
    var textview3: TextView
    var imageView: ImageView


    constructor(context: Context) : super(context) {
        var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.fragment_item_list, this, true)
        textview = findViewById(R.id.textView)
        textview2 = findViewById(R.id.textView2)
        textview3 = findViewById(R.id.textView3)
        imageView = findViewById(R.id.imageView)
    }

    constructor(context: Context, attrs:AttributeSet): super(context, attrs){
        var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.fragment_item_list, this, true)
        textview = findViewById(R.id.textView)
        textview2 = findViewById(R.id.textView2)
        textview3 = findViewById(R.id.textView3)
        imageView = findViewById(R.id.imageView)
    }

    fun setRP(rp: String){
        textview.text = rp
    }

    fun setName(name: String){
        textview2.text = name
    }

    fun setKeyvalue(keyvalue: String){
        textview3.text = keyvalue
    }

    fun setImage(resid: Int){
        imageView.setImageResource(resid)
    }


}