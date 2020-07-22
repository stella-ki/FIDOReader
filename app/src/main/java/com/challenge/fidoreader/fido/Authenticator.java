package com.challenge.fidoreader.fido;

import android.nfc.tech.IsoDep;
import android.util.Log;

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
    /*
    public String getInfo() throws Exception{
        Log.v(TAG, "getInfo");

        //  Test - kelee
        String temp_result = bSendAPDU("00A4040008A0000006472F000100");


        Log.v(TAG, "Select COmmand result " + temp_result);

        String result = bSendAPDU("80100000010400");

        Log.v(TAG, "getInfo result " + result);

        ByteArrayInputStream bais = new ByteArrayInputStream(Util.atohex(result));

        CBORFactory cf = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(cf);
        try {
            JsonNode jnode = mapper.readValue(bais, JsonNode.class);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }
    */

    public String getInfo(){
        Log.v(TAG, "getInfo");

        // String resultData = "Start GetInfo Process" + "\n";

        // viewprint.append("00A4040008A0000006472F000100");
        bSendAPDU("00A4040008A0000006472F000100");

        //  viewprint.append(result.);
        //  Test - kelee
        String result = bSendAPDU("80100000010400");

        result = result.substring(2, result.length() - 6);


//        return "80100000010400";
        return result;
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
