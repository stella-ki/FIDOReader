package com.challenge.fidoreader.fagment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.challenge.fidoreader.R;

public class CredItemFragment extends LinearLayout {

    TextView textview, textview2;
    ImageView imageView;

    public CredItemFragment(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_item_list, this, true);
        textview = findViewById(R.id.textView);
        textview2 = findViewById(R.id.textView2);
        imageView = findViewById(R.id.imageView);
    }

    public CredItemFragment(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setName(String name){
        textview.setText(name);
    }


    public void setMobile(String name){
        textview2.setText(name);
    }

    public void setImage(int resid){
        imageView.setImageResource(resid);
    }


}

