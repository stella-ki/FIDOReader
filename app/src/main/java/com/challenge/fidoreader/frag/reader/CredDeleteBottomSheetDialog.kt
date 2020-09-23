package com.challenge.fidoreader.frag.reader

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.challenge.fidoreader.R
import com.challenge.fidoreader.frag.data.CredentialItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CredDeleteBottomSheetDialog(var cii: CredentialItem): BottomSheetDialogFragment() {
    private var mListener: BottomSheetListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainView: View = inflater.inflate(R.layout.dialog_bottom_sheet_layout, container, false)
        val textViewID = mainView.findViewById<TextView>(R.id.txtView_CredInfo_id)
        val textViewKey = mainView.findViewById<TextView>(R.id.txtView_CredInfo_key)

        textViewID.text = cii.rpid
        textViewKey.text = cii.credential_id

        mainView.findViewById<Button>(R.id.btn_cred_delete).setOnClickListener { mListener!!.onDeleteButtonClicked(cii) }
        mainView.findViewById<Button>(R.id.btn_cred_Cancel).setOnClickListener {
            mListener!!.onDeleteButtonClicked(null)
            dismiss()
        }
        return mainView
    }

    interface BottomSheetListener {
        fun onDeleteButtonClicked(cii: CredentialItem?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as BottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement BottomSheetListener")
        }
    }
}