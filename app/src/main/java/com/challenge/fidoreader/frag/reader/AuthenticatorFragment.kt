package com.challenge.fidoreader.frag.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.challenge.fidoreader.R

class AuthenticatorFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainview = inflater.inflate(R.layout.fragment_authenticator, container, false)
        val btn = mainview.findViewById<View>(R.id.readerActivationBtn) as Button
        btn.setOnClickListener {
            //TODO
        }
        return view
    }
}