package com.challenge.fidoreader.fidoReader

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
import com.challenge.fidoreader.R
import com.challenge.fidoreader.Util.Util
import java.io.IOException

class CardReader {
    val TAG = "CardReader"

    companion object{
        lateinit var myTag:IsoDep
    }

    lateinit var mAdapter:NfcAdapter
    lateinit var mPendingIntent:PendingIntent
    lateinit var mTechLists: Array<Array<String>>
    lateinit var  mFilters: Array<IntentFilter>

    var mFirstDetected = false
    var mShowAtr = false

    var result = false
    var result_image = 0
    var result1_str = ""
    var result2_str = ""


    fun CardReader(mainActivity: AppCompatActivity?, intent: Intent) {
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
        result_image = if (mFirstDetected == true && myTag.isConnected()) {
            if (mShowAtr == true) {
                R.drawable.ic_icc_on_atr
            } else {
                R.drawable.ic_icc_on
            }
        } else {
            R.drawable.ic_icc_off
        }
        if (mAdapter == null || !mAdapter.isEnabled) {
            if (mAdapter == null) {
                result1_str = "PLEASE TAP CARD"
            } else if (mAdapter.isEnabled) {
                result2_str = "NFC ENABLED"
            } else {
                result1_str = "PLEASE TAP CARD"
                result2_str = "NO READER DETECTED"
            }
        }
        if (mAdapter != null) {
            if (mAdapter.isEnabled) {
                result2_str = "NFC ENABLED"
            }
            mAdapter.enableForegroundDispatch(mainActivity, mPendingIntent, mFilters, mTechLists)
        } else {
            result2_str = "NO READER DETECTED"
            result1_str = "PLEASE TAP CARD"
        }
    }

    fun onPause(activity: AppCompatActivity?) {
        result_image = if (mFirstDetected == true && myTag.isConnected()) {
            if (mShowAtr == true) {
                R.drawable.ic_icc_on_atr
            } else {
                R.drawable.ic_icc_on
            }
        } else {
            R.drawable.ic_icc_off
        }
        mAdapter.disableForegroundDispatch(activity)
    }


    fun resolveIntent(intent: Intent) {
        result = false
        Log.v(TAG, "resolveIntent")
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG)
            val t = tag as Tag
            myTag = IsoDep.get(t)
            mFirstDetected = true
            if (!myTag.isConnected()) {
                try {
                    myTag.connect()
                    myTag.setTimeout(5000)
                } catch (e: IOException) {
                    e.printStackTrace()
                    return
                }
            }
            if (myTag.isConnected()) {
                if (mShowAtr == true) {
                    result_image = R.drawable.ic_icc_on_atr
                    //page1_1.setEnabled();
                    result = true
                } else {
                    result_image = R.drawable.ic_icc_on
                }

                //"Card Removal will NOT be detected";
                var szATR: String? = null
                try {
                    mShowAtr = true
                    szATR = "3B" + Util.getATRLeString(myTag.getHistoricalBytes()) + "8001" + Util.getHexString(myTag.getHistoricalBytes()) + "" + Util.getATRXorString(myTag.getHistoricalBytes())
                } catch (e: Exception) {
                    mShowAtr = false
                    szATR = "CARD DETECTED  "
                }
                result1_str = szATR!!
                if (mShowAtr == true) {
                    result = true
                }
            } else {
                result_image = R.drawable.ic_icc_off
            }

            /*
            mAdapter.ignore(t, 10000, new NfcAdapter.OnTagRemovedListener (){

                @Override
                public void onTagRemoved() {
                    Log.v(TAG, "onTagRemoved");
                    result_image = R.drawable.ic_icc_off;
                    result2_str = "NFC ENABLED";
                    result1_str = "";
                }
            },  new Handler(Looper.myLooper()) );
            */
        }
        result_image = if (mFirstDetected == true && myTag.isConnected()) {
            if (mShowAtr == true) {
                R.drawable.ic_icc_on_atr
            } else {
                R.drawable.ic_icc_on
            }
        } else {
            R.drawable.ic_icc_off
        }
    }


}