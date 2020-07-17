package com.challenge.fidoreader.fagment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.challenge.fidoreader.MainActivity;
import com.challenge.fidoreader.R;

public class ReaderButtonFragment extends Fragment {
    public final static String TAG = "ReaderButtonFragment";


    TextView txtView1;
    TextView txtView2;
    TextView txtView3;
    Button btn;

    MainActivity mainActivity;
    private ReaderButtonViewModel mViewModel;


    public static ReaderButtonFragment newInstance() {
        return new ReaderButtonFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.reader_button_fragment, container, false);

        btn = (Button)view.findViewById(R.id.readerActivationBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onChangeFragment();

            }
        });
        btn.setEnabled(false);

        txtView1 = (TextView)view.findViewById(R.id.readerActivationText1);
        txtView2 = (TextView)view.findViewById(R.id.readerActivationText2);
        txtView3 = (TextView)view.findViewById(R.id.readerActivationText3);

        setTextview1("");
        setTextview2("");
        setTextview3("");

        return view;
    }

    public void setEnabled(){
        Log.v(TAG, "setEnabled");
        btn.setEnabled(true);
    }
    public void setTextview1(String txt){
        Log.v(TAG, "1 : " + txt);
        txtView1.setText(txt);
    }

    public void setTextview2(String txt){
        Log.v(TAG, "2 : " + txt);
        txtView2.setText(txt);
    }


    public void setTextview3(String txt){
        Log.v(TAG, "3 : " + txt);
        txtView3.setText(txt);
    }


    @Override
    public void onResume(){
        super.onResume();
        Log.v(TAG, "onResume");

    }

    @Override
    public void onPause(){
        super.onPause();
        Log.v(TAG, "onPause");

    }


    @Override
    public void onStop(){
        super.onStop();
        Log.v(TAG, "onStop");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
        Log.v(TAG, "onDetach");
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }



}
