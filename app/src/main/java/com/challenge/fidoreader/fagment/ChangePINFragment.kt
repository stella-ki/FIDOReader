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
import com.challenge.fidoreader.Util.Util


class ChangePINFragment(val activity: MainActivity) : DialogFragment() {
    var originPIN = ""
    var clientPIN = ""

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Dialog Title 없애기
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mainView = inflater.inflate(R.layout.changepin_popup, null)
        dialog!!.setContentView(mainView)

        originPINText = mainView.findViewById(R.id.beforePINEditText)
        password1 = mainView.findViewById(R.id.newPINEditText)
        password2 = mainView.findViewById(R.id.confrimPINEditText)

        resultView = mainView.findViewById(R.id.resultSetView)

        password2.addTextChangedListener(object : TextWatcher {
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

            override fun afterTextChanged(s: Editable) {}
        })
        okbtn = mainView.findViewById(R.id.OKBtn)
        okbtn.setOnClickListener {
            if (originPINText.text.toString().isNotEmpty()) {
                try {
                    originPIN = Util.ascii(originPINText.text.toString())
                    clientPIN = Util.ascii(password1.text.toString())
                    val result = activity.authenticator.changePin(originPIN, clientPIN)
                    when(result){
                        "00" ->{
                            Toast.makeText(activity.applicationContext, "PIN 변경이 완료되었습니다.", Toast.LENGTH_LONG).show()
                            dialog!!.dismiss()
                        }
                        "31" ->{
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

        cancelbtn = mainView.findViewById<View>(R.id.CancelBtn) as ImageView
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
