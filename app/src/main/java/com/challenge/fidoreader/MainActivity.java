package com.challenge.fidoreader;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.challenge.fidoreader.Util.Util;
import com.challenge.fidoreader.fagment.Credential_item;
import com.challenge.fidoreader.fagment.ReaderButtonFragment;
import com.challenge.fidoreader.fagment.AuthenticatorFragment;
import com.challenge.fidoreader.fagment.ReaderListFragment;
import com.challenge.fidoreader.fido.Authenticator;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity  extends BaseActivity {
    public final static String TAG = "MainActivity";

    ReaderButtonFragment page1_1;
    ReaderListFragment page1_2;
    AuthenticatorFragment page2;

    private NfcAdapter mAdapter=null;
    private PendingIntent mPendingIntent;
    private String[][] mTechLists;
    private IntentFilter[] mFilters;
    static IsoDep myTag;
    boolean mFirstDetected=false;
    boolean mShowAtr=false;

    Authenticator authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바에 툴바 추가
        androidx.appcompat.widget.Toolbar toolbar =
                (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //프래그먼트 선언
        page1_1 = new ReaderButtonFragment();
        page1_2 = new ReaderListFragment();
        page2 = new AuthenticatorFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container,page1_1).commit();
        //탭 레이아웃 호출 후 탭 추가
        TabLayout tabs = (TabLayout) findViewById(R.id.tabLayout);
        tabs.addTab(tabs.newTab().setText("Authenticators"));
        tabs.addTab(tabs.newTab().setText("FIDO2 Reader"));

        //각 탭을 누를 때 메소드 호출
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //몇번째 탭을 선택했는지 가져옴
                int position = tab.getPosition();

                //탭을 선택 한것에 따라 프래그먼트를 바꾼다.
                Fragment selected = null;
                if(position == 0){
                    //
                    selected = page1_1;
                }
                else if(position == 1){
                    selected = page2;
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v(TAG, "onStart");
        resolveIntent(getIntent());

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try{
            ndef.addDataType("*/*");
        }catch (IntentFilter.MalformedMimeTypeException e){
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] { ndef, };
        mTechLists = new String[][] { new String[] { IsoDep.class.getName() } };

        page1_1.setTextview1("");
        page1_1.setTextview2("");
        page1_1.setTextview3("");

        authenticator = new Authenticator();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        if(  (mFirstDetected==true) && (myTag.isConnected()) ){
            if(mShowAtr==true){
                page1_1.setTextview1("R.drawable.ic_icc_on_atr");
            }
            else{
                page1_1.setTextview1("R.drawable.ic_icc_on");
            }
        }
        else{
            page1_1.setTextview1("R.drawable.ic_icc_off");
        }

        if( (mAdapter == null) || (!mAdapter.isEnabled()) ) {
            if (mAdapter == null) {
                page1_1.setTextview2("PLEASE TAP CARD");
            }else if(mAdapter.isEnabled()){
                page1_1.setTextview3("NFC ENABLED");
            }else{
                page1_1.setTextview2("PLEASE TAP CARD");
                page1_1.setTextview3("NO READER DETECTED");
            }
        }

        if (mAdapter != null) {
            if (mAdapter.isEnabled()) {
                page1_1.setTextview3("NFC ENABLED");
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);

        }else{
            page1_1.setTextview3("NO READER DETECTED");
            page1_1.setTextview2("PLEASE TAP CARD");
        }


    }


    @Override
    public void onPause(){
        super.onPause();
        Log.v(TAG, "onPause");

        if( (mFirstDetected==true) && (myTag.isConnected()) ){
            if(mShowAtr==true){
                page1_1.setTextview1("R.drawable.ic_icc_on_atr");
            }
            else{
                page1_1.setTextview1("R.drawable.ic_icc_on");
            }
        }
        else{
            page1_1.setTextview1("R.drawable.ic_icc_off");
        }
        mAdapter.disableForegroundDispatch(this);


    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    public void onChangeFragment(){
        Log.v(TAG, "onchangeFragment");
        try{
            getCredentialList();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,page1_2).commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void getCredentialList() throws Exception{

        try{
            Log.v(TAG, "getCredentialList");
            progressON("Loading...");

            authenticator.setTag(myTag);

            ArrayList<Credential_item> list = authenticator.getCredentialList();

            for (int i = 0; i< list.size(); i++){
                page1_2.addCredentialItem(list.get(i));

            }

            progressOFF();

        }catch (Exception e){
            progressOFF();
            e.printStackTrace();
            throw e;
        }


    }
/*

    private void startProgress() {

        progressON("Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);

    }
*/

    private void resolveIntent(Intent intent)
    {
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
                    return;
                }
            }
            if( myTag.isConnected() ){
                if(mShowAtr == true){
                    page1_1.setTextview1("R.drawable.ic_icc_on_atr");
                    page1_1.setEnabled();
                }
                else{
                    page1_1.setTextview1("R.drawable.ic_icc_on");
                }

                vShowCardRemovalInfo();

                String szATR = null;
                try{
                    mShowAtr=true;
                    szATR =" 3B " + Util.getATRLeString(myTag.getHistoricalBytes())+ "80 01 " + Util.getHexString(myTag.getHistoricalBytes())+""+ Util.getATRXorString(myTag.getHistoricalBytes());
                }
                catch (Exception e){
                    mShowAtr=false;
                    szATR = "CARD DETECTED  ";
                }
                page1_1.setTextview2(szATR);
                if(mShowAtr == true){
                    page1_1.setEnabled();
                }
            }
            else{
                page1_1.setTextview1("R.drawable.ic_icc_off");
            }
        }
        if( mFirstDetected==true && myTag.isConnected() ){
            if(mShowAtr==true){
                page1_1.setTextview1("R.drawable.ic_icc_on_atr");
            }
            else{
                page1_1.setTextview1("R.drawable.ic_icc_on");
            }
        }
        else{
            page1_1.setTextview1("R.drawable.ic_icc_off");
        }
    }


    private void vShowCardRemovalInfo()
    {
        Context context = getApplicationContext();
        CharSequence text = "Card Removal will NOT be detected";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}



