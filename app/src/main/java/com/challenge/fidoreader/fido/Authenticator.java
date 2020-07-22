package com.challenge.fidoreader.fido;

import android.nfc.tech.IsoDep;
import android.util.Log;

import com.challenge.fidoreader.Exception.UserException;
import com.challenge.fidoreader.Util.MapList;
import com.challenge.fidoreader.Util.Util;
import com.challenge.fidoreader.fagment.Credential_item;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Authenticator {
    public final static String TAG = "Authenticator";
    public final static boolean LOG_MODE = false;

    public static final byte cp_sub_getPINRetries	            = 0x01;
    public static final byte cp_sub_getKeyAgreement	            = 0x02;
    public static final byte cp_sub_setPIN	                    = 0x03;
    public static final byte cp_sub_changePIN    	            = 0x04;
    public static final byte cp_sub_getPinUvAuthTokenUsingPin	= 0x05;
    public static final byte cp_sub_getPinUvAuthTokenUsingUv	= 0x06;
    public static final byte cp_sub_getUVRetries	            = 0x07;

    public static final String cm_sub_getCredsMetadata	                    =  "1" ;
    public static final String cm_sub_enumerateRPsBegin	                    =  "2" ;
    public static final String cm_sub_enumerateRPsGetNextRP	                =  "3" ;
    public static final String cm_sub_enumerateCredentialsBegin	            =  "4" ;
    public static final String cm_sub_enumerateCredentialsGetNextCredential =  "5" ;
    public static final String cm_sub_deleteCredential	                    =  "6" ;

    IsoDep myTag;

    static byte[] byteAPDU=null;
    static byte[] respAPDU=null;
    static String sw = "";


    SharedSecretObject sso;
    MapList<String, RPs> rps;
    String pinUvAuthToken;

    public Authenticator(){
        sso = new SharedSecretObject();
        pinUvAuthToken = "";
        rps = new MapList<String, RPs>();
    }

    public void setTag(IsoDep myTag) {
        this.myTag = myTag;
    }

    public String getInfo() throws Exception{
        printLog("getInfo");

        sso.init();
        pinUvAuthToken = "";

        //  Test - kelee
        String result = bSendAPDU("80100000010400");

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
       // printLog(TAG, "getInfo");
        ByteArrayInputStream bais = new ByteArrayInputStream(Util.atohex(result));

        CBORFactory cf = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(cf);
        try {
            JsonNode jnode = mapper.readValue(bais, JsonNode.class);
            //printLog(TAG, "jnode : "+ jnode);
            printLog(jnode);
            return jnode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public JsonNode getCBORDataFromResponse(String res) throws Exception{
        //printLog(res);
        res = res.replaceAll(" ", "");
        res = res.substring(2);

        //printLog(res);
        return getCBORData(res);
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
        if(result.substring(0,2).equals("00")){
            ClientPINparse(sub, result);
        }else{
            printLog("ClientPIN is not successful");
        }

    }

    public void assertSW(String sw) throws Exception{
        if(!sw.equals(getSW())){
            throw new Exception("Excepted SW [ " + sw + " ], and return SW [ " + getSW() + " ]");
        }
    }


    public ArrayList<Credential_item> getCredentialList() throws Exception{
        printLog("getCredentialList");
        ArrayList<Credential_item> list = new ArrayList<Credential_item>();


        bSendAPDU("00A4040008A0000006472F000100");
        assertSW("9000");

        getInfo();
        assertSW("9000");

        ClientPIN(Authenticator.cp_sub_getKeyAgreement);
        assertSW("9000");

        ClientPIN(Authenticator.cp_sub_getPinUvAuthTokenUsingPin);
        assertSW("9000");

        int num = 0;

        String fido_result = CredentialManagement(Authenticator.cm_sub_enumerateRPsBegin);
        if(fido_result.equals("2E")){
            throw new UserException("Credential is not exist");
        }
        for (; num < rps.expectedSize(); num++){
            CredentialManagement(Authenticator.cm_sub_enumerateRPsGetNextRP);
        }

        String rp = ""; //get First key value which is rp
        for (num = 0; num < rps.getSize(); num++){
            rp = (String) rps.getKey(num); //get First key value which is rp
            printLog("Read Credential about RP : " + rp);
            CredentialManagement(Authenticator.cm_sub_enumerateCredentialsBegin, rp);
            int credCunt = rps.get(rp).getCredentialExpectedCnt();
            for (int j = 0; j < credCunt; j++){
                CredentialManagement(Authenticator.cm_sub_enumerateCredentialsGetNextCredential, rp);
            }
        }

        for (num = 0; num < rps.getSize(); num++){
            RPs tmprps = (RPs)rps.getValue(num);
            for (int j = 0; j < tmprps.getCredentials().size(); j++){
                list.add(new Credential_item(tmprps.getCredential(j).getCredentialID(), tmprps.getRp()));
            }
        }
        return list;
    }

    public String ClientPIN_cmd(byte sub) throws Exception{
        String cmd = "";

        switch (sub){
            case cp_sub_getPINRetries              :
                printLog("Send Client PIN : " + "getPINRetries");
                break;
            case cp_sub_getKeyAgreement            :
                printLog("Send Client PIN : " + "getKeyAgreement");
                cmd = "06A20101020200";
                break;
            case cp_sub_setPIN                     :
                printLog("Send Client PIN : " + "setPIN");
                break;
            case cp_sub_changePIN                  :
                printLog("Send Client PIN : " + "changePIN");
                break;
            case cp_sub_getPinUvAuthTokenUsingPin  :
                printLog("Send Client PIN : " + "getPinUvAuthTokenUsingPin");
                String keyAgreement = "A5010203262001215820" + sso.getPublickey().substring(0,64) + "225820" + sso.getPublickey().substring(64);
                String sha = Util.sha_256("30303030").substring(0, 16 * 2);
                printLog("sha : " + sha);
                String pinHashEnc = Util.aes_cbc(sso.getSharedSecret(), sha);
                printLog("pinHashEnc : " + pinHashEnc);
                cmd = "A4"
                        + "01" + "01" //pinUvAuthProtocol
                        + "02" + "05" // subCommand index
                        + "03" + keyAgreement //keyAgreement
                        + "06" + "58"+ Util.toHex(pinHashEnc.length()/2) + pinHashEnc; //pinHashEnc
                cmd = "06" + cmd;
                break;
            case cp_sub_getPinUvAuthTokenUsingUv   :
                printLog("Send Client PIN : " + "getPinUvAuthTokenUsingUv");
                break;
            case cp_sub_getUVRetries               :
                printLog("Send Client PIN : " + "getUVRetries");
                break;
        }

        String pin = "0000";

        String result = makeCommand(cmd);
        //printLog(cmd);
        printLog(result);

        return result;
    }


    public String ClientPINparse(byte sub, String res) throws Exception{
        JsonNode jnode = getCBORDataFromResponse(res);

        if(res.equals("") || jnode == null){
            return "";
        }

        switch (sub){
            case cp_sub_getPINRetries              :
                break;
            case cp_sub_getKeyAgreement            :

                //get Authenticator public key
                String a_publickey = Util.getHexString(jnode.get("1").get("-2").binaryValue())
                        + Util.getHexString(jnode.get("1").get("-3").binaryValue());//TODO get authenticator public key

                sso.generateSharedSecret(a_publickey);
                printLog(sso.toString());

                return "801000000606A20101020200";
            case cp_sub_setPIN                     :
                break;
            case cp_sub_changePIN                  :
                break;
            case cp_sub_getPinUvAuthTokenUsingPin  :
                String encPINUvAuthToken = Util.getHexString(jnode.get("2").binaryValue());//TODO
                //printLog(encPINUvAuthToken);
                pinUvAuthToken = Util.aes_cbc_dec(sso.getSharedSecret(), encPINUvAuthToken);
                //printLog(pinUvAuthToken);
                break;
            case cp_sub_getPinUvAuthTokenUsingUv   :
                break;
            case cp_sub_getUVRetries               :
                break;
        }

        return "80100000010400";
    }


    public String CredentialManagement(String sub, String... param) throws Exception{
        String fido_result = "";
        String result = CredentialManagement_cmd(sub, param);
        fido_result = result.substring(0,2);
        if(fido_result.equals("00")){
            CredentialManagement_parse(sub, result, param);
        }else{
            printLog("CredentialManagement is not successful");
        }

        return fido_result;
    }

    public String CredentialManagement_cmd(String sub, String... param) throws Exception{
        String pinUvAuthParam = "";
        String cmd = "";

        String rp = "";
        String rpIDHash = "";
        
        switch (sub){
            case cm_sub_getCredsMetadata	                  :
                printLog("Send Credential Management : "+"getCredsMetadata");
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "01").substring(0, 16*2);
                cmd = "A3" 
                    + "01" + "01" //subcommand index
                    + "03" + "01" // pinUvAuthProtocol
                    + "04" + "58"+ Util.toHex(pinUvAuthParam.length()/2) + pinUvAuthParam; //pinUvAuthParam
                cmd = "41" + cmd;
                break;
            case cm_sub_enumerateRPsBegin	                  :
                printLog("Send Credential Management : "+"enumerateRPsBegin");
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "02").substring(0, 16*2);
                cmd = "A3" 
                    + "01" + "02" //subcommand index
                    + "03" + "01" // pinUvAuthProtocol
                    + "04" + "58"+ Util.toHex(pinUvAuthParam.length()/2) + pinUvAuthParam; //pinUvAuthParam
                cmd = "41" + cmd;
                break;
            case cm_sub_enumerateRPsGetNextRP	              :
                printLog("Send Credential Management : "+"enumerateRPsGetNextRP");
                cmd = "A1" 
                    + "01" + "03"; //subcommand index
                cmd = "41" + cmd;
                break;
            case cm_sub_enumerateCredentialsBegin	          :
                printLog("Send Credential Management : "+"enumerateCredentialsBegin");
                rp = param[0];
                rp = Util.convertTohex(rp);
                rpIDHash = Util.sha_256(rp);
                rpIDHash = "A10158" + Util.toHex(rpIDHash.length()/2) + rpIDHash;
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "04" + rpIDHash).substring(0, 16*2);
                
                cmd = "A4" 
                    + "01" + "04" //subcommand index
                    + "02" + rpIDHash //subcommand params
                    + "03" + "01" // pinUvAuthProtocol
                    + "04" + "58"+ Util.toHex(pinUvAuthParam.length()/2) + pinUvAuthParam; //pinUvAuthParam
                        
                cmd = "41" + cmd;

                break;
            case cm_sub_enumerateCredentialsGetNextCredential :
                printLog("Send Credential Management : "+"enumerateCredentialsGetNextCredential");
                cmd = "A1" 
                    + "01" + "05"; //subcommand index                        
                cmd = "41" + cmd;                
                break;
            case cm_sub_deleteCredential	                  :
                printLog("Send Credential Management : "+"deleteCredential");
                String credentialID = param[0];
                credentialID = "A1" + "02" + "A2" + "64" + "74797065" + "6A" + "7075626C69632D6B6579" + "62" + "6964" + "58" + Util.toHex(credentialID.length()/2) + credentialID;
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "06" + credentialID).substring(0, 16*2);
               
                cmd = "A4" 
                    + "01" + "06" //subcommand index
                    + "02" + credentialID //subcommand params
                    + "03" + "01" // pinUvAuthProtocol
                    + "04" + "58"+ Util.toHex(pinUvAuthParam.length()/2) + pinUvAuthParam; //pinUvAuthParam
                cmd = "41" + cmd;                
                break;
            default : 
                throw new Exception("Subcommand value is wrong");
                
        }

        String pin = "0000";
        String result = makeCommand(cmd);

        return result;
    }


    public JsonNode CredentialManagement_parse(String sub, String res, String... param) throws Exception{
        JsonNode jnode = getCBORDataFromResponse(res);
        //printLog(jnode.toString());
        
        if(res.equals("")){
            return null;
        }
        
        String rp = "";
        String rpIDHash = "";
        String user = "";
        String CredentialID = "";
        String publicKey = "";
        RPs tmpRPs;

        switch (sub){
            case cm_sub_getCredsMetadata	                  :
                break;
            case cm_sub_enumerateRPsBegin	                  :               
                rps.clear();
                rp = jnode.get("3").get("id").toString().replaceAll("\"", "");
                rpIDHash = jnode.get("4").toString();
                int totalRpCount = Integer.parseInt(jnode.get("5").toString());
                rps.add(rp, new RPs(rp, rpIDHash));
                rps.setExpectedSize(totalRpCount - 1);
                break;
            case cm_sub_enumerateRPsGetNextRP	              :
                rp = jnode.get("3").get("id").toString().replaceAll("\"", "");
                rpIDHash = jnode.get("4").toString();
                rps.add(rp, new RPs(rp, rpIDHash));
                break;
            case cm_sub_enumerateCredentialsBegin	          :
                rp = param[0];
                tmpRPs = rps.get(rp);
                user = jnode.get("6").toString();
                CredentialID = jnode.get("7").toString();
                publicKey = jnode.get("8").toString();
                int totalcredCount = Integer.parseInt(jnode.get("9").toString());
                tmpRPs.setCredentialExpectedCnt(totalcredCount - 1);
                tmpRPs.addCredential(new Credential(user, CredentialID, publicKey));
                break;
            case cm_sub_enumerateCredentialsGetNextCredential :
                rp = param[0];
                tmpRPs = rps.get(rp);
                user = jnode.get("6").toString();
                CredentialID = jnode.get("7").toString();
                publicKey = jnode.get("8").toString();
                tmpRPs.addCredential(new Credential(user, CredentialID, publicKey));
                break;
            case cm_sub_deleteCredential	                  :
                break;
            default :
                throw new Exception("Subcommand value is wrong");
        }

        return jnode;
    }


    private byte[]  transceives (byte[] data) throws Exception{

        byte[] ra = null;

        try{
            printLog("***COMMAND APDU***");
            printLog("");
            printLog("IFD - " + Util.getHexString(data));
        }
        catch (Exception e1){
            e1.printStackTrace();
        }

        try{
            ra = myTag.transceive(data);
        }
        catch (IOException e){

            printLog("************************************");
            printLog("         NO CARD RESPONSE");
            printLog("************************************");
            throw new Exception("No Card Response");
        }
        try{
            printLog("");
            printLog("ICC - " + Util.getHexString(ra));
        }
        catch (Exception e1){
            e1.printStackTrace();
        }

        return (ra);
    }

    private String makeCommand(String cData) throws Exception{
        int len_cmd_data = cData.length();
        int off_data = 0;
        int len_remain_data = 0;
        String response = "";
        String responseData = "";

        boolean isLong = false;

        if(len_cmd_data <= 480){
            response = bSendAPDU("80100000" + Util.toHex(len_cmd_data/2) + cData + "00");
            responseData = response.substring(0, response.length() - 4);
        }else{
            while(off_data < len_cmd_data - 480 - 2){
                bSendAPDU("90100000" + Util.toHex(240) + cData.substring(off_data, 480));
                off_data += 480;
                isLong = true;
            }

            len_remain_data = len_cmd_data - off_data;

            response = bSendAPDU("80100000" + Util.toHex(len_remain_data / 2) + cData.substring(off_data, off_data + len_remain_data) + "");
            responseData = response.substring(0, response.length() - 4);


        }

        while(getSW().equals("6100") || getSW().equals("6C00")){
            response = bSendAPDU("80C0000000");
            responseData += response.substring(0, response.length() - 4);
        }


        if(!getSW().substring(2,4).equals("00")){
            response = bSendAPDU("80C00000" + getSW().substring(2,4));
            responseData += response.substring(0, response.length() - 4);
        }

        /*if(getSW().equals("9000")){

        }*/

        if (isLong) {
            printLog("Response - " + responseData);
        }

        return responseData;
    }

    public String getSW(){
        return sw;
    }

    private String bSendAPDU(String StringAPDU) throws Exception{

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
            String tmp = Util.getHexString(respAPDU);
            sw = tmp.substring(tmp.length() - 4);
            return Util.getHexString(respAPDU);
        }
        catch (Exception e1){
            sw = "";
            e1.printStackTrace();
        }

        return "";
    }

    public void printLog(Object str){
        if(LOG_MODE){
            System.out.println(str);
        }else{
            Log.v(TAG, str.toString());
        }
    }


}
