package com.challenge.fidoreader;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.challenge.fidoreader.Util.Util;

import java.io.IOException;

public class CardReader {
    public final static String TAG = "CardReader";

    private NfcAdapter mAdapter=null;
    private PendingIntent mPendingIntent;
    private String[][] mTechLists;
    private IntentFilter[] mFilters;
    public static IsoDep myTag;
    boolean mFirstDetected=false;
    boolean mShowAtr=false;

    public boolean result = false;
    public int result_image = 0;
    public String result1_str = "";
    public String result2_str = "";

    public CardReader(AppCompatActivity mainActivity, Intent intent){
        mAdapter = NfcAdapter.getDefaultAdapter(mainActivity);
        resolveIntent(intent);

        mPendingIntent = PendingIntent.getActivity(mainActivity, 0, new Intent(mainActivity, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try{
            ndef.addDataType("*/*");
        }catch (IntentFilter.MalformedMimeTypeException e){
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] { ndef, };
        mTechLists = new String[][] { new String[] { IsoDep.class.getName() } };
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onResume(AppCompatActivity mainActivity){

        if(  (mFirstDetected==true) && (myTag.isConnected()) ){
            if(mShowAtr==true){
                result_image = R.drawable.ic_icc_on_atr;
            }
            else{
                result_image = R.drawable.ic_icc_on;
            }
        }
        else{
            result_image = R.drawable.ic_icc_off;
        }

        if( (mAdapter == null) || (!mAdapter.isEnabled()) ) {
            if (mAdapter == null) {
                result1_str = "PLEASE TAP CARD";
            }else if(mAdapter.isEnabled()){
                result2_str = "NFC ENABLED";
                
            }else{
                result1_str = "PLEASE TAP CARD";
                result2_str = "NO READER DETECTED";
            }
        }

        if (mAdapter != null) {
            if (mAdapter.isEnabled()) {
                result2_str = "NFC ENABLED";
            }
            mAdapter.enableForegroundDispatch(mainActivity, mPendingIntent, mFilters, mTechLists);

        }else{
            result2_str = "NO READER DETECTED";
            result1_str = "PLEASE TAP CARD";
        }

    }

    public void onPause(AppCompatActivity activity){
        if( (mFirstDetected==true) && (myTag.isConnected()) ){
            if(mShowAtr==true){
                result_image = R.drawable.ic_icc_on_atr;
            }
            else{
                result_image = R.drawable.ic_icc_on;
            }
        }
        else{
            result_image = R.drawable.ic_icc_off;
        }
        mAdapter.disableForegroundDispatch(activity);
    }


    public void resolveIntent(Intent intent)
    {
        result = false;
        Log.v(TAG, "resolveIntent");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            final Tag t = (Tag) tag;


            myTag = IsoDep.get(t);
            mFirstDetected=true;
            if( !myTag.isConnected() ){
                try{
                    myTag.connect();
                    myTag.setTimeout(5000);
                }
                catch (IOException e){
                    e.printStackTrace();
                    return ;
                }
            }
            if( myTag.isConnected() ){
                if(mShowAtr == true){
                    result_image = R.drawable.ic_icc_on_atr;
                    //page1_1.setEnabled();
                    result = true;
                }
                else{
                    result_image = R.drawable.ic_icc_on;
                }

                //"Card Removal will NOT be detected";

                String szATR = null;
                try{
                    mShowAtr=true;
                    szATR ="3B" + Util.getATRLeString(myTag.getHistoricalBytes())+ "8001" + Util.getHexString(myTag.getHistoricalBytes())+""+ Util.getATRXorString(myTag.getHistoricalBytes());
                }
                catch (Exception e){
                    mShowAtr=false;
                    szATR = "CARD DETECTED  ";
                }
                result1_str = szATR;
                if(mShowAtr == true){
                    result = true;
                }
            }
            else{
                result_image = R.drawable.ic_icc_off;
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
        if( mFirstDetected==true && myTag.isConnected() ){
            if(mShowAtr==true){
                result_image = R.drawable.ic_icc_on_atr;
            }
            else{
                result_image = R.drawable.ic_icc_on;
            }
        }
        else{
            result_image = R.drawable.ic_icc_off;
        }
    }

}
