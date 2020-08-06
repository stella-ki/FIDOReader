package com.challenge.fidoreader.fagment

import android.content.Context
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
import com.challenge.fidoreader.Util.Util
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import java.io.ByteArrayInputStream
import java.util.*

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

            if(clientPinBtn.text.toString()=="Set New PIN"){
                pinFragment = SetNewPINFragment(mainActivity)
            }else if(clientPinBtn.text.toString()=="Change PIN"){
                pinFragment = ChangePINFragment(mainActivity)
            }

            pinFragment!!.show(fragmentManager!!, "dialog")
            pinFragment.isCancelable = false

            try {
                mainActivity.authenticator.setTag(mainActivity.cardReader.myTag)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            imageView = mainView.findViewById(R.id.readerActivationImageView)
            textView2 = mainView.findViewById(R.id.readerActivationText2)
            textView3 = mainView.findViewById(R.id.readerActivationText3)

            setResult(mainActivity.cardReader.result_image,
                    mainActivity.cardReader.result1_str,
                    mainActivity.cardReader.result2_str,
                    mainActivity.cardReader.result)


        }


        return mainView
    }

    fun setResult(resource:Int, str1: String, str2: String){
        imageView.setImageResource(resource)
        textView2.text = str1
        textView3.text = str2
    }

    fun setResult(resource:Int, str1: String, str2: String, result:Boolean){
        setResult(resource, str1, str2)
        if(result){
            setEnable()
        }
    }

    fun setEnable(){
        Log.v(TAG, "SetEnable")
        btn.isEnabled = true

        getInfo()
        getinfoBtn.isEnabled = true
        clientPinBtn.isEnabled = true
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

    fun getInfo():Map<String,Object>{
        var result = ""

        mainActivity.authenticator.setTag(mainActivity.cardReader.myTag)
        result = mainActivity.authenticator.getInfo()

        var bais = ByteArrayInputStream(Util.atohex(result))

        var cf = CBORFactory()
        var mapper = ObjectMapper(cf)

        var cborParser = cf.createParser(bais)

        var responseMap = mapper.readValue<Map<String, Object>>(cborParser, object : TypeReference<Map<String, Object>>() {})

        for(key in responseMap.keys){
            if(key == "4"){
                var options = responseMap.get(key) as LinkedHashMap<String, Boolean>
                clientPinBtn.text = if(options.get("clientPin")!!){
                     "Change PIN"
                }else{
                    "Set New PIN"
                }
            }
        }

        return responseMap
    }

    fun getInfoPrint(getInfoTable: TableLayout) {
        try {

            val responseMap: Map<String, Object> = getInfo()
            var result = ""

            var version: ArrayList<String>
            var extensions: ArrayList<String>
            var aaguid: ByteArray
            var options: LinkedHashMap<String, Boolean>
            var pinUvAuthProtocol: ArrayList<Int>

            for (key in responseMap.keys) {

                val tableRow = TableRow(activity)
                val functionRow = TextView(activity)
                val valuewRow = TextView(activity)

                valuewRow.width = 600
                when (key) {
                    "1" -> {
                        version = responseMap[key] as ArrayList<String>
                        // resultData += "Version : \n";
                        functionRow.text = "Version"
                        result = ""
                        for(value in version){
                            result += value + "\n"
                        }
                    }
                    "2" -> {
                        extensions = responseMap[key] as ArrayList<String>
                        // resultData += "Extensions : \n";
                        functionRow.text = "Exnesions"
                        result = ""
                        for(value in extensions){
                            result += value + "\n"
                        }
                    }
                    "3" -> {
                        aaguid = responseMap[key] as ByteArray
                        // resultData += "AAGUID : \n";
                        // resultData += "\t[" + Util.getHexString(aaguid) + "]\n";
                        functionRow.text = "AAGUID"
                        result = Util.getHexString(aaguid)
                    }
                    "4" -> {
                        options = responseMap[key] as LinkedHashMap<String, Boolean>
                        // resultData += "Opionts : \n";
                        functionRow.text = "Options"
                        result = ""
                        result += if (options["rk"]!!) {
                            // resultData += "\t[Resident Key] : [지원]\n";
                            "Resident Key 지원\n"
                        } else {
                            // resultData += "\t[Resident Key] : [미지원]\n";
                            "Resident Key 미지원\n"
                        }
                        result += if (options["up"]!!) {
                            // resultData += "\t[User Presence] : [지원]\n";
                            "User Presence 지원\n"
                        } else {
                            // resultData += "\t[User Presence] : [미지원]\n";
                            "User Presence 미지원\n"
                        }
                        result += if (options["uv"]!!) {
                            // resultData += "\t[FingerPrint] : [사용 가능]\n";
                            "FingerPrint\n:[사용 가능]\n"
                        } else {
                            // resultData += "\t[FingerPrint] : [미지원 or 지문 미등록]\n";
                            "FingerPrint\n:[미지원 or 지문 미등록]\n"
                        }
                        result += if (!options["plat"]!!) {
                            // resultData += "\t[no Platform Device]\n";
                            "no Platform Device\n"
                        } else {
                            // resultData += "\t[Platform Device]\n";
                            "Platform Device\n"
                        }
                        if (options["clientPin"]!!) {
                            // resultData += "\t[사용자 PIN] : [사용 가능]\n";
                            result += "사용자 PIN\n:[사용 가능]\n"
                            hasClientPIN = true
                        } else {
                            // resultData += "\t[사용자 PIN] : [미지원 or 사용자 PIN 미등록]\n";
                            result += "사용자 PIN\n:[미지원 or 사용자 PIN 미등록]\n"
                        }
                        result += if (options["credentialMgmtPreview"]!!) {
                            // resultData += "\t[Credential Management] : [지원]\n";
                            "Credential Management 지원\n"
                        } else {
                            // resultData += "\t[Credential Management] : [미지원]\n";
                            "Credential Management 미지원\n"
                        }
                        result += if (!options["userVerificationMgmtPreview"]!!) {
                            // resultData += "\t[FingerPrint Management] : [미지원 or 지문 미등록]\n";
                            "FingerPrint Management\n:[미지원 or 지문 미등록]"
                        } else {
                            // resultData += "\t[FingerPrint Management : [지원]\n";
                            "FingerPrint Management\n:[지원]"
                        }
                    }
                    "6" -> {
                        pinUvAuthProtocol = responseMap[key] as ArrayList<Int>
                        // resultData += "PinUvAuthProtocol : \n";
                        functionRow.text = "PinUvAuthProtocol"
                        result = ""
                        for(value in pinUvAuthProtocol){
                            result += value.toString() + "\n"
                        }
                    }
                    "7" -> {
                        // resultData += "지원가능한 Credential 수 : \n";
                        functionRow.text = "지원 가능한 Credential 수"

                        // resultData += "\t[" + (Integer) responseMap.get(key) + " bytes]\n";
                        result = (responseMap[key] as Int).toString()
                    }
                    "8" -> {
                        // resultData += "CredentialID 길이 : \n";
                        functionRow.text = "CredentialID 길이"
                        // resultData += "\t[" + (Integer) responseMap.get(key) + "]\n";
                        result = (responseMap[key] as Int).toString()
                    }
                }
                tableRow.addView(functionRow)

                valuewRow.text = result
                tableRow.addView(valuewRow)
                getInfoTable.addView(tableRow)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")
        setResult(mainActivity.cardReader.result_image, mainActivity.cardReader.result1_str, mainActivity.cardReader.result2_str)
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause")
    }


    override fun onStop() {
        super.onStop()
        Log.v(TAG, "onStop")
    }


    override fun onAttach(context: Context) {
        super.onAttach(context!!)
        mainActivity = (activity as MainActivity?)!!
    }

    override fun onDetach() {
        super.onDetach()
        //mainActivity = null
        Log.v(TAG, "onDetach")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.v(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }


}



