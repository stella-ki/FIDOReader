package com.challenge.fidoreader.fagment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.challenge.fidoreader.MainActivity;
import com.challenge.fidoreader.R;
import com.challenge.fidoreader.Util.Util;

public class InputNameFragment extends DialogFragment {

    OnDialogResult mresult;

   // private MainActivity activity;

    private View view;
    private EditText password1;

    private Button okbtn;
    private ImageView cancelbtn;

   public InputNameFragment(){
    }

    public InputNameFragment(MainActivity activity, String buttonType){
       // this.activity = activity;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.inputname_popup, null);

        builder.setView(view);

        return builder.create();

    }

    public void setDialogResult(OnDialogResult mresult){
       this.mresult = mresult;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Dialog Title 없애기
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.inputname_popup, null);
        getDialog().setContentView(view);

        password1 = (EditText)view.findViewById(R.id.newFingerName);
        okbtn = (Button)view.findViewById(R.id.OKBtn);

        // mProgressBar.setVisibility(View.INVISIBLE);
        okbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    if(mresult != null){
                        mresult.finish(password1.getText().toString());
                    }
                    getDialog().dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cancelbtn = (ImageView) view.findViewById(R.id.CancelBtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mresult != null){
                    mresult.finish("NO");
                }
                try{
                    //Toast.makeText(activity.getApplicationContext(), "PIN 입력을 취소하였습니다.", Toast.LENGTH_LONG).show();
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

    interface OnDialogResult{
       void finish(String result);
    }

}
