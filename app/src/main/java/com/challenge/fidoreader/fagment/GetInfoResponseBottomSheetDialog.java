package com.challenge.fidoreader.fagment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.challenge.fidoreader.MainActivity;
import com.challenge.fidoreader.R;
import com.challenge.fidoreader.Util.Util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class GetInfoResponseBottomSheetDialog extends BottomSheetDialogFragment {

    private Button clientpinbtn;

    private ImageView cancelbtn;

    private Boolean hasclientPIN = false;

    private MainActivity mainActivity;

    public GetInfoResponseBottomSheetDialog(Button clientpinbtn, MainActivity mainActivity){
        this.clientpinbtn = clientpinbtn;
        this.mainActivity = mainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_btm_getinfo_sheet_layout, container, false);

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

//        cancelbtn = (ImageView) view.findViewById(R.id.CancelBtn);
//        cancelbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try{
//                    getDialog().dismiss();
//
//                } catch (Exception e){
//                    e.printStackTrace();;
//                }
//            }
//        });

        // 이미지랑 Layout이랑 안겹치는 부분 배경색 없애기
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getDialog().show();


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    private void initTable(View view) throws Exception {
        TableLayout getInfoTable = (TableLayout)view.findViewById(R.id.KonaBIOPASSGetInfoTable);

//        TableRow row = (TableRow)view.findViewById(R.id.konaBIOPASSGetInfoRow);
        TableRow row = new TableRow(getActivity());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(layoutParams);
        row.setBackgroundColor(Color.parseColor("#FF9898"));

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

    private TableRow generateRow(String key, String value, boolean isOdd) {
        TableRow tr = new TableRow((getActivity()));
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        if (isOdd) {
            tr.setBackgroundColor(getResources().getColor(R.color.test));
        }
        TextView keyView = new TextView((getActivity()));
        TextView valueView = new TextView((getActivity()));

        keyView.setTypeface(null, Typeface.BOLD);
        keyView.setPadding(30, 20, 30, 20);
        //keyView.setTextSize(15);
        keyView.setText(key);
        valueView.setText(value);
        valueView.setSingleLine(false);
        valueView.setMaxLines(20);
        valueView.setPadding(0, 20, 30, 20);
        tr.addView(keyView);

        // Set width to zero and weight to 1
        tr.addView(valueView,new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1f));

        return tr;
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
            boolean seq = true;
            for (String key : responseMap.keySet()) {
//            System.out.println("key : " + key);
                //TableRow tableRow = new TableRow((getActivity()));
                //TextView functionRow = new TextView(getActivity());
                //TextView valuewRow = new TextView(getActivity());
                //valuewRow.setWidth(600);
                String menu = "";
                String value = "";

                switch (key) {
                    case "1":
                        version = (ArrayList<String>) responseMap.get(key);
                        // resultData += "Version : \n";
                        //functionRow.setText("Version");
                        menu = "Version";
                        result = "";
                        for(int index = 0 ; index < version.size() ; index++){
                            // resultData += "\t[" + version.get(index) + "]\n";
                            result += version.get(index) + "\n";
                        }

                        break;
                    case "2":
                        extensions = (ArrayList<String>) responseMap.get(key);
                        // resultData += "Extensions : \n";
                        //functionRow.setText("Exnesions");
                        menu = "Exnesions";
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
                        //functionRow.setText("AAGUID");
                        menu = "AAGUID";
                        result = Util.getHexString(aaguid);
                        break;
                    case "4":
                        options = (LinkedHashMap<String, Boolean>) responseMap.get(key);
                        // resultData += "Opionts : \n";
                        //functionRow.setText("Options");
                        menu = "Options";
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
                        //functionRow.setText("PinUvAuthProtocol");
                        menu = "PinUvAuthProtocol";
                        result = "";
                        for(int index = 0 ; index < pinUvAuthProtocol.size() ; index++){
                            // resultData += "\t[" + pinUvAuthProtocol.get(index) + "]\n";
                            result += pinUvAuthProtocol.get(index);
                        }
                        break;
                    case "7":
                        // resultData += "지원가능한 Credential 수 : \n";
                        //functionRow.setText("지원 가능한\nCredential 수");
                        menu = "지원 가능한\nCredential 수";
                        // resultData += "\t[" + (Integer) responseMap.get(key) + " bytes]\n";
                        result = ((Integer) responseMap.get(key)).toString();
                        break;
                    case "8":
                        // resultData += "CredentialID 길이 : \n";
                        //functionRow.setText("CredentialID 길이");
                        menu = "CredentialID 길이";
                        // resultData += "\t[" + (Integer) responseMap.get(key) + "]\n";
                        result = ((Integer) responseMap.get(key)).toString();
                        break;
                }
                //tableRow.addView(functionRow);

                //valuewRow.setText(result);
                //tableRow.addView(valuewRow);

                TableRow tableRow = generateRow(menu, result, seq);
                seq = !seq;
                getInfoTable.addView(tableRow);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}