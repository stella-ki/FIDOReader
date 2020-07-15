package com.syki.fidoreader;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.syki.fidoreader.fagment.page1;
import com.syki.fidoreader.fagment.page2;

public class MainActivity extends AppCompatActivity {
    Fragment page1;
    Fragment page2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바에 툴바 추가
        androidx.appcompat.widget.Toolbar toolbar =
                (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //프래그먼트 선언
        page1 = new page1();
        page2 = new page2();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,page1).commit();
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
                    selected = page1;
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




}