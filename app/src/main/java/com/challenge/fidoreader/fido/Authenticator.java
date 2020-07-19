package com.challenge.fidoreader.fido;

import android.nfc.tech.IsoDep;
import android.util.Log;

import com.challenge.fidoreader.Util.MapList;
import com.challenge.fidoreader.Util.Util;
import com.challenge.fidoreader.fagment.Credential_item;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECPublicKeySpec;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

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


    SharedSecretObject sso;
    MapList<String, RPs> rps;

    public Authenticator(){
        sso = new SharedSecretObject();
        rps = new MapList<String, RPs>();
    }

    public void setTag(IsoDep myTag) {
        this.myTag = myTag;
    }

    public String getInfo() throws Exception{
        Log.v(TAG, "getInfo");

        sso.init();

        //  Test - kelee
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


        return "80100000010400";
    }

    public JsonNode getInfo_parse(String result) throws Exception{
       // Log.v(TAG, "getInfo");
        ByteArrayInputStream bais = new ByteArrayInputStream(Util.atohex(result));

        CBORFactory cf = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(cf);
        try {
            JsonNode jnode = mapper.readValue(bais, JsonNode.class);
            //Log.v(TAG, "jnode : "+ jnode);
            System.out.println(jnode);
            return jnode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public JsonNode getCBORData(String res) throws Exception{
        ByteArrayInputStream bais = new ByteArrayInputStream(Util.atohex(res));

        CBORFactory cf = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(cf);
        try {
            JsonNode jnode = mapper.readValue(bais, JsonNode.class);
            return jnode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void ClientPIN(byte sub) throws Exception{
        String result = ClientPIN_cmd(sub);
        ClientPINparse(sub, result);
    }


    public String ClientPIN_cmd(byte sub) throws Exception{
        String cmd = "";

        switch (sub){
            case cp_sub_getPINRetries              :
                break;
            case cp_sub_getKeyAgreement            :
                cmd = "801000000606A20101020200";
                break;
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

        String result = bSendAPDU(cmd);


        return result;
    }


    public String ClientPINparse(byte sub, String res) throws Exception{
        JsonNode jnode = getCBORData(res);
        if(res.equals("") || jnode == null){
            return "";
        }

        switch (sub){
            case cp_sub_getPINRetries              :
                break;
            case cp_sub_getKeyAgreement            :

                //get Authenticator public key
                String a_publickey = "";//TODO get authenticator public key

                sso.generateSharedSecret(a_publickey);
                Log.d(TAG, sso.toString());

                return "801000000606A20101020200";
            case cp_sub_setPIN                     :
                break;
            case cp_sub_changePIN                  :
                break;
            case cp_sub_getPinUvAuthTokenUsingPin  :
                String keyAggreement = "A5010203262001215820" + sso.getPublickey().substring(0,64) + "225820" + sso.getPublickey().substring(64);
                String pinHashEnc = Util.aes_cbc(Util.sha_256("0000"), sso.getSharedSecret());

                break;
            case cp_sub_getPinUvAuthTokenUsingUv   :
                break;
            case cp_sub_getUVRetries               :
                break;
        }

        return "80100000010400";
    }

    public ArrayList<Credential_item> getCredentialList() throws Exception{
        Log.v(TAG, "getCredentialList");
        ArrayList<Credential_item> list = new ArrayList<Credential_item>();


        bSendAPDU("00A4040008A0000006472F000100");
        getInfo();
        ClientPIN(Authenticator.cp_sub_getKeyAgreement);
        ClientPIN(Authenticator.cp_sub_getPinUvAuthTokenUsingPin);
        int num = 0;

        CredentialManagement(Authenticator.cm_sub_enumerateRPsBegin);
        for (; num < rps.expectedSize(); num++){
            CredentialManagement(Authenticator.cm_sub_enumerateRPsGetNextRP);
        }

        num = 0;
        String rp = (String) rps.getKey(num++); //get First key value which is rp
        CredentialManagement(Authenticator.cm_sub_enumerateCredentialsBegin, rp);
        for (; num < rps.getSize(); num++){
            rp = (String) rps.getKey(num); //get First key value which is rp
            CredentialManagement(Authenticator.cm_sub_enumerateCredentialsGetNextCredential, rp);
        }


        return list;
    }

    public void CredentialManagement(byte sub, String... param) throws Exception{
        String result = CredentialManagement_cmd(sub, param);
        CredentialManagement_parse(sub, result);
    }

    public String CredentialManagement_cmd(byte sub, String... param) throws Exception{
        String result = "";

        String rp = "";
        String rpIDHash = "";

        switch (sub){
            case cm_sub_getCredsMetadata	                  :
                break;
            case cm_sub_enumerateRPsBegin	                  :
                rps.clear();
                rp = ""; //TODO
                rpIDHash = ""; //TODO
                int totalRpCount = 0;
                rps.add(rp, new RPs(rp, rpIDHash));
                rps.setExpectedSize(totalRpCount);
                break;
            case cm_sub_enumerateRPsGetNextRP	              :
                rp = ""; //TODO
                rpIDHash = ""; //TODO
                rps.add(rp, new RPs(rp, rpIDHash));
                break;
            case cm_sub_enumerateCredentialsBegin	          :
                rp = param[0];


                break;
            case cm_sub_enumerateCredentialsGetNextCredential :
                break;
            case cm_sub_deleteCredential	                  :
                break;
        }

        String pin = "0000";


        return result;
    }


    public String CredentialManagement_parse(byte sub, String res){
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
