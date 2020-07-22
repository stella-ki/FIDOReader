package com.challenge.fidoreader.fagment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.challenge.fidoreader.MainActivity;
import com.challenge.fidoreader.R;

import org.w3c.dom.Text;

public class ReaderButtonFragment extends Fragment {
    public final static String TAG = "ReaderButtonFragment";


    ImageView imageView;
    TextView txtView2;
    TextView txtView3;
    Button btn;
    Button getInfobtn;
    TextView getInfoText;

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
                mainActivity.onChangeFragmentToList();

            }
        });
        btn.setEnabled(false);

        getInfobtn = (Button)view.findViewById(R.id.KonaBIOPASSGetInfoBtn);
        getInfobtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });
        getInfobtn.setEnabled(false);


        imageView = (ImageView)view.findViewById(R.id.readerActivationImageView);
        txtView2 = (TextView)view.findViewById(R.id.readerActivationText2);
        txtView3 = (TextView)view.findViewById(R.id.readerActivationText3);

        getInfoText = (TextView)view.findViewById(R.id.KonaBIOPASSGetInfoTextView);
        getInfoText.setText("GET INFO RESPONSE");

        setImageView(R.drawable.ic_icc_off);
        setTextview2("");
        setTextview3("");

        return view;
    }

    public void setEnabled(){
        Log.v(TAG, "setEnabled");
        btn.setEnabled(true);
        getInfobtn.setEnabled(true);
    }
    public void setImageView(int resource){
        imageView.setImageResource(resource);
    }

    public void setTextview2(String txt){
        txtView2.setText(txt);
    }


    public void setTextview3(String txt){
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
