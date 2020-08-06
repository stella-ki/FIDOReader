package com.challenge.fidoreader.fagment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.challenge.fidoreader.MainActivity
import com.challenge.fidoreader.R
import java.lang.Exception

class ReaderButtonFragment : Fragment(){
    val TAG = "ReaderButtonFragment"

    lateinit var imageView: ImageView
    lateinit var textView2: TextView
    lateinit var textView3: TextView

    lateinit var btn:Button
    lateinit var getinfoBtn:Button
    lateinit var clientPinBtn:Button

    var hasClientPIN = false

    lateinit var mainActivity:MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.v(TAG, "onCreateView")

        var mainView: View = inflater.inflate(R.layout.fragment_reader_button, container, false)

        btn = mainView.findViewById(R.id.readerActivationBtn)
        btn.setOnClickListener{
            mainActivity.onChangeFragmentToList()
        }

        btn.isEnabled = false

        getinfoBtn = mainView.findViewById(R.id.KonaBIOPASSGetInfoBtn)
        clientPinBtn = mainView.findViewById(R.id.KonaBIOPASSPINBtn)
        clientPinBtn.isEnabled = false

        getinfoBtn.setOnClickListener{
            try {
                initTable(mainView)
                if(hasClientPIN){
                    clientPinBtn.text = "Change PIN"
                }else{
                    clientPinBtn.text = "Set PIN"
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        getinfoBtn.isEnabled = false

        clientPinBtn.setOnClickListener {
            var pinFragment:DialogFragment? = null
        }


        return mainView
    }

    fun initTable(view: View){
        var getInfoTable: TableLayout = view.findViewById(R.id.KonaBIOPASSGetInfoTable)
        var row:TableRow = TableRow(activity)
        var layoutParams:TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
        row.layoutParams = layoutParams

        var funtionRow: TextView = TextView(activity)
        funtionRow.gravity = Gravity.CENTER
        funtionRow.text = "기능"
        row.addView(funtionRow)

        var supported:TextView = TextView(activity)
        supported.gravity = Gravity.CENTER
        supported.text = "값"
        row.addView(supported)

        getInfoTable.addView(row)

        getInfoPrint(getInfoTable)
    }

    fun getInfoPrint(getInfoTable: TableLayout) {

    }


}



