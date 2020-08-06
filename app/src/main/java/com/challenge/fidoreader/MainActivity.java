package com.challenge.fidoreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.challenge.fidoreader.Util.Code;
import com.challenge.fidoreader.fidoReader.Authenticator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity  extends AppCompatActivity {
    public final static String TAG = "MainActivity";

    private ProgressBar pgsBar;
    Fragment[] pages;

    ReaderButtonFragment page1_1;
    AuthenticatorFragment page2;

    public static Authenticator authenticator;
    public static CardReader cardReader;

    ViewPager2 viewPager2;
    ArrayList<String> titles = new ArrayList<>();

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
        page2 = new AuthenticatorFragment();

        pages = new Fragment[2];
        pages[0] = page1_1;
        pages[1] = page2;

        //탭 레이아웃 호출 후 탭 추가
        TabLayout tabs = (TabLayout) findViewById(R.id.tabLayout);
        titles.add("Authenticators");
        titles.add("FIDO2 Reader");

        viewPager2 = findViewById(R.id.view_pager);

        viewPager2.setAdapter(new ViewPagerAdapter(this, pages));
        new TabLayoutMediator(tabs, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(titles.get(position));
                    }
                }).attach();

        pgsBar = (ProgressBar) findViewById(R.id.h_progressbar);
        //pgsBar.setIndeterminate(true);
        pgsBar.setVisibility(View.GONE);

        cardReader = new CardReader(this, getIntent());
        authenticator = new Authenticator();
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v(TAG, "onStart");
        if(page1_1.isReady()){
            page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str, cardReader.result);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        cardReader.onResume(this);
        if(page1_1.isReady()){
            page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str, cardReader.result);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.v(TAG, "onPause");

        cardReader.onPause(this);
        if(page1_1.isReady()){
            page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str, cardReader.result);
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        cardReader.resolveIntent(intent);

        if(page1_1.isReady()){
            page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str, cardReader.result);
        }
    }

    public void onChangeFragmentToList(){
        Log.v(TAG, "onchangeFragment");
        try{
            authenticator.setTag(cardReader.myTag);

            //ArrayList<CredentialItem> list = authenticator.getCredentialList();
            GoogleTranslate googleTranslate = new GoogleTranslate(pgsBar);
            AsyncTask<Object, Object, Object> asyncTask = googleTranslate.execute(authenticator);
            ArrayList<CredentialItem> list = (ArrayList<CredentialItem>)asyncTask.get();

            Intent intent = new Intent(getApplicationContext(), CredListActivity.class);
            intent.putParcelableArrayListExtra("Credentiallist", list);
            startActivityForResult(intent, Code.requestCode);

        }catch (Exception e){
            Toast.makeText(this.getApplicationContext(),"Error 발생", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}



