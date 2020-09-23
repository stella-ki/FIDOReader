package com.challenge.fidoreader.frag.reader

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.challenge.fidoreader.*
import com.challenge.fidoreader.Util.ParcelableActivityData
import com.challenge.fidoreader.Util.atohex
import com.challenge.fidoreader.Util.cardReader
import com.challenge.fidoreader.frag.reader.InputPINFragment.OnDialogResult
import com.challenge.fidoreader.fidoReader.Authenticator
import com.challenge.fidoreader.fidoReader.data.CBOR_CODE_CTAP2_ERR_INVALID_OPTION
import com.challenge.fidoreader.fidoReader.data.CBOR_CODE_CTAP2_ERR_NO_CREDENTIALS
import com.challenge.fidoreader.reader.CardReader
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import com.fasterxml.jackson.dataformat.cbor.CBORParser
import java.io.ByteArrayInputStream
import java.util.*
import kotlin.collections.ArrayList


class ReaderButtonFragment : Fragment(){
    val TAG = "ReaderButtonFragment"

    lateinit var imageView: ImageView
    lateinit var textView2: TextView
    lateinit var textView3: TextView

    lateinit var getCredListbtn :Button
    lateinit var getinfoBtn:Button
    lateinit var clientPinBtn:Button
    lateinit var enrollManageBtn:Button
    lateinit var resetBtn:Button

    var hasClientPIN = false

    lateinit var mainActivity:MainActivity

    var pgsBar: ProgressBar? = null

    var getinfoSheet: GetInfoResponseBottomSheetDialog? = null
    var getInfoResponse: Map<String, Any>? = null

    var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.v(TAG, "onCreateView")

        var mainView: View = inflater.inflate(R.layout.fragment_reader_button, container, false)

        getCredListbtn  = mainView.findViewById(R.id.readerActivationBtn)
        imageView = mainView.findViewById(R.id.readerActivationImageView)
        textView2 = mainView.findViewById(R.id.readerActivationText2)
        textView3 = mainView.findViewById(R.id.readerActivationText3)
        enrollManageBtn = mainView.findViewById(R.id.EnrollBtn)
        resetBtn = mainView.findViewById(R.id.resetBtn)
        pgsBar = mainView.findViewById(R.id.h_progressbar)
        getinfoBtn = mainView.findViewById(R.id.KonaBIOPASSGetInfoBtn)
        clientPinBtn = mainView.findViewById(R.id.KonaBIOPASSPINBtn)

        getCredListbtn .setOnClickListener{
            Log.v(TAG, "onClick")
            try {
                val inputPINFragment = InputPINFragment(mainActivity, "Credential")
                inputPINFragment.show(fragmentManager!!, "dialog")
                inputPINFragment.isCancelable = false
                inputPINFragment.setDialogResult(object : OnDialogResult {
                    override fun finish(result: String?) {
                        Log.v(TAG, "inputPINFragment result : " + result)

                        if (result == "OK") {
                            val googleTranslate = GoogleTranslate()
                            googleTranslate.execute()
                        }
                    }
                })
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
        getCredListbtn .isEnabled = false


        getinfoBtn.setOnClickListener{
            getinfoSheet = GetInfoResponseBottomSheetDialog(clientPinBtn, mainActivity, getInfoResponse!!)
            getinfoSheet!!.show(activity!!.supportFragmentManager, "exampleBottomSheet")
        }
        //getinfoBtn.isEnabled = false

        clientPinBtn.setOnClickListener {
            var pinFragment = if(clientPinBtn.text.toString()=="Set New PIN"){
                SetNewPINFragment(mainActivity)
            }else{
                ChangePINFragment(mainActivity)
            }

            pinFragment.show(fragmentManager!!, "dialog")
            pinFragment.isCancelable = false

        }
        clientPinBtn.isEnabled = false

        enrollManageBtn.setOnClickListener {
            Log.v(TAG, "onClick")
            try {
                val inputPINFragment = InputPINFragment(mainActivity, "Fingerprint")
                inputPINFragment.show(fragmentManager!!, "dialog")
                inputPINFragment.isCancelable = false

                inputPINFragment.setDialogResult(object : OnDialogResult {
                    override fun finish(result: String?) {
                        if (result == "OK") {
                            val googleTranslate = GetEnrollInformation()
                            googleTranslate.execute()
                        }
                    }
                })
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        resetBtn.setOnClickListener {
            Log.v(TAG, "resetBtn")
            try {
                val googleTranslate = ResetProcess()
                googleTranslate.execute()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        pgsBar!!.visibility = View.GONE

        try {
            setResult(cardReader, cardReader.result)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        isReady = true

        return mainView
    }

    fun setResult(c:CardReader){
        when(c.cntionState){
            CardReader.ConnectState.ICC_ON_ATR ->{
                imageView.setImageResource(R.drawable.ic_icc_on_atr)
            } CardReader.ConnectState.ICC_ON ->{
                imageView.setImageResource(R.drawable.ic_icc_on)
            } CardReader.ConnectState.ICC_OFF ->{
                imageView.setImageResource(R.drawable.ic_icc_off)
            }
        }

        when(c.nfcReaderState){
            CardReader.NfcReaderState.NO_READER ->{
                textView2.text = "NFC ENABLED"
            } CardReader.NfcReaderState.READER_ABLE ->{
                textView2.text = "NO READER DETECTED"
            }
        }

        textView3.text = c.cntionResult
    }

    fun setResult(c:CardReader, result: Boolean){
        setResult(c)
        if(result){
            setEnable()
        }
    }

    fun setEnable(){
        Log.v(TAG, "SetEnable")
        getCredListbtn.isEnabled = true

        //getInfo()
        getinfoBtn.isEnabled = true
        getInfoResponse = getInfo();
        clientPinBtn.isEnabled = true
    }

    private fun getInfo(): Map<String, Any>? {
        //MainActivity.authenticator.myTag = CardReader.myTag
        var result = MainActivity.authenticator.getInfo()
        val bais = ByteArrayInputStream(result.atohex())
        val cf = CBORFactory()
        val mapper = ObjectMapper(cf)
        var cborParser: CBORParser? = null
        cborParser = cf.createParser(bais)
        var responseMap: Map<String, Any>? = null
        responseMap = mapper.readValue(cborParser, object : TypeReference<Map<String?, Any?>?>() {})
        for (key in responseMap!!.keys) {
            if (key == "4") {
                val options = responseMap[key] as LinkedHashMap<String, Boolean>?
                if (options!!["clientPin"]!!) {
                    clientPinBtn.setText("Change PIN")
                } else {
                    clientPinBtn.setText("Set New PIN")
                }
            }
        }
        return responseMap
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")
        setResult(cardReader)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = (activity as MainActivity?)!!
    }

    override fun onDetach() {
        super.onDetach()
        //mainActivity = null
        Log.v(TAG, "onDetach")
    }

    abstract inner class AsyncTaskProcess  : AsyncTask<Any?, Any?, Any?>() {
        open var TAG = "AsyncTaskProcess"
        var list : ArrayList<Parcelable>? = null
        lateinit var errorcode :String
        lateinit var parcelableActivityData : ParcelableActivityData

        abstract fun process(authenticator: Authenticator) : ArrayList<Parcelable>

        override fun doInBackground(vararg params: Any?): Any? {
            Log.v(TAG, "doInBackground")
            try {
                val authenticator: Authenticator = MainActivity.authenticator
                //authenticator.myTag = CardReader.myTag
                list = process(authenticator)
            } catch (e: java.lang.Exception) {
                Log.d("AsyncTaskProcess : ", "Error : " + e.message)
                if (e.message.equals(errorcode)) {
                    list = ArrayList()
                }
                e.printStackTrace()
            }
            return list
        }

        override fun onProgressUpdate(vararg values: Any?) {
            super.onProgressUpdate(*values)
            Log.v(TAG, "${values[0]}번 count했습니다.".trimIndent())
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.v(TAG, "onPreExecute")
            pgsBar!!.visibility = View.VISIBLE
        }

        override fun onPostExecute(s: Any?) {
            super.onPostExecute(s)
            Log.v(TAG, "onPostExecute")
            pgsBar!!.visibility = View.GONE
            mainActivity.showActivityList(parcelableActivityData, list)
        }
    }

    inner class GoogleTranslate : AsyncTaskProcess() {
        override var TAG = "GoogleTranslate"

        init {
            errorcode = CBOR_CODE_CTAP2_ERR_NO_CREDENTIALS
            parcelableActivityData =  ParcelableActivityData(
                    CredListActivity::class.java as Class<Any>,
                    true,
                    "Credential is not exist",
                    "Credentiallist"
            )
        }

        override fun process(authenticator: Authenticator): ArrayList<Parcelable> {
            return authenticator.getCredentialList() as ArrayList<Parcelable>
        }
    }

    inner class GetEnrollInformation : AsyncTaskProcess() {
        override var TAG = "GetEnrollInformation"

        init {
            errorcode = CBOR_CODE_CTAP2_ERR_INVALID_OPTION
            parcelableActivityData = ParcelableActivityData(
                    EnrollManageActivty::class.java as Class<Any>,
                    false,
                    "Enrollment is not exist",
                    "fingerItem"
            )
        }

        override fun process(authenticator: Authenticator): ArrayList<Parcelable> {
            return authenticator.readEnrollInformation() as ArrayList<Parcelable>
        }
    }

    inner class ResetProcess : AsyncTask<Any?, Any?, Any>() {
        var result = true
        override fun doInBackground(vararg params: Any?): Any? {
            Log.v("ResetProcess", "doInBackground")
            try {
                val authenticator: Authenticator = MainActivity.authenticator
                //authenticator.myTag = CardReader.myTag
                result = authenticator.reset()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onProgressUpdate(vararg values: Any?) {
            super.onProgressUpdate(*values)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.v("ResetProcess", "onPreExecute")
            pgsBar!!.visibility = View.VISIBLE
        }

        override fun onPostExecute(s: Any) {
            super.onPostExecute(s)
            Log.v("ResetProcess", "onPostExecute")
            pgsBar!!.visibility = View.GONE
            Toast.makeText(mainActivity, "정상종료", Toast.LENGTH_SHORT).show()
        }
    }


}



