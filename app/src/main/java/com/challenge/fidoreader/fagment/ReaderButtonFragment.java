package com.challenge.fidoreader.fagment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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

    boolean hasclientPIN = false;

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

                try {
                    initTable(view);

                    if(hasclientPIN){
                        clientpinbtn.setText("Change PIN");
                    }
                    else{
                        clientpinbtn.setText("Set PIN");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        getInfobtn.setEnabled(false);

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

        getInfo();
        getInfobtn.setEnabled(true);
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

    private void initTable(View view) throws Exception {
        TableLayout getInfoTable = (TableLayout)view.findViewById(R.id.KonaBIOPASSGetInfoTable);

//        TableRow row = (TableRow)view.findViewById(R.id.konaBIOPASSGetInfoRow);
        TableRow row = new TableRow(getActivity());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(layoutParams);

        TextView functionRow = new TextView(getActivity());
        functionRow.setGravity(Gravity.CENTER);
        functionRow.setText("기능");
        row.addView(functionRow);

        TextView supported = new TextView(getActivity());
        supported.setGravity(Gravity.CENTER);
        supported.setText("값");
        row.addView(supported);

        getInfoTable.addView(row);

        getInfoPrint(getInfoTable);


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

    private void getInfoPrint(TableLayout getInfoTable) throws Exception {
        try {
            // JsonNode jnode = mapper.readValue(bais, JsonNode.class);

            Map<String, Object> responseMap = getInfo();
            String result = "";

            ArrayList<String> version;
            ArrayList<String> extensions;
            byte[] aaguid;
            LinkedHashMap<String, Boolean> options;
            ArrayList<Integer> pinUvAuthProtocol;

            for (String key : responseMap.keySet()) {
//            System.out.println("key : " + key);
                TableRow tableRow = new TableRow((getActivity()));
                TextView functionRow = new TextView(getActivity());
                TextView valuewRow = new TextView(getActivity());
                valuewRow.setWidth(600);

                switch (key) {
                    case "1":
                        version = (ArrayList<String>) responseMap.get(key);
                        // resultData += "Version : \n";
                        functionRow.setText("Version");
                        result = "";
                        for(int index = 0 ; index < version.size() ; index++){
                            // resultData += "\t[" + version.get(index) + "]\n";
                            result += version.get(index) + "\n";
                        }

                        break;
                    case "2":
                        extensions = (ArrayList<String>) responseMap.get(key);
                        // resultData += "Extensions : \n";
                        functionRow.setText("Exnesions");
                        result = "";
                        for(int index = 0 ; index < extensions.size() ; index++){
                            // resultData += "\t[" + extensions.get(index) + "]\n";
                            result += extensions.get(index) + "\n";
                        }
                        break;
                    case "3":
                        aaguid = (byte[]) responseMap.get(key);
                        // resultData += "AAGUID : \n";
                        // resultData += "\t[" + Util.getHexString(aaguid) + "]\n";
                        functionRow.setText("AAGUID");
                        result = Util.getHexString(aaguid);
                        break;
                    case "4":
                        options = (LinkedHashMap<String, Boolean>) responseMap.get(key);
                        // resultData += "Opionts : \n";
                        functionRow.setText("Options");

                        result = "";
                        if(options.get("rk")){
                            // resultData += "\t[Resident Key] : [지원]\n";
                            result += "Resident Key 지원\n";
                        }
                        else{
                            // resultData += "\t[Resident Key] : [미지원]\n";
                            result += "Resident Key 미지원\n";
                        }
                        if(options.get("up")){
                            // resultData += "\t[User Presence] : [지원]\n";
                            result += "User Presence 지원\n";
                        }
                        else{
                            // resultData += "\t[User Presence] : [미지원]\n";
                            result += "User Presence 미지원\n";
                        }
                        if(options.get("uv")){
                            // resultData += "\t[FingerPrint] : [사용 가능]\n";
                            result += "FingerPrint\n:[사용 가능]\n";
                        }
                        else{
                            // resultData += "\t[FingerPrint] : [미지원 or 지문 미등록]\n";
                            result += "FingerPrint\n:[미지원 or 지문 미등록]\n";

                        }
                        if(!options.get("plat")){
                            // resultData += "\t[no Platform Device]\n";
                            result += "no Platform Device\n";
                        }
                        else{
                            // resultData += "\t[Platform Device]\n";
                            result += "Platform Device\n";
                        }
                        if(options.get("clientPin")){
                            // resultData += "\t[사용자 PIN] : [사용 가능]\n";
                            result += "사용자 PIN\n:[사용 가능]\n";
                            hasclientPIN = true;
                        }
                        else{
                            // resultData += "\t[사용자 PIN] : [미지원 or 사용자 PIN 미등록]\n";
                            result += "사용자 PIN\n:[미지원 or 사용자 PIN 미등록]\n";
                        }
                        if(options.get("credentialMgmtPreview")){
                            // resultData += "\t[Credential Management] : [지원]\n";
                            result += "Credential Management 지원\n";
                        }
                        else{
                            // resultData += "\t[Credential Management] : [미지원]\n";
                            result += "Credential Management 미지원\n";
                        }
                        if(!options.get("userVerificationMgmtPreview")){
                            // resultData += "\t[FingerPrint Management] : [미지원 or 지문 미등록]\n";
                            result += "FingerPrint Management\n:[미지원 or 지문 미등록]";
                        }
                        else{
                            // resultData += "\t[FingerPrint Management : [지원]\n";
                            result += "FingerPrint Management\n:[지원]";
                        }
                        break;
                    case "6":
                        pinUvAuthProtocol = (ArrayList<Integer>) responseMap.get(key);
                        // resultData += "PinUvAuthProtocol : \n";
                        functionRow.setText("PinUvAuthProtocol");

                        result = "";
                        for(int index = 0 ; index < pinUvAuthProtocol.size() ; index++){
                            // resultData += "\t[" + pinUvAuthProtocol.get(index) + "]\n";
                            result += pinUvAuthProtocol.get(index);
                        }
                        break;
                    case "7":
                        // resultData += "지원가능한 Credential 수 : \n";
                        functionRow.setText("지원 가능한 Credential 수");

                        // resultData += "\t[" + (Integer) responseMap.get(key) + " bytes]\n";
                        result = ((Integer) responseMap.get(key)).toString();
                        break;
                    case "8":
                        // resultData += "CredentialID 길이 : \n";
                        functionRow.setText("CredentialID 길이");
                        // resultData += "\t[" + (Integer) responseMap.get(key) + "]\n";
                        result = ((Integer) responseMap.get(key)).toString();
                        break;
                }
                tableRow.addView(functionRow);

                valuewRow.setText(result);
                tableRow.addView(valuewRow);
                getInfoTable.addView(tableRow);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
            if(list != null){
                mainActivity.onChangeFragmentToList(list);
            }
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
            if(list != null){
                mainActivity.onChangeFragmentToList2(list);
            }
        }

    }
}
