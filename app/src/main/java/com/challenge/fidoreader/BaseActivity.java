package com.challenge.fidoreader;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by TedPark on 2017. 3. 18..
 */

public class BaseActivity extends AppCompatActivity {


    public void progressON() {
        BaseApplication.getInstance().progressON(this, null);
    }

    public void progressON(String message) {
        BaseApplication.getInstance().progressON(this, message);
    }

    public void progressOFF() {
        BaseApplication.getInstance().progressOFF();
    }

}