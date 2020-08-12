package com.challenge.fidoreader.fagment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.challenge.fidoreader.R;

public class FingerItemFragment extends LinearLayout {

    TextView textview;
    ImageView imageView;

    public FingerItemFragment(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_finger_list, this, true);
        textview = findViewById(R.id.textViewfp);
        imageView = findViewById(R.id.imageViewfp);
    }

    public FingerItemFragment(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setName(String name){
        textview.setText(name);
    }

    public void setImage(int resid){
        imageView.setImageResource(resid);
    }


}

