package com.challenge.fidoreader.frag.reader


import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.challenge.fidoreader.MainActivity
import com.challenge.fidoreader.R
import com.challenge.fidoreader.Util.ascii
import com.challenge.fidoreader.fidoReader.data.CBOR_CODE_CTAP1_ERR_SUCCESS
import com.challenge.fidoreader.fidoReader.data.CBOR_CODE_CTAP2_ERR_PIN_INVALID

class InputPINFragment(private val activity: MainActivity, buttonType: String) : DialogFragment() {
    val TAG = "InputPINFragment"
    var mresult: OnDialogResult? = null
    var clientPIN = ""
    lateinit var inputpin_popup: View
    private var password1: EditText? = null
    private var okbtn: Button? = null
    private var cancelbtn: ImageView? = null
    private val buttonType = ""
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(getActivity())
        val inflater = getActivity()!!.layoutInflater
        inputpin_popup = inflater.inflate(R.layout.inputpin_popup, null)
        builder.setView(inputpin_popup)
        return builder.create()
    }

    fun setDialogResult(mresult: OnDialogResult?) {
        this.mresult = mresult
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Dialog Title 없애기
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        inputpin_popup = inflater.inflate(R.layout.inputpin_popup, null)
        dialog!!.setContentView(inputpin_popup)
        password1 = inputpin_popup.findViewById<View>(R.id.userPINEditText) as EditText
        okbtn = inputpin_popup.findViewById<View>(R.id.OKBtn) as Button

        // mProgressBar.setVisibility(View.INVISIBLE);
        okbtn!!.setOnClickListener {
            try {
                clientPIN = password1!!.text.toString().ascii()
                val result: String = MainActivity.authenticator.setUserPIN(clientPIN)

                if (mresult != null) {
                    mresult!!.finish("OK")
                }

                Log.v(TAG, "setUserPIN result : " + result)
                if (result == CBOR_CODE_CTAP2_ERR_PIN_INVALID) {
                    Toast.makeText(activity.applicationContext, "PIN 정보가 올바르지 않습니다.", Toast.LENGTH_LONG).show()
                } else if (result == CBOR_CODE_CTAP1_ERR_SUCCESS) {
                    dialog!!.dismiss()
                } else {
                    Toast.makeText(activity.applicationContext, "List 정보를 읽어올 수 없습니다.", Toast.LENGTH_LONG).show()
                    dialog!!.dismiss()
                }
            } catch (e: Exception) {
                Toast.makeText(activity.applicationContext, "Communication Error", Toast.LENGTH_LONG).show()

                e.printStackTrace()
            }
        }
        cancelbtn = inputpin_popup.findViewById<View>(R.id.CancelBtn) as ImageView
        cancelbtn!!.setOnClickListener {
            if (mresult != null) {
                mresult!!.finish("NO")
            }
            try {
                Toast.makeText(activity.applicationContext, "PIN 입력을 취소하였습니다.", Toast.LENGTH_LONG).show()
                dialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 이미지랑 Layout이랑 안겹치는 부분 배경색 없애기
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()
        return inputpin_popup
    }

    interface OnDialogResult {
        fun finish(result: String?)
    }
}