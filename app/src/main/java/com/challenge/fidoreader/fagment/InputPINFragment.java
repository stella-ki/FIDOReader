package com.challenge.fidoreader.fagment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.challenge.fidoreader.MainActivity;
import com.challenge.fidoreader.R;
import com.challenge.fidoreader.Util.Util;
import com.challenge.fidoreader.fidoReader.GoogleTranslate;


public class InputPINFragment extends DialogFragment {

    public String clientPIN = "";
    private MainActivity activity;

    private View view;
    private EditText password1;

    private Button okbtn;
    private ImageView cancelbtn;

    ProgressBar mProgressBar;

    public InputPINFragment(MainActivity activity){
        this.activity = activity;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.inputpin_popup, null);

        builder.setView(view);

        return builder.create();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Dialog Title 없애기
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.inputpin_popup, null);
        getDialog().setContentView(view);

        password1 = (EditText)view.findViewById(R.id.userPINEditText);
        okbtn = (Button)view.findViewById(R.id.OKBtn);

        mProgressBar = (ProgressBar)view.findViewById(R.id.h_progressbar2);
        okbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    clientPIN = Util.ascii(password1.getText().toString());
                    activity.authenticator.setUserPIN(clientPIN);

                    // getDialog().dismiss();

                    GoogleTranslate googleTranslate = new GoogleTranslate(mProgressBar);
                    activity.onChangeFragmentToList(googleTranslate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cancelbtn = (ImageView) view.findViewById(R.id.CancelBtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Toast.makeText(activity.getApplicationContext(), "PIN 입력을 취소하였습니다.", Toast.LENGTH_LONG).show();
                    getDialog().dismiss();

                } catch (Exception e){
                    e.printStackTrace();;
                }
            }
        });

        // 이미지랑 Layout이랑 안겹치는 부분 배경색 없애기
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().show();

        return view;
    }

}
