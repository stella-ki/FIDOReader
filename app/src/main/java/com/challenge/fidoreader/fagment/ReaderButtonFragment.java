package com.challenge.fidoreader.fagment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.challenge.fidoreader.Exception.UserException;
import com.challenge.fidoreader.MainActivity;
import com.challenge.fidoreader.R;
import com.challenge.fidoreader.Util.Util;
import com.challenge.fidoreader.fidoReader.Authenticator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReaderButtonFragment extends Fragment {
    public final static String TAG = "ReaderButtonFragment";

    private ProgressBar pgsBar;

    ImageView imageView;
    TextView txtView2;
    TextView txtView3;

    Button getCredListbtn;
    Button getInfobtn;
    Button clientpinbtn;
    Button enrollManageBtn;
    Button resetBtn;

    GetInfoResponseBottomSheetDialog getinfoSheet;
    Map<String, Object> getInfoResponse;

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
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.fragment_reader_button, container, false);

        getCredListbtn = (Button)view.findViewById(R.id.readerActivationBtn);
        getCredListbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "onClick");
                try{
                    InputPINFragment inputPINFragment = new InputPINFragment(mainActivity, "Credential");
                    inputPINFragment.show(getFragmentManager(), "dialog");
                    inputPINFragment.setCancelable(false);
                    inputPINFragment.setDialogResult(new InputPINFragment.OnDialogResult() {
                        @Override
                        public void finish(String result) {
                            if(result.equals("OK")){
                                GoogleTranslate googleTranslate = new GoogleTranslate();
                                googleTranslate.execute();
                            }
                        }
                    });


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        getCredListbtn.setEnabled(false);

        getInfobtn = (Button)view.findViewById(R.id.KonaBIOPASSGetInfoBtn);
        clientpinbtn = (Button)view.findViewById(R.id.KonaBIOPASSPINBtn);
        clientpinbtn.setEnabled(false);

        getInfobtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getinfoSheet = new GetInfoResponseBottomSheetDialog(clientpinbtn, mainActivity, getInfoResponse);
                getinfoSheet.show(getActivity().getSupportFragmentManager(), "exampleBottomSheet");

            }
        });
        // getInfobtn.setEnabled(false);

        clientpinbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // Change PIN or
                // Set New PIN
                DialogFragment pinFragment = null;
                if(clientpinbtn.getText().toString().equals("Set New PIN")) {
                    pinFragment = new SetNewPINFragment(mainActivity);
                }
                else if(clientpinbtn.getText().toString().equals("Change PIN")) {
                    pinFragment = new ChangePINFragment(mainActivity);
                }

                pinFragment.show(getFragmentManager(), "dialog");
                pinFragment.setCancelable(false);
                try {
                    mainActivity.authenticator.setTag(mainActivity.cardReader.myTag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageView = (ImageView)view.findViewById(R.id.readerActivationImageView);
        txtView2 = (TextView)view.findViewById(R.id.readerActivationText2);
        txtView3 = (TextView)view.findViewById(R.id.readerActivationText3);

        enrollManageBtn = view.findViewById(R.id.EnrollBtn);

        enrollManageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "onClick");
                try{
                    InputPINFragment inputPINFragment = new InputPINFragment(mainActivity, "Fingerprint");
                    inputPINFragment.show(getFragmentManager(), "dialog");
                    inputPINFragment.setCancelable(false);

                    inputPINFragment.setDialogResult(new InputPINFragment.OnDialogResult() {
                        @Override
                        public void finish(String result) {
                            if(result.equals("OK")){
                                GetEnrollInformation googleTranslate = new GetEnrollInformation();
                                googleTranslate.execute();
                            }
                        }
                    });


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        resetBtn = view.findViewById(R.id.resetBtn);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "resetBtn");
                try{
                    ResetProcess googleTranslate = new ResetProcess();
                    googleTranslate.execute();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        pgsBar = (ProgressBar) view.findViewById(R.id.h_progressbar);
        //pgsBar.setIndeterminate(true);
        pgsBar.setVisibility(View.GONE);

        try {
            setResult(MainActivity.cardReader.result_image,
                    MainActivity.cardReader.result1_str,
                    MainActivity.cardReader.result2_str,
                    MainActivity.cardReader.result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    public boolean isReady(){
        if(imageView != null && txtView2 != null && txtView3 != null){
            Log.v(TAG, "Fragment is ready");
            return true;
        }
        Log.v(TAG, "Fragment is not ready");
        return false;
    }


    public void setEnabled() throws Exception {
        Log.v(TAG, "setEnabled");
        getCredListbtn.setEnabled(true);

        getInfobtn.setEnabled(true);
        getInfoResponse = getInfo();
        clientpinbtn.setEnabled(true);
    }
    public void setResult(int resource, String str1, String str2){
        imageView.setImageResource(resource);
        txtView2.setText(str1);
        txtView3.setText(str2);
    }


    public void setResult(int resource, String str1, String str2, boolean result) throws Exception {
        setResult(resource, str1, str2);
        if(result){
            setEnabled();
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        Log.v(TAG, "onResume");
        setResult(MainActivity.cardReader.result_image, MainActivity.cardReader.result1_str, MainActivity.cardReader.result2_str);
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

    class GoogleTranslate extends AsyncTask<Object, Object, Object> {
        ArrayList<CredentialItem> list = null;

        @Override
        protected Object doInBackground(Object... params) {
            Log.v("translate", "doInBackground");

            try {
                Authenticator authenticator = mainActivity.authenticator;
                authenticator.setTag(mainActivity.cardReader.myTag);
                list = authenticator.getCredentialList();
            }
            catch (Exception e) {
                if(e.getMessage().equals("2E")){
                    list = new ArrayList<CredentialItem>();
                }
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Log.v("translate", "\n"+values[0].toString()+"번 count했습니다.");
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("translate", "onPreExecute");
            pgsBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object s) {
            super.onPostExecute(s);
            Log.v("translate", "onPostExecute");
            pgsBar.setVisibility(View.GONE);
            mainActivity.onChangeFragmentToList(list);
        }

    }

    class GetEnrollInformation extends AsyncTask<Object, Object, Object> {

        ArrayList<FingerItem> list = null;
        @Override
        protected Object doInBackground(Object... params) {
            Log.v("GetEnrollInformation", "doInBackground");
            try {
                Authenticator authenticator = mainActivity.authenticator;
                authenticator.setTag(mainActivity.cardReader.myTag);
                list = authenticator.readEnrollInformation();
            }
            catch (Exception e) {
                Log.v("GetEnrollInformation", "" + e.getMessage());
                if(e.getMessage().equals("2C")){
                    list = new ArrayList<FingerItem>();
                }
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Log.v("translate", "\n"+values[0].toString()+"번 count했습니다.");
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("translate", "onPreExecute");
            pgsBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object s) {
            super.onPostExecute(s);
            Log.v("translate", "onPostExecute");
            pgsBar.setVisibility(View.GONE);
            mainActivity.onChangeFragmentToList2(list);

        }

    }


    class ResetProcess extends AsyncTask<Object, Object, Object> {

        boolean result = true;
        @Override
        protected Object doInBackground(Object... params) {
            Log.v("ResetProcess", "doInBackground");
            try {
                Authenticator authenticator = mainActivity.authenticator;
                authenticator.setTag(mainActivity.cardReader.myTag);
                result = authenticator.reset();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("ResetProcess", "onPreExecute");
            pgsBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object s) {
            super.onPostExecute(s);
            Log.v("ResetProcess", "onPostExecute");
            pgsBar.setVisibility(View.GONE);
            Toast.makeText(mainActivity,"정상종료", Toast.LENGTH_SHORT).show();

        }

    }



    private Map<String, Object> getInfo() throws Exception {
        String result = null;

        mainActivity.authenticator.setTag(mainActivity.cardReader.myTag);
        result = mainActivity.authenticator.getInfo();

        ByteArrayInputStream bais = new ByteArrayInputStream(Util.atohex(result));

        CBORFactory cf = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(cf);

        CBORParser cborParser = null;
        cborParser = cf.createParser(bais);
        Map<String, Object> responseMap = null;
        responseMap = mapper.readValue(cborParser, new TypeReference<Map<String, Object>>() {
        });

        for (String key : responseMap.keySet()) {
            if (key.equals("4")) {
                LinkedHashMap<String, Boolean> options = (LinkedHashMap<String, Boolean>) responseMap.get(key);

                if(options.get("clientPin")){
                    clientpinbtn.setText("Change PIN");
                }
                else{
                    clientpinbtn.setText("Set New PIN");
                }
            }
        }

        return responseMap;
    }
}
