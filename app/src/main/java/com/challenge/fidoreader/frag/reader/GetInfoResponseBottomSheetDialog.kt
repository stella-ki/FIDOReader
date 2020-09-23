package com.challenge.fidoreader.frag.reader


import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.challenge.fidoreader.MainActivity
import com.challenge.fidoreader.R
import com.challenge.fidoreader.Util.getHexString
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


class GetInfoResponseBottomSheetDialog(var clientpinbtn: Button, var mainActivity: MainActivity, var getInfoResponse: Map<String, Any>) : BottomSheetDialogFragment() {
    private val cancelbtn: ImageView? = null
    private var hasclientPIN = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.dialog_btm_getinfo_sheet_layout, container, false)
        try {
            initTable(view)
            if (hasclientPIN) {
                clientpinbtn.text = "Change PIN"
            } else {
                clientpinbtn.text = "Set PIN"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        cancelbtn = (ImageView) view.findViewById(R.id.CancelBtn);
//        cancelbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try{
//                    getDialog().dismiss();
//
//                } catch (Exception e){
//                    e.printStackTrace();;
//                }
//            }
//        });

        // 이미지랑 Layout이랑 안겹치는 부분 배경색 없애기
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getDialog().show();
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement BottomSheetListener")
        }
    }

    @Throws(Exception::class)
    private fun initTable(view: View) {
        val getInfoTable = view.findViewById<View>(R.id.KonaBIOPASSGetInfoTable) as TableLayout

//        TableRow row = (TableRow)view.findViewById(R.id.konaBIOPASSGetInfoRow);
        val row = TableRow(activity)
        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
        row.layoutParams = layoutParams
        row.setBackgroundColor(Color.parseColor("#FF9898"))
        val functionRow = TextView(activity)
        functionRow.gravity = Gravity.CENTER
        functionRow.text = "기능"
        row.addView(functionRow)
        val supported = TextView(activity)
        supported.gravity = Gravity.CENTER
        supported.text = "값"
        row.addView(supported)
        getInfoTable.addView(row)
        getInfoPrint(getInfoTable)
    }

    private fun generateRow(key: String, value: String?, isOdd: Boolean): TableRow {
        val tr = TableRow(activity)
        tr.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)
        if (isOdd) {
            tr.setBackgroundColor(resources.getColor(R.color.test))
        }
        val keyView = TextView(activity)
        val valueView = TextView(activity)
        keyView.setTypeface(null, Typeface.BOLD)
        keyView.setPadding(30, 20, 30, 20)
        //keyView.setTextSize(15);
        keyView.text = key
        valueView.text = value
        valueView.isSingleLine = false
        valueView.maxLines = 20
        valueView.setPadding(0, 20, 30, 20)
        tr.addView(keyView)

        // Set width to zero and weight to 1
        tr.addView(valueView, TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1f))
        return tr
    }

    @Throws(Exception::class)
    private fun getInfoPrint(getInfoTable: TableLayout) {
        try {
            // JsonNode jnode = mapper.readValue(bais, JsonNode.class);
            val responseMap = getInfoResponse
            var result = ""
            var version: ArrayList<String>?
            var extensions: ArrayList<String>?
            var aaguid: ByteArray?
            var options: LinkedHashMap<String?, Boolean?>?
            var pinUvAuthProtocol: ArrayList<Int?>?
            var seq = true
            for (key in responseMap.keys) {
//            System.out.println("key : " + key);
                //TableRow tableRow = new TableRow((getActivity()));
                //TextView functionRow = new TextView(getActivity());
                //TextView valuewRow = new TextView(getActivity());
                //valuewRow.setWidth(600);
                var menu = ""
                val value = ""
                when (key) {
                    "1" -> {
                        version = responseMap[key] as ArrayList<String>?
                        // resultData += "Version : \n";
                        //functionRow.setText("Version");
                        menu = "Version"
                        result = ""
                        var index = 0
                        while (index < version!!.size) {

                            // resultData += "\t[" + version.get(index) + "]\n";
                            result += """
                                ${version[index]}
                                
                                """.trimIndent()
                            index++
                        }
                    }
                    "2" -> {
                        extensions = responseMap[key] as ArrayList<String>?
                        // resultData += "Extensions : \n";
                        //functionRow.setText("Exnesions");
                        menu = "Exnesions"
                        result = ""
                        var index = 0
                        while (index < extensions!!.size) {

                            // resultData += "\t[" + extensions.get(index) + "]\n";
                            result += """
                                ${extensions[index]}
                                
                                """.trimIndent()
                            index++
                        }
                    }
                    "3" -> {
                        aaguid = responseMap[key] as ByteArray?
                        // resultData += "AAGUID : \n";
                        // resultData += "\t[" + Util.getHexString(aaguid) + "]\n";
                        //functionRow.setText("AAGUID");
                        menu = "AAGUID"
                        result = aaguid!!.getHexString()
                    }
                    "4" -> {
                        options = responseMap[key] as LinkedHashMap<String?, Boolean?>?
                        // resultData += "Opionts : \n";
                        //functionRow.setText("Options");
                        menu = "Options"
                        result = ""
                        result += if (options!!["rk"]!!) {
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
                            hasclientPIN = true
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
                        pinUvAuthProtocol = responseMap[key] as ArrayList<Int?>?
                        // resultData += "PinUvAuthProtocol : \n";
                        //functionRow.setText("PinUvAuthProtocol");
                        menu = "PinUvAuthProtocol"
                        result = ""
                        var index = 0
                        while (index < pinUvAuthProtocol!!.size) {

                            // resultData += "\t[" + pinUvAuthProtocol.get(index) + "]\n";
                            result += pinUvAuthProtocol[index]
                            index++
                        }
                    }
                    "7" -> {
                        // resultData += "지원가능한 Credential 수 : \n";
                        //functionRow.setText("지원 가능한\nCredential 수");
                        menu = "지원 가능한\nCredential 수"
                        // resultData += "\t[" + (Integer) responseMap.get(key) + " bytes]\n";
                        result = (responseMap[key] as Int?).toString()
                    }
                    "8" -> {
                        // resultData += "CredentialID 길이 : \n";
                        //functionRow.setText("CredentialID 길이");
                        menu = "CredentialID 길이"
                        // resultData += "\t[" + (Integer) responseMap.get(key) + "]\n";
                        result = (responseMap[key] as Int?).toString()
                    }
                }
                //tableRow.addView(functionRow);

                //valuewRow.setText(result);
                //tableRow.addView(valuewRow);
                val tableRow = generateRow(menu, result, seq)
                seq = !seq
                getInfoTable.addView(tableRow)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}