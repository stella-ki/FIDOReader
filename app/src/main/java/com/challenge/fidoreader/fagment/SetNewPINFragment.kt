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

class SetNewPINFragment(var mainActivity: MainActivity) : DialogFragment() {
    var clientPIN = ""
    lateinit var mainview: View
    lateinit var password1: EditText
    lateinit var password2: EditText

    lateinit var resultView: TextView
    lateinit var okbtn: Button
    lateinit var cancelbtn: ImageView

    override fun onCreateDialog(savedInstanceStage: Bundle?): Dialog{
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        mainview = inflater.inflate(R.layout.setnewpin_popup, null)

        builder.setView(mainview)

        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mainview = inflater.inflate(R.layout.setnewpin_popup, null)
        dialog!!.setContentView(mainview)

        password1 = mainview.findViewById(R.id.newPINEditText)
        password2 = mainview.findViewById(R.id.confrimPINEditText)

        resultView = mainview.findViewById(R.id.resultSetView)

        password2.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (password1.text.toString() == password2.text.toString()) {
                    resultView.setTextColor(Color.parseColor("#00FF00"))
                    resultView.text = "Matched Password"
                } else {
                    resultView.setTextColor(Color.parseColor("#FF0000"))
                    resultView.text = "Not Matched Password"
                }
            }
        })

        okbtn = mainview.findViewById(R.id.OKBtn)
        okbtn.setOnClickListener {
            try {
                clientPIN = Util.ascii(password1.text.toString())
                var result:String = mainActivity.authenticator.setPIN(clientPIN)

                if(result == "00"){
                    Toast.makeText(mainActivity.applicationContext, "PIN 설정이 완료되었습니다.", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(mainActivity.applicationContext, "PIN 설정을 정상적으로 완료하지 못하였습니다..", Toast.LENGTH_LONG).show()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

        }
        cancelbtn = mainview.findViewById(R.id.CancelBtn)
        cancelbtn.setOnClickListener{
            try {
                Toast.makeText(mainActivity.applicationContext, "PIN 설정을 취소하였습니다.", Toast.LENGTH_LONG).show()
                dialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()

        return view
    }





}