package com.challenge.fidoreader.fagment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.challenge.fidoreader.MainActivity;
import com.challenge.fidoreader.R;
import com.challenge.fidoreader.fidoReader.Authenticator;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class FingerEnrollBottomSheetDialog extends BottomSheetDialogFragment {
    public static final String TAG = "FingerEnrollBottomSheetDialog";
    private BottomSheetListener mListener;
    ImageView imageView;

    boolean isCancel = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.v(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.dialog_btm_enroll_sheet_layout, container, false);

        imageView = v.findViewById(R.id.imageView2_fp);
        Button button1 = v.findViewById(R.id.btn_enroll_Cancel);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancel = true;
                Log.v("setOnClickListener", isCancel + "");
                //dismiss();
            }
        });

        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                try{
                    Log.v("setOnShowListener", isVisible() + "");
                    MainActivity.authenticator.setTag(MainActivity.cardReader.myTag);

                    EnrollFingerPrintClass test = new EnrollFingerPrintClass();
                    test.execute(MainActivity.authenticator);

                }catch (Exception e){

                    e.printStackTrace();
                }finally {

                }
            }
        });
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v(TAG, "onStart");
    }

    public interface BottomSheetListener {
        void onButtonClicked(FingerItem list);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(TAG, "onAttach");
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    class EnrollFingerPrintClass extends AsyncTask<Object, Object, Object> {
        public static final String TAG = "EnrollFingerPrintClass";
        FingerItem fingerItem = null;
        public EnrollFingerPrintClass() {
            super();
        }

        @Override
        protected void onPreExecute() {
            Log.v(TAG, "onPreExecute");
        }

        @Override
        protected void onPostExecute(Object s) {
            super.onPostExecute(s);
            Log.v(TAG, "onPostExecute");

            if(fingerItem != null){
                mListener.onButtonClicked(fingerItem);
            }
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Log.v(TAG, "onProgressUpdate : " + values[0].toString());
            imageView.setImageResource(R.drawable.authenticator_key);
            imageView.setImageResource(R.drawable.fingerprint);
        }

        @Override
        protected Object doInBackground(Object... params) {
            Log.v(TAG, "doInBackground");
            Authenticator authenticator = (Authenticator)params[0];


            try {
                authenticator.getPINToken();
                String templateID = authenticator.enrollfinger();
                publishProgress(templateID);
                int count = 1;
                int tryCount = 0;
                while(count != 0 && tryCount != 10){
                    if(isCancel){
                        isCancel = false;
                        authenticator.enrollCancel();
                        dismiss();
                        break;
                    }
                    count = authenticator.enrollNextfinger(templateID);
                    publishProgress(templateID);
                    tryCount++;
                }
                fingerItem = new FingerItem(templateID, "finger" + templateID,0);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return fingerItem;
        }
    }
}