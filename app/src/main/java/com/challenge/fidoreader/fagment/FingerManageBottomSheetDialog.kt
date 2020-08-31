package com.challenge.fidoreader.fagment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.challenge.fidoreader.R
import com.challenge.fidoreader.fagment.data.FingerItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FingerManageBottomSheetDialog(private val cii: FingerItem) : BottomSheetDialogFragment() {
    private var mListener: BottomSheetListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.dialog_btm_fp_sheet_layout, container, false)
        val textViewID = v.findViewById<TextView>(R.id.txtView_fpinfo)
        textViewID.text = cii.fingerName

        val btnFpName = v.findViewById<Button>(R.id.btn_fp_name);
        val btnFpDelete = v.findViewById<Button>(R.id.btn_fp_delete)
        val btnFpCancel = v.findViewById<Button>(R.id.btn_fp_Cancel)
        btnFpName.setOnClickListener{
            val newNameFragment = InputNameFragment()
            newNameFragment.show(fragmentManager!!, "dialog")
            newNameFragment.isCancelable = true

            newNameFragment.setDialogResult(object : InputNameFragment.OnDialogResult {
                override fun finish(result: String?) {
                    if (result != "NO" && result != "") {
                        mListener!!.onChangeNameBtnClicked(cii, result!!)
                    }
                }
            })
        }
        btnFpDelete.setOnClickListener { mListener!!.onDeleteBtnClicked(cii) }
        btnFpCancel.setOnClickListener { dismiss() }
        return v
    }

    interface BottomSheetListener {
        fun onChangeNameBtnClicked(cii: FingerItem, result: String)
        fun onDeleteBtnClicked(cii: FingerItem)
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