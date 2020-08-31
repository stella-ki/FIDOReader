package com.challenge.fidoreader.fagment

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.challenge.fidoreader.MainActivity
import com.challenge.fidoreader.R
import com.challenge.fidoreader.Util.ascii
import com.challenge.fidoreader.fidoReader.CBOR_CODE_CTAP1_ERR_SUCCESS
import com.challenge.fidoreader.fidoReader.CBOR_CODE_CTAP2_ERR_PIN_INVALID


class ChangePINFragment(val activity: MainActivity) : DialogFragment() {

/*
    lateinit var mainView: View
    lateinit var originPINText: EditText
    lateinit var password1: EditText
    lateinit var password2: EditText

    lateinit var resultView: TextView

    lateinit var okbtn: Button
    lateinit var cancelbtn: ImageView


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(getActivity())
        val inflater = getActivity()!!.layoutInflater
        mainView = inflater.inflate(R.layout.changepin_popup, null)

        builder.setView(mainView)
        return builder.create()
    }
*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var mainView: View = inflater.inflate(R.layout.changepin_popup, null)

        // Dialog Title 없애기
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(mainView)

        var originPINText = mainView.findViewById<EditText>(R.id.beforePINEditText)
        var password1 = mainView.findViewById<EditText>(R.id.newPINEditText)
        var password2 = mainView.findViewById<EditText>(R.id.confrimPINEditText)

        var resultView = mainView.findViewById<TextView>(R.id.resultSetView)

        password2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (password1.text.toString() == password2.text.toString()) {
                    resultView.setTextColor(Color.parseColor("#00FF00"))
                    resultView.text = "Matched Password"
                } else {
                    resultView.setTextColor(Color.parseColor("#FF0000"))
                    resultView.text = "Not Matched Password"
                }
            }
        })

        var originPIN = ""
        var clientPIN = ""

        var okbtn = mainView.findViewById<Button>(R.id.OKBtn)
        okbtn.setOnClickListener {
            if (originPINText.text.toString().isNotEmpty()) {
                try {
                    originPIN = originPINText.text.toString().ascii()
                    clientPIN = password1.text.toString().ascii()
                    val result = MainActivity.authenticator.changePin(originPIN, clientPIN)
                    when(result){
                        CBOR_CODE_CTAP1_ERR_SUCCESS ->{
                            Toast.makeText(activity.applicationContext, "PIN 변경이 완료되었습니다.", Toast.LENGTH_LONG).show()
                            dialog!!.dismiss()
                        }
                        CBOR_CODE_CTAP2_ERR_PIN_INVALID ->{
                            Toast.makeText(activity.applicationContext, "기존 PIN 정보가 올바르지 않습니다.", Toast.LENGTH_LONG).show()
                        }
                        else ->{
                            Toast.makeText(activity.applicationContext, "PIN 변경을 정상적으로 종료하지 못하였습니다.", Toast.LENGTH_LONG).show()
                            dialog!!.dismiss()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        var cancelbtn = mainView.findViewById<ImageView>(R.id.CancelBtn)
        cancelbtn.setOnClickListener {
            try {
                Toast.makeText(activity.applicationContext, "PIN 변경을 취소하였습니다.", Toast.LENGTH_LONG).show()
                dialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 이미지랑 Layout이랑 안겹치는 부분 배경색 없애기
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()

        return mainView
    }

}
