package com.challenge.fidoreader;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.challenge.fidoreader.Exception.UserException;
import com.challenge.fidoreader.fagment.CredDeleteBottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.challenge.fidoreader.Util.Util;
import com.challenge.fidoreader.fagment.CredentialItem;
import com.challenge.fidoreader.fagment.ReaderButtonFragment;
import com.challenge.fidoreader.fagment.AuthenticatorFragment;
import com.challenge.fidoreader.fagment.ReaderListFragment;
import com.challenge.fidoreader.fido.Authenticator;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity  extends AppCompatActivity implements CredDeleteBottomSheetDialog.BottomSheetListener{
    public final static String TAG = "MainActivity";

    private ProgressBar pgsBar;
    Fragment[] pages;
    ReaderButtonFragment page1_1;
    ReaderListFragment page1_2;

    CredDeleteBottomSheetDialog bottomSheet;

    AuthenticatorFragment page2;

    public Authenticator authenticator;
    public CardReader cardReader;

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
        pgsBar.setVisibility(View.GONE);

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v(TAG, "onStart");

        cardReader = new CardReader(this, getIntent());

        page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str, cardReader.result);

        authenticator = new Authenticator();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        cardReader.onResume(this);
        page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.v(TAG, "onPause");

        cardReader.onPause(this);
        page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        cardReader.resolveIntent(intent);

        page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str, cardReader.result);
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


    @Override
    public void onButtonClicked(CredentialItem cii) {
        if(cii == null){
            return;
        }
        Log.v(TAG, "onButtopnclieck : " + cii.getCredential_id());

        try{
            pgsBar.setVisibility(View.VISIBLE);
            authenticator.deleteCredential(cii.getCredential_id());
            bottomSheet.dismiss();
            page1_2.deleteCredentialItem(cii);

            Toast.makeText(this.getApplicationContext(),"Deletion is success", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }finally {
            pgsBar.setVisibility(View.INVISIBLE);
        }
    }

    public void onChangeFragmentToDelete(CredentialItem cii){
        bottomSheet = new CredDeleteBottomSheetDialog(cii);
        bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
    }


    public void onChangeFragmentToMain(){
        onChangeFragment(page1_1);
    }

    public void onChangeFragmentToList(){
        Log.v(TAG, "onchangeFragment");
        try{
            //pgsBar.setVisibility(View.VISIBLE);

            authenticator.setTag(cardReader.myTag);

            //ArrayList<CredentialItem> list = authenticator.getCredentialList();
            GoogleTranslate googleTranslate = new GoogleTranslate(pgsBar);
            AsyncTask<Object, Object, Object> asyncTask = googleTranslate.execute(authenticator);
            ArrayList<CredentialItem> list = (ArrayList<CredentialItem>)asyncTask.get();

            for (int i = 0; i< list.size(); i++){
                page1_2.addCredentialItem(list.get(i));
            }


            onChangeFragment(page1_2);

        }catch (Exception e){
            Toast.makeText(this.getApplicationContext(),"Error 발생", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }finally {
           //pgsBar.setVisibility(View.INVISIBLE);
        }
    }

    public void getCredentialList() throws Exception{

        try{
            Log.v(TAG, "getCredentialList");
            //progressON("Loading...");

            authenticator.setTag(cardReader.myTag);

            ArrayList<CredentialItem> list = authenticator.getCredentialList();

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

}



