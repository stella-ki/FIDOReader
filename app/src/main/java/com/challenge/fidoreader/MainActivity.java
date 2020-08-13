package com.challenge.fidoreader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.challenge.fidoreader.Util.Code;
import com.challenge.fidoreader.fagment.FingerItem;
import com.challenge.fidoreader.fidoReader.CardReader;
import com.google.android.material.tabs.TabLayout;
import com.challenge.fidoreader.fagment.CredentialItem;
import com.challenge.fidoreader.fagment.ReaderButtonFragment;
import com.challenge.fidoreader.fagment.AuthenticatorFragment;
import com.challenge.fidoreader.fidoReader.Authenticator;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity  extends AppCompatActivity {
    public final static String TAG = "MainActivity";

    ArrayList<String> titles = new ArrayList<>();
    Fragment[] pages;

    ReaderButtonFragment page1_1;
    AuthenticatorFragment page2;

    public static Authenticator authenticator;
    public static CardReader cardReader;

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
        titles.add("FIDO2 Reader");
        titles.add("Authenticators");

        ViewPager2 viewPager2 = findViewById(R.id.view_pager);

        viewPager2.setAdapter(new ViewPagerAdapter(this, pages));
        new TabLayoutMediator(tabs, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(titles.get(position));
                    }
                }).attach();


        cardReader = new CardReader(this, getIntent());
        authenticator = new Authenticator();


    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v(TAG, "onStart");
        try {
            if(page1_1.isReady()){
                page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str, cardReader.result);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        try {
            cardReader.onResume(this);
            if(page1_1.isReady()){
                page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str, cardReader.result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.v(TAG, "onPause");

        try {
            cardReader.onPause(this);
            if(page1_1.isReady()){
                page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str, cardReader.result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            cardReader.resolveIntent(intent);

            if (page1_1.isReady()) {
                page1_1.setResult(cardReader.result_image, cardReader.result1_str, cardReader.result2_str, cardReader.result);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onChangeFragmentToList(ArrayList<CredentialItem> list){
        Log.v(TAG, "onchangeFragment");
        if(list == null){
            Toast.makeText(this.getApplicationContext(),"Error 발생", Toast.LENGTH_SHORT).show();
            return;
        }else if(list.size() == 0){
            Toast.makeText(this.getApplicationContext(),"Credential is not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        try{

            Intent intent = new Intent(getApplicationContext(), CredListActivity.class);
            intent.putParcelableArrayListExtra("Credentiallist", list);
            startActivityForResult(intent, Code.requestCode);

        }catch (Exception e){
            Toast.makeText(this.getApplicationContext(),"Error 발생", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void onChangeFragmentToList2(ArrayList<FingerItem> list){
        Log.v(TAG, "onchangeFragment");
        if(list == null){
            Toast.makeText(this.getApplicationContext(),"Error 발생", Toast.LENGTH_SHORT).show();
            return;
        }/*else if(list.size() == 0){
            Toast.makeText(this.getApplicationContext(),"Fingerprint is not exist", Toast.LENGTH_SHORT).show();
        }*/

        try{
            Intent intent = new Intent(getApplicationContext(), EnrollManageActivty.class);
            intent.putParcelableArrayListExtra("fingerItem", list);
            startActivityForResult(intent, Code.requestCode);

        }catch (Exception e){
            Toast.makeText(this.getApplicationContext(),"Error 발생", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}



