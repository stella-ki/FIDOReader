package com.challenge.fidoreader.reader

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentFilter.MalformedMimeTypeException
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Build
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.challenge.fidoreader.Util.getATRLeString
import com.challenge.fidoreader.Util.getATRXorString
import com.challenge.fidoreader.Util.getHexString
import java.io.IOException

class CardReader {
    val TAG = "CardReader"

    lateinit var myTag:IsoDep

    var mAdapter:NfcAdapter
    var mPendingIntent:PendingIntent
    var mTechLists: Array<Array<String>>
    var  mFilters: Array<IntentFilter>

    var mFirstDetected = false
    var mShowAtr = false

    var result = false
    var cntionState : ConnectState = ConnectState.ICC_OFF
    var nfcReaderState :NfcReaderState = NfcReaderState.NO_READER
    var cntionResult = ""


    constructor(mainActivity: AppCompatActivity?, intent: Intent) {
        mAdapter = NfcAdapter.getDefaultAdapter(mainActivity)
        resolveIntent(intent)
        mPendingIntent = PendingIntent.getActivity(mainActivity, 0, Intent(mainActivity, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        val ndef = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        try {
            ndef.addDataType("*/*")
        } catch (e: MalformedMimeTypeException) {
            throw RuntimeException("fail", e)
        }
        mFilters = arrayOf(ndef)
        mTechLists = arrayOf(arrayOf(IsoDep::class.java.name))

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun onResume(mainActivity: AppCompatActivity?) {
        cntionState = if (mFirstDetected && myTag.isConnected) {
            if (mShowAtr) {
                ConnectState.ICC_ON_ATR
            } else {
                ConnectState.ICC_ON
            }
        } else {
            ConnectState.ICC_OFF
        }
        //if (!mAdapter.isEnabled) {
        if (mAdapter.isEnabled) {
            nfcReaderState = NfcReaderState.READER_ABLE//"NFC ENABLED"
        } else {
            cntionResult = "PLEASE TAP CARD"
            nfcReaderState = NfcReaderState.NO_READER//"NO READER DETECTED"
        }
        //}
        //if (mAdapter.isEnabled) {
        //    nfcReaderState = NfcReaderState.READER_ABLE//"NFC ENABLED"
        //}
        mAdapter.enableForegroundDispatch(mainActivity, mPendingIntent, mFilters, mTechLists)
    }

    fun onPause(activity: AppCompatActivity?) {
        cntionState = if (mFirstDetected && myTag.isConnected) {
            if (mShowAtr) {
                ConnectState.ICC_ON_ATR
            } else {
                ConnectState.ICC_ON
            }
        } else {
            ConnectState.ICC_OFF
        }
        mAdapter.disableForegroundDispatch(activity)
    }

    //onNewIntent
    fun resolveIntent(intent: Intent) {
        result = false
        Log.v(TAG, "resolveIntent")
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG)
            val t = tag as Tag
            myTag = IsoDep.get(t)
            mFirstDetected = true
            if (!myTag.isConnected) {
                try {
                    myTag.connect()
                    myTag.timeout = 5000
                } catch (e: IOException) {
                    e.printStackTrace()
                    return
                }
            }
            if (myTag.isConnected) {
                if (mShowAtr) {
                    cntionState = ConnectState.ICC_ON_ATR
                    result = true
                } else {
                    cntionState = ConnectState.ICC_ON
                }

                //"Card Removal will NOT be detected";
                var szATR = ""
                try {
                    mShowAtr = true
                    szATR = "3B" + getATRLeString(myTag.historicalBytes) + "8001" + myTag.historicalBytes.getHexString() + "" + getATRXorString(myTag.historicalBytes)
                } catch (e: Exception) {
                    mShowAtr = false
                    szATR = "CARD DETECTED  "
                }
                cntionResult = szATR
                if (mShowAtr) {
                    result = true
                }
            } else {
                cntionState = ConnectState.ICC_OFF
            }
        }
        cntionState = if (mFirstDetected && myTag.isConnected) {
            if (mShowAtr) {
                ConnectState.ICC_ON_ATR
            } else {
                ConnectState.ICC_ON
            }
        } else {
            ConnectState.ICC_OFF
        }
    }

    enum class ConnectState{
        ICC_ON_ATR, ICC_ON, ICC_OFF
    }
    enum class NfcReaderState{
        NO_READER, READER_ABLE
    }

}