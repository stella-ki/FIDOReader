package com.challenge.fidoreader.fagment


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.challenge.fidoreader.MainActivity
import com.challenge.fidoreader.R
import com.challenge.fidoreader.fagment.data.FingerItem
import com.challenge.fidoreader.fidoReader.Authenticator
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FingerEnrollBottomSheetDialog : BottomSheetDialogFragment() {
    val TAG = "FingerEnrollBottomSheetDialog"

    private var mListener: BottomSheetListener? = null
    var imageView: ImageView? = null
    var isCancel = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.v(TAG, "onCreateView")
        val v: View = inflater.inflate(R.layout.dialog_btm_enroll_sheet_layout, container, false)
        imageView = v.findViewById(R.id.imageView2_fp)
        val button1 = v.findViewById<Button>(R.id.btn_enroll_Cancel)
        button1.setOnClickListener {
            isCancel = true
            Log.v("setOnClickListener", "" + isCancel );
            //dismiss();
        }
        dialog!!.setOnShowListener {
            try {
                Log.v("setOnShowListener", isVisible.toString() + "")
                //MainActivity.authenticator.myTag = CardReader.myTag
                val test = EnrollFingerPrintClass()
                test.execute(MainActivity.authenticator)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
            }
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart")
    }

    interface BottomSheetListener {
        fun onUpdateFingerEnrollResult(list: FingerItem?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.v(TAG, "onAttach")
        mListener = try {
            context as BottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement BottomSheetListener")
        }
    }

    inner class EnrollFingerPrintClass : AsyncTask<Any?, Any?, Any?>() {
        val TAG = "EnrollFingerPrintClass"
        var fingerItem: FingerItem? = null

        override fun onPreExecute() {
            super.onPreExecute()
            Log.v(TAG, "onPreExecute")
        }

        override fun onPostExecute(s: Any?) {
            super.onPostExecute(s);
            Log.v(TAG, "onPostExecute")
            mListener!!.onUpdateFingerEnrollResult(fingerItem);
        }

        override fun onProgressUpdate(vararg values: Any?) {
            super.onProgressUpdate(*values)
            Log.v(TAG, "onProgressUpdate : " + values[0].toString())
            imageView!!.setImageResource(R.drawable.authenticator_key)
            imageView!!.setImageResource(R.drawable.fingerprint)
        }

        override fun doInBackground(vararg params: Any?): Any? {
            Log.v(TAG, "doInBackground")
            val authenticator = params[0] as Authenticator
           //var fingerItem: FingerItem? = null
            try {
                authenticator.getPINToken()
                val templateID: String = authenticator.enrollfinger()
                publishProgress(templateID)
                var count = 1
                var tryCount = 0
                while (count != 0 && tryCount != 10) {
                    if (isCancel) {
                        isCancel = false
                        authenticator.enrollCancel()
                        dismiss()
                        break
                    }
                    count = authenticator.enrollNextfinger(templateID)
                    publishProgress(templateID)
                    tryCount++
                }
                fingerItem = FingerItem(templateID, "finger$templateID", 0)
            } catch (e: Exception) {
                fingerItem = null;
                e.printStackTrace()
            }
            return fingerItem
        }
    }
}