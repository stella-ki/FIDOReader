package com.challenge.fidoreader.fagment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.challenge.fidoreader.MainActivity;
import com.challenge.fidoreader.R;
import com.challenge.fidoreader.Util.Util;


public class ChangePINFragment extends DialogFragment {

    public String originPIN = "";
    public String clientPIN = "";
    private MainActivity activity;

    private View view;
    private EditText originPINText;
    private EditText password1;
    private EditText password2;

    private TextView resultView;

    private Button okbtn;
    private ImageView cancelbtn;

    public ChangePINFragment(MainActivity activity){
        this.activity = activity;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.changepin_popup, null);

        builder.setView(view);

        return builder.create();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Dialog Title 없애기
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.changepin_popup, null);
        getDialog().setContentView(view);

        originPINText = (EditText)view.findViewById(R.id.beforePINEditText);
        password1 = (EditText)view.findViewById(R.id.newPINEditText);
        password2 = (EditText)view.findViewById(R.id.confrimPINEditText);

        resultView = (TextView)view.findViewById(R.id.resultSetView);

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(password1.getText().toString().equals(password2.getText().toString())){
                    resultView.setTextColor(Color.parseColor("#00FF00"));
                    resultView.setText("Matched Password");
                }
                else{
                    resultView.setTextColor(Color.parseColor("#FF0000"));
                    resultView.setText("Not Matched Password");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        okbtn = (Button)view.findViewById(R.id.OKBtn);
        okbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(originPINText.getText().toString().length() > 0) {
                    try {
                        originPIN = Util.ascii(originPINText.getText().toString());
                        clientPIN = Util.ascii(password1.getText().toString());
                        String result = activity.authenticator.changePin(originPIN, clientPIN);

                        if(result.equals("00")) {
                            Toast.makeText(activity.getApplicationContext(), "PIN 변경이 완료되었습니다.", Toast.LENGTH_LONG).show();

                            getDialog().dismiss();
                        }
                        else if(result.equals("31")){
                            Toast.makeText(activity.getApplicationContext(), "기존 PIN 정보가 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(activity.getApplicationContext(), "PIN 변경을 정상적으로 종료하지 못하였습니다.", Toast.LENGTH_LONG).show();

                            getDialog().dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        cancelbtn = (ImageView) view.findViewById(R.id.CancelBtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Toast.makeText(activity.getApplicationContext(), "PIN 변경을 취소하였습니다.", Toast.LENGTH_LONG).show();
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
