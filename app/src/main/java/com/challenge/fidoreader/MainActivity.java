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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.challenge.fidoreader.Exception.UserException;
import com.google.android.material.tabs.TabLayout;
import com.challenge.fidoreader.Util.Util;
import com.challenge.fidoreader.fagment.Credential_item;
import com.challenge.fidoreader.fagment.ReaderButtonFragment;
import com.challenge.fidoreader.fagment.AuthenticatorFragment;
import com.challenge.fidoreader.fagment.ReaderListFragment;
import com.challenge.fidoreader.fido.Authenticator;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity  extends AppCompatActivity {
    public final static String TAG = "MainActivity";

    private ProgressBar pgsBar;
    Fragment[] pages;
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

        pages = new Fragment[3];
        pages[0] = page1_1;
        pages[1] = page1_2;
        pages[2] = page2;

        getSupportFragmentManager().beginTransaction().add(R.id.container,page1_1).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container,page1_2).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container,page2).commit();

        getSupportFragmentManager().beginTransaction().hide(page1_1).commit();
        getSupportFragmentManager().beginTransaction().hide(page1_2).commit();
        getSupportFragmentManager().beginTransaction().hide(page2).commit();

        getSupportFragmentManager().beginTransaction().show(page1_1).commit();

        //탭 레이아웃 호출 후 탭 추가
        TabLayout tabs = (TabLayout) findViewById(R.id.tabLayout);
        tabs.addTab(tabs.newTab().setText("Authenticators"));
        tabs.addTab(tabs.newTab().setText("FIDO2 Reader"));

        //각 탭을 누를 때 메소드 호출
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if(position == 0){
                    onChangeFragment(page1_1);
                }
                else if(position == 1){
                    onChangeFragment(page2);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pgsBar = (ProgressBar) findViewById(R.id.h_progressbar);
        //pgsBar.setIndeterminate(true);
        pgsBar.setVisibility(View.INVISIBLE);

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

        page1_1.setImageView(R.drawable.ic_icc_off);
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
                page1_1.setImageView(R.drawable.ic_icc_on_atr);
            }
            else{
                page1_1.setImageView(R.drawable.ic_icc_on);
            }
        }
        else{
            page1_1.setImageView(R.drawable.ic_icc_off);
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
                page1_1.setImageView(R.drawable.ic_icc_on_atr);
            }
            else{
                page1_1.setImageView(R.drawable.ic_icc_on);
            }
        }
        else{
            page1_1.setImageView(R.drawable.ic_icc_off);
        }
        mAdapter.disableForegroundDispatch(this);


    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    public void onChangeFragment(Fragment frgmt){
        for (int i = 0; i <pages.length; i++){
            if(!frgmt.equals(pages[i])){
                getSupportFragmentManager().beginTransaction().hide(pages[i]).commit();
            }else{
                getSupportFragmentManager().beginTransaction().show(pages[i]).commit();
            }

        }
    }

    public void onChangeFragmentToMain(){
        onChangeFragment(page1_1);
    }

    public void onChangeFragmentToList(){
        Log.v(TAG, "onchangeFragment");
        try{
            pgsBar.setVisibility(View.VISIBLE);
            getCredentialList();

            onChangeFragment(page1_2);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            pgsBar.setVisibility(View.INVISIBLE);
        }
    }




    public void getCredentialList() throws Exception{

        try{
            Log.v(TAG, "getCredentialList");
            //progressON("Loading...");

            authenticator.setTag(myTag);

            ArrayList<Credential_item> list = authenticator.getCredentialList();

            for (int i = 0; i< list.size(); i++){
                page1_2.addCredentialItem(list.get(i));

            }

        }catch (UserException ue){
            Toast.makeText(this.getApplicationContext(),ue.getMessage(), Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this.getApplicationContext(),"Error 발생", Toast.LENGTH_SHORT).show();
            throw e;
        }finally {
            //progressOFF();
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
                    page1_1.setImageView(R.drawable.ic_icc_on_atr);
                    page1_1.setEnabled();
                }
                else{
                    page1_1.setImageView(R.drawable.ic_icc_on);
                }

                vShowCardRemovalInfo();

                String szATR = null;
                try{
                    mShowAtr=true;
                    szATR =" 3B" + Util.getATRLeString(myTag.getHistoricalBytes())+ "8001" + Util.getHexString(myTag.getHistoricalBytes())+""+ Util.getATRXorString(myTag.getHistoricalBytes());
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
                page1_1.setImageView(R.drawable.ic_icc_off);
            }
        }
        if( mFirstDetected==true && myTag.isConnected() ){
            if(mShowAtr==true){
                page1_1.setImageView(R.drawable.ic_icc_on_atr);
            }
            else{
                page1_1.setImageView(R.drawable.ic_icc_on);
            }
        }
        else{
            page1_1.setImageView(R.drawable.ic_icc_off);
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



