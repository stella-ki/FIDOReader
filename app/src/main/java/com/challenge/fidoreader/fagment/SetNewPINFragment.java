package com.challenge.fidoreader.fagment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.challenge.fidoreader.MainActivity;
import com.challenge.fidoreader.R;
import com.challenge.fidoreader.Util.Util;


public class SetNewPINFragment extends DialogFragment {
    /*
    public static SetNewPINFragment newInstance(String title){
        SetNewPINFragment frag = new SetNewPINFragment();

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        frag.setArguments(bundle);

        return frag;
    }
    */

    public String clientPIN = "";
    private MainActivity activity;
    private View view;

    public SetNewPINFragment(MainActivity activity, View view){
        this.activity = activity;
        this.view = view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.setnewpin_popup, null);

        final EditText password1 = (EditText)view.findViewById(R.id.newPINEditText);
        final EditText password2 = (EditText)view.findViewById(R.id.confrimPINEditText);

        final TextView resultView = (TextView)view.findViewById(R.id.resultSetView);

        final Button okbtn = (Button)view.findViewById(R.id.OKBtn);
        final Button cancelbtn = (Button)view.findViewById(R.id.CancelBtn);

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(password1.getText().toString().equals(password2.getText().toString())){
                    resultView.setTextColor(Color.parseColor("#00FF00"));
                    resultView.setText("Matched Passord");
                }
                else{
                    resultView.setTextColor(Color.parseColor("#FF0000"));
                    resultView.setText("Not Matched Passord");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        builder.setTitle("사용자 PIN 설정하기").setView(view);
        /*.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    clientPIN = Util.ascii(password1.getText().toString());
                    activity.authenticator.setPIN(clientPIN);

                    Toast.makeText(activity.getApplicationContext(), "PIN 설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(activity.getApplicationContext(), "PIN 설정을 취소하였습니다.", Toast.LENGTH_LONG).show();
            }
        });*/
        okbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    clientPIN = Util.ascii(password1.getText().toString());
                    String result = activity.authenticator.setPIN(clientPIN);
                    if(result.equals("00")) {
                        Toast.makeText(activity.getApplicationContext(), "PIN 설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(activity.getApplicationContext(), "PIN 설정을 정상적으로 완료하지 못하였습니다..", Toast.LENGTH_LONG).show();
                    }

                    getDialog().dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(activity.getApplicationContext(), "PIN 설정을 취소하였습니다.", Toast.LENGTH_LONG).show();

                    getDialog().dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return builder.create();
    }

}
