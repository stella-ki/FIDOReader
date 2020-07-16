package com.syki.fidoreader;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.syki.fidoreader.fagment.Credential_item;
import com.syki.fidoreader.fagment.ReaderButtonFragment;
import com.syki.fidoreader.fagment.ReaderFragment;
import com.syki.fidoreader.fagment.AuthenticatorFragment;

public class MainActivity extends AppCompatActivity {
    ReaderButtonFragment page1_1;
    ReaderFragment page1_2;
    AuthenticatorFragment page2;

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
        page1_2 = new ReaderFragment();
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
                    //page1_1.addCredentialItem(new Credential_item("test1", "test2", R.drawable.ic_icc_off));
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

    public void onChangeFragment(){
        Log.v("MainActivicy", "onchangeFragment");
        getSupportFragmentManager().beginTransaction().replace(R.id.container,page1_2).commit();
    }

}



