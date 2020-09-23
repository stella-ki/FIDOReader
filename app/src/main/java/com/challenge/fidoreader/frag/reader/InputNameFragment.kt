package com.challenge.fidoreader.frag.reader


import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.challenge.fidoreader.R
import java.util.regex.Pattern


class InputNameFragment : DialogFragment() {
    var mresult: OnDialogResult? = null

    // private MainActivity activity;
    lateinit var inputname_popup:View
    private var password1: EditText? = null
    private var okbtn: Button? = null
    private var cancelbtn: ImageView? = null

    var filterAlphaNum = InputFilter { source, start, end, dest, dstart, dend ->
        val ps = Pattern.compile("^[a-zA-Z0-9]+$")
        if (source == "" || ps.matcher(source).matches() || source.length < 9) {
            return@InputFilter source
        }
        Log.v("test1 : ", start.toString() + "")
        Log.v("test2 : ", end.toString() + "")
        Log.v("test3 : ", dstart.toString() + "")
        Log.v("test4 : ", dend.toString() + "")
        Toast.makeText(activity, "영문만 입력 가능합니다. ", Toast.LENGTH_SHORT).show()
        source.subSequence(start, dend)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        inputname_popup = inflater.inflate(R.layout.inputname_popup, null)
        builder.setView(inputname_popup)
        return builder.create()
    }

    fun setDialogResult(mresult: OnDialogResult?) {
        this.mresult = mresult
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Dialog Title 없애기
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        inputname_popup = inflater.inflate(R.layout.inputname_popup, null)
        dialog!!.setContentView(inputname_popup)
        password1 = inputname_popup.findViewById<View>(R.id.newFingerName) as EditText
        password1!!.filters = arrayOf(
                filterAlphaNum,
                LengthFilter(10))
        password1!!.privateImeOptions = "defaultInputmode=english;"
        okbtn = inputname_popup.findViewById<View>(R.id.OKBtn) as Button

        // mProgressBar.setVisibility(View.INVISIBLE);
        okbtn!!.setOnClickListener {
            try {
                if (mresult != null) {
                    mresult!!.finish(password1!!.text.toString())
                }
                dialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        cancelbtn = inputname_popup.findViewById<View>(R.id.CancelBtn) as ImageView
        cancelbtn!!.setOnClickListener {
            if (mresult != null) {
                mresult!!.finish("NO")
            }
            try {
                //Toast.makeText(activity.getApplicationContext(), "PIN 입력을 취소하였습니다.", Toast.LENGTH_LONG).show();
                dialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 이미지랑 Layout이랑 안겹치는 부분 배경색 없애기
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()
        return inputname_popup
    }

    interface OnDialogResult {
        fun finish(result: String?)
    }
}