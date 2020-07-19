package com.challenge.fidoreader.fido;

import android.nfc.tech.IsoDep;
import android.util.Log;
import android.widget.TextView;

import com.challenge.fidoreader.Util.Util;
import com.challenge.fidoreader.fagment.Credential_item;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Authenticator {
    public final static String TAG = "Authenticator";

    public static final byte cp_sub_getPINRetries	            = 0x01;
    public static final byte cp_sub_getKeyAgreement	            = 0x02;
    public static final byte cp_sub_setPIN	                    = 0x03;
    public static final byte cp_sub_changePIN    	            = 0x04;
    public static final byte cp_sub_getPinUvAuthTokenUsingPin	= 0x05;
    public static final byte cp_sub_getPinUvAuthTokenUsingUv	= 0x06;
    public static final byte cp_sub_getUVRetries	            = 0x07;

    public static final byte cm_sub_getCredsMetadata	                  =  0x01 ;
    public static final byte cm_sub_enumerateRPsBegin	                  =  0x02 ;
    public static final byte cm_sub_enumerateRPsGetNextRP	              =  0x03 ;
    public static final byte cm_sub_enumerateCredentialsBegin	          =  0x04 ;
    public static final byte cm_sub_enumerateCredentialsGetNextCredential =	0x05  ;
    public static final byte cm_sub_deleteCredential	                  =  0x06 ;

    IsoDep myTag;

    static byte[] byteAPDU=null;
    static byte[] respAPDU=null;

    Data data;
    FIDO2_API cmd;
    Responses res;

    public Authenticator(IsoDep myTag){
        this.myTag = myTag;
        data = new Data();
    }

    public void setTag(IsoDep myTag) {
        this.myTag = myTag;
    }

    public ArrayList<Credential_item> getCredentialList() throws Exception{
        Log.v(TAG, "getCredentialList");
        ArrayList<Credential_item> list = new ArrayList<Credential_item>();


        bSendAPDU("00A4040008A0000006472F000100");
        getInfo();
        ClientPINparse(bSendAPDU(ClientPIN(Authenticator.cp_sub_getKeyAgreement)));
        ClientPINparse(bSendAPDU(ClientPIN(Authenticator.cp_sub_getPinUvAuthTokenUsingPin)));
        CredentialManagementparse(bSendAPDU(CredentialManagement(Authenticator.cm_sub_enumerateRPsBegin)));
        CredentialManagementparse(bSendAPDU(CredentialManagement(Authenticator.cp_sub_getKeyAgreement)));

        return list;
    }

    public String getInfo() throws Exception{
        Log.v(TAG, "getInfo");

        //  Test - kelee
        String temp_result = bSendAPDU("00A4040008A0000006472F000100");


        Log.v(TAG, "Select COmmand result " + temp_result);

        String result = bSendAPDU("80100000010400");

        Log.v(TAG, "getInfo result " + result);

        /*ByteArrayInputStream bais = new ByteArrayInputStream(Util.atohex(result));

        CBORFactory cf = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(cf);
        try {
            JsonNode jnode = mapper.readValue(bais, JsonNode.class);

        } catch (Exception e) {
            e.printStackTrace();
        }*/


        return result;
    }

    public String getInfo(TextView viewprint) throws Exception{
        Log.v(TAG, "getInfo");

        String resultData = "Start GetInfo Process" + "\n";

        // viewprint.append("00A4040008A0000006472F000100");
        bSendAPDU("00A4040008A0000006472F000100");

        //  viewprint.append(result.);
        //  Test - kelee
        String result = bSendAPDU("80100000010400");

        result = result.substring(2, result.length() - 6);

        ByteArrayInputStream bais = new ByteArrayInputStream(Util.atohex(result));

        CBORFactory cf = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(cf);
        try {
            // JsonNode jnode = mapper.readValue(bais, JsonNode.class);
            CBORParser cborParser = cf.createParser(bais);
            Map<String, Object> responseMap = mapper.readValue(cborParser, new TypeReference<Map<String, Object>>() {
            });

            ArrayList<String> version;
            ArrayList<String> extensions;
            byte[] aaguid;
            LinkedHashMap<String, Boolean> options;
            ArrayList<Integer> pinUvAuthProtocol;

            for (String key : responseMap.keySet()) {
//            System.out.println("key : " + key);
                switch (key) {
                    case "1":
                        version = (ArrayList<String>) responseMap.get(key);
                        resultData += "Version : \n";
                        for(int index = 0 ; index < version.size() ; index++){
                            resultData += "\t[" + version.get(index) + "]\n";
                        }

                        break;
                    case "2":
                        extensions = (ArrayList<String>) responseMap.get(key);
                        resultData += "Extensions : \n";
                        for(int index = 0 ; index < extensions.size() ; index++){
                            resultData += "\t[" + extensions.get(index) + "]\n";
                        }
                        break;
                    case "3":
                        aaguid = (byte[]) responseMap.get(key);
                        resultData += "AAGUID : \n";
                        resultData += "\t[" + Util.getHexString(aaguid) + "]\n";
                        break;
                    case "4":
                        options = (LinkedHashMap<String, Boolean>) responseMap.get(key);
                        resultData += "Opionts : \n";
                        if(options.get("rk")){
                            resultData += "\t[Resident Key] : [지원]\n";
                        }
                        else{
                            resultData += "\t[Resident Key] : [미지원]\n";
                        }
                        if(options.get("up")){
                            resultData += "\t[User Presence] : [지원]\n";
                        }
                        else{
                            resultData += "\t[User Presence] : [미지원]\n";
                        }
                        if(options.get("uv")){
                            resultData += "\t[FingerPrint] : [사용 가능]\n";
                        }
                        else{
                            resultData += "\t[FingerPrint] : [미지원 or 지문 미등록]\n";
                        }
                        if(!options.get("plat")){
                            resultData += "\t[no Platform Device]\n";
                        }
                        else{
                            resultData += "\t[Platform Device]\n";
                        }
                        if(options.get("clientPin")){
                            resultData += "\t[사용자 PIN] : [사용 가능]\n";
                        }
                        else{
                            resultData += "\t[사용자 PIN] : [미지원 or 사용장 PIN 미등록]\n";
                        }
                        if(options.get("credentialMgmtPreview")){
                            resultData += "\t[Credential Management] : [지원]\n";
                        }
                        else{
                            resultData += "\t[Credential Management] : [미지원]\n";
                        }
                        if(!options.get("userVerificationMgmtPreview")){
                            resultData += "\t[FingerPrint Management] : [미지원 or 지문 미등록]\n";
                        }
                        else{
                            resultData += "\t[FingerPrint Management : [지원]\n";
                        }
                        break;
                    case "6":
                        pinUvAuthProtocol = (ArrayList<Integer>) responseMap.get(key);
                        resultData += "PinUvAuthProtocol : \n";
                        for(int index = 0 ; index < pinUvAuthProtocol.size() ; index++){
                            resultData += "\t[" + pinUvAuthProtocol.get(index) + "]\n";
                        }
                        break;
                    case "7":
                        resultData += "지원가능한 Credential 수 : \n";
                        resultData += "\t[" + (Integer) responseMap.get(key) + " bytes]\n";
                        break;
                    case "8":
                        resultData += "CredentialID 길이 : \n";
                        resultData += "\t[" + (Integer) responseMap.get(key) + "]\n";
                        break;
                }
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
//        return "80100000010400";
        return resultData;
    }


    public String ClientPIN(byte sub){
        String result = "";

        switch (sub){
            case cp_sub_getPINRetries              :
                break;
            case cp_sub_getKeyAgreement            :
                return "801000000606A20101020200";
            case cp_sub_setPIN                     :
                break;
            case cp_sub_changePIN                  :
                break;
            case cp_sub_getPinUvAuthTokenUsingPin  :
                break;
            case cp_sub_getPinUvAuthTokenUsingUv   :
                break;
            case cp_sub_getUVRetries               :
                break;
        }

        String pin = "0000";


        return result;
    }


    public String ClientPINparse(String res){
        if(res.equals("")){
            return "";
        }

        return "80100000010400";
    }


    public String CredentialManagement(byte sub){
        String result = "";

        switch (sub){
            case cm_sub_getCredsMetadata	                  :
                break;
            case cm_sub_enumerateRPsBegin	                  :
                break;
            case cm_sub_enumerateRPsGetNextRP	              :
                break;
            case cm_sub_enumerateCredentialsBegin	          :
                break;
            case cm_sub_enumerateCredentialsGetNextCredential :
                break;
            case cm_sub_deleteCredential	                  :
                break;
        }

        String pin = "0000";


        return result;
    }


    public String CredentialManagementparse(String res){
        if(res.equals("")){
            return "";
        }

        return "80100000010400";
    }





    public static void print(String txt){
        Log.v("print", txt);
    }


    private byte[]  transceives (byte[] data){
        Log.v("Authenticator", "transceives");

        byte[] ra = null;

        try{
            print("***COMMAND APDU***");
            print("");
            print("IFD - " + Util.getHexString(data));
        }
        catch (Exception e1){
            e1.printStackTrace();
        }

        try{
            ra = myTag.transceive(data);
        }
        catch (IOException e){

            print("************************************");
            print("         NO CARD RESPONSE");
            print("************************************");

        }
        try{
            print("");
            print("ICC - " + Util.getHexString(ra));
        }
        catch (Exception e1){
            e1.printStackTrace();
        }

        return (ra);
    }

    private String bSendAPDU(String StringAPDU)
    {

        //String StringAPDU = "00A4040008A0000006472F000100";


        byteAPDU = Util.atohex(StringAPDU);
        respAPDU = transceives(byteAPDU);

        /*if(mCheckResp.isChecked())
        {
            try
            {
                vShowResponseInterpretation(respAPDU);
            }
            catch (Exception e)
            {
                clearlog();
                print("Response is not TLV format !!!");
            }

        }*/

        try{
            return Util.getHexString(respAPDU);
        }
        catch (Exception e1){
            e1.printStackTrace();
        }

        return "";
    }


}
