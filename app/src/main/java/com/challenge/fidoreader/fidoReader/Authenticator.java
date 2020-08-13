package com.challenge.fidoreader.fidoReader;

import android.content.Intent;
import android.nfc.tech.IsoDep;
import android.util.Log;

import com.challenge.fidoreader.Exception.UserException;
import com.challenge.fidoreader.Util.Util;
import com.challenge.fidoreader.fagment.CredentialItem;
import com.challenge.fidoreader.fagment.FingerItem;
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

    IsoDep myTag;

    static byte[] byteAPDU=null;
    static byte[] respAPDU=null;
    static String sw = "";


    SharedSecretObject sso;
    String pinUvAuthToken;
    String clientPIN = "";
    String originPIN = "";

    CredentialManagement_API credMg;
    BioEnrollment_API bio_api;

    String userPIN = "";

    public Authenticator(){
        sso = new SharedSecretObject();
        pinUvAuthToken = "";
        credMg = new CredentialManagement_API();
        bio_api = new BioEnrollment_API();
    }

    public void setTag(IsoDep myTag) {
        this.myTag = myTag;
    }

    public String getInfo() throws Exception {

        bSendAPDU("00A4040008A0000006472F000100");
        assertSW("9000");

        printLog("getInfo");

        sso.init();
        pinUvAuthToken = "";

        //  Test - kelee
        String result = bSendAPDU("80100000010400");
        assertSW("9000");

        result = result.substring(2, result.length() - 4);

//        return "80100000010400";
        return result;
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

        if(res.equals("")){
            return null;
        }
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

    public String ClientPIN(byte sub) throws Exception{
        String result = ClientPIN_cmd(sub);
        if(result.substring(0,2).equals("00")){
            ClientPINparse(sub, result);
        }else{
            printLog("ClientPIN is not successful");
        }
        return result.substring(0, 2);
    }

    public void assertSW(String sw) throws Exception{
        if(!sw.equals(getSW())){
            throw new Exception("Excepted SW [ " + sw + " ], and return SW [ " + getSW() + " ]");
        }
    }

    public boolean deleteCredential(String cred_id) throws Exception{
        printLog("deleteCredential");

        bSendAPDU("00A4040008A0000006472F000100");
        assertSW("9000");

        getInfo();
        assertSW("9000");

        ClientPIN(Authenticator.cp_sub_getKeyAgreement);
        assertSW("9000");

        ClientPIN(Authenticator.cp_sub_getPinUvAuthTokenUsingPin);
        assertSW("9000");

        String fido_result = CredentialManagement(CredentialManagement_API.cm_sub_deleteCredential, cred_id);
        if(!fido_result.equals("00")){
            throw new UserException("Credential deletion is failed");
        }

        return true;
    }

    public ArrayList<FingerItem> readEnrollInformation() throws Exception{
        ArrayList<FingerItem> list = new ArrayList<>();//null;

        printLog("deleteCredential");

//        bSendAPDU("00A4040008A0000006472F000100");
//        assertSW("9000");
//
//        getInfo();
//        assertSW("9000");
//
//        ClientPIN(Authenticator.cp_sub_getKeyAgreement);
//        assertSW("9000");
//
//        ClientPIN(Authenticator.cp_sub_getPinUvAuthTokenUsingPin);
//        assertSW("9000");

        String fido_result = BioEnrollment(BioEnrollment_API.be_sub_emurateEnrollments);
        if(!fido_result.equals("00")){
            throw new UserException("BioEnrollment is failed");
        }

        return bio_api.fingerList;
    }


    public boolean deleteEnroll(String templateID) throws Exception{
        ArrayList<FingerItem> list = new ArrayList<>();//null;

        printLog("deleteEnroll");

        bSendAPDU("00A4040008A0000006472F000100");
        assertSW("9000");

        getInfo();
        assertSW("9000");

        ClientPIN(Authenticator.cp_sub_getKeyAgreement);
        assertSW("9000");

        ClientPIN(Authenticator.cp_sub_getPinUvAuthTokenUsingPin);
        assertSW("9000");

        String fido_result = BioEnrollment(BioEnrollment_API.be_sub_removeEnrollment, templateID);
        if(!fido_result.equals("00")){
            throw new UserException("BioEnrollment is failed");
        }

        return true;
    }


    public boolean changeEnroll(String templateID, String new_name) throws Exception{
        ArrayList<FingerItem> list = new ArrayList<>();//null;

        printLog("deleteEnroll");

        bSendAPDU("00A4040008A0000006472F000100");
        assertSW("9000");

        getInfo();
        assertSW("9000");

        ClientPIN(Authenticator.cp_sub_getKeyAgreement);
        assertSW("9000");

        ClientPIN(Authenticator.cp_sub_getPinUvAuthTokenUsingPin);
        assertSW("9000");

        String fido_result = BioEnrollment(BioEnrollment_API.be_sub_setFriendlyName, templateID, new_name);
        if(!fido_result.equals("00")){
            throw new UserException("BioEnrollment is failed");
        }

        return true;
    }


    public boolean getPINToken() throws Exception{

        printLog("getPinUvAuthTokenUsingPin");

        bSendAPDU("00A4040008A0000006472F000100");
        assertSW("9000");

        getInfo();
        assertSW("9000");

        ClientPIN(Authenticator.cp_sub_getKeyAgreement);
        assertSW("9000");

        ClientPIN(Authenticator.cp_sub_getPinUvAuthTokenUsingPin);
        assertSW("9000");

        return true;
    }

    public String enrollfinger() throws Exception{
        printLog("enrollfinger");

        String fido_result = BioEnrollment(BioEnrollment_API.be_sub_enrollBegin, "4E20");
        if(!fido_result.equals("00")){
            throw new UserException("BioEnrollment is failed");
        }

        int cnt = Integer.valueOf(bio_api.remainingSamples, 16);

        //printLog("remain : " + cnt);
        //printLog("bio_api.templateID : " + bio_api.templateID);
        return bio_api.templateID;
    }


    public int enrollNextfinger(String templateID) throws Exception{
        printLog("enrollCaptureNextSample");

        String fido_result = BioEnrollment(BioEnrollment_API.be_sub_enrollCaptureNextSample, "4E20", templateID);
        if(!fido_result.equals("00")){
            throw new UserException("BioEnrollment is failed");
        }

        int cnt = Integer.valueOf(bio_api.remainingSamples, 16);
        printLog("remain : " + cnt);

        return cnt;
    }



    public boolean enrollCancel() throws Exception{
        printLog("enrollCancel");

        String fido_result = BioEnrollment(BioEnrollment_API.be_sub_cancelCurrentEnrollment);
        if(!fido_result.equals("00")){
            throw new UserException("BioEnrollment is failed");
        }

        return true;
    }


    public ArrayList<CredentialItem> getCredentialList() throws Exception{
        printLog("getCredentialList");
        ArrayList<CredentialItem> list = new ArrayList<CredentialItem>();

//        bSendAPDU("00A4040008A0000006472F000100");
//        assertSW("9000");
//
//        getInfo();
//        assertSW("9000");
//
//        ClientPIN(Authenticator.cp_sub_getKeyAgreement);
//        assertSW("9000");
//
//        ClientPIN(Authenticator.cp_sub_getPinUvAuthTokenUsingPin);
//        assertSW("9000");

        int num = 0;

        String fido_result = CredentialManagement(CredentialManagement_API.cm_sub_enumerateRPsBegin);
        if(fido_result.equals("2E")){
            throw new UserException("Credential is not exist");
        }
        for (; num < credMg.getRps().expectedSize(); num++){
            CredentialManagement(CredentialManagement_API.cm_sub_enumerateRPsGetNextRP);
        }

        String rp = ""; //get First key value which is rp
        for (num = 0; num < credMg.getRps().getSize(); num++){
            rp = (String) credMg.getRps().getKey(num); //get First key value which is rp
            printLog("Read Credential about RP : " + rp);
            CredentialManagement(CredentialManagement_API.cm_sub_enumerateCredentialsBegin, rp);
            int credCunt = credMg.getRps().get(rp).getCredentialExpectedCnt();
            for (int j = 0; j < credCunt; j++){
                CredentialManagement(CredentialManagement_API.cm_sub_enumerateCredentialsGetNextCredential, rp);
            }
        }

        for (num = 0; num < credMg.getRps().getSize(); num++){
            RPs tmprps = (RPs)credMg.getRps().getValue(num);
            for (int j = 0; j < tmprps.getCredentials().size(); j++){
                list.add(new CredentialItem(tmprps.getCredential(j).getCredentialID(), tmprps.getRp(), tmprps.getCredential(j).getUser()));
            }
        }
        return list;
    }

    public String setUserPIN(String userPIN) throws Exception {
        this.userPIN = userPIN;

        bSendAPDU("00A4040008A0000006472F000100");
        assertSW("9000");

        getInfo();
        assertSW("9000");

        ClientPIN(Authenticator.cp_sub_getKeyAgreement);
        assertSW("9000");

        String result = ClientPIN(Authenticator.cp_sub_getPinUvAuthTokenUsingPin);
        assertSW("9000");

        return result;
    }

    public String setPIN(String clientPIN) throws Exception {
        bSendAPDU("00A4040008A0000006472F000100");
        assertSW("9000");

        sso.init();
        pinUvAuthToken = "";

        ClientPIN(Authenticator.cp_sub_getKeyAgreement);
        assertSW("9000");

        this.clientPIN = clientPIN;

        String result = ClientPIN(Authenticator.cp_sub_setPIN);
        assertSW("9000");

        return result;

    }

    public String changePin(String beforePIN, String newPIN) throws Exception {
        bSendAPDU("00A4040008A0000006472F000100");
        assertSW("9000");

        sso.init();
        pinUvAuthToken = "";

        ClientPIN(Authenticator.cp_sub_getKeyAgreement);
        assertSW("9000");

        this.originPIN = beforePIN;
        this.clientPIN = newPIN;

        String result = ClientPIN(Authenticator.cp_sub_changePIN);
        assertSW("9000");

        return result;
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
                String keyAgreement = "A5010203262001215820" + sso.getPublickey().substring(0,64) + "225820" + sso.getPublickey().substring(64);
                String newPinEnc = padding_00(clientPIN);   // clientPIN -> User INPUT
                newPinEnc = Util.aes_cbc(sso.getSharedSecret(), newPinEnc);
                String pinUvAuthParam = Util.hmac_sha_256(sso.getSharedSecret(), newPinEnc).substring(0, 32);
                cmd = "A5"
                        + "01" + "01" // pinUvAuthProtocol
                        + "02" + "03" // subCommand
                        + "03" + keyAgreement   //keyAgreement
                        + "04" + "50" + pinUvAuthParam  // pinUbAuthParam
                        + "05" + "58" + "40" + newPinEnc    // newPinEnc
                ;
                cmd = "06" + cmd;
                break;
            case cp_sub_changePIN                  :
                printLog("Send Client PIN : " + "changePIN");
                keyAgreement = "A5010203262001215820" + sso.getPublickey().substring(0,64) + "225820" + sso.getPublickey().substring(64);

                newPinEnc = padding_00(clientPIN);   // clientPIN -> User INPUT (new PIN)
                newPinEnc = Util.aes_cbc(sso.getSharedSecret(), newPinEnc);

                String pinHashEnc = Util.sha_256(originPIN);    // currentPIN
                pinHashEnc = Util.aes_cbc(sso.getSharedSecret(), pinHashEnc.substring(0, 32));

                pinUvAuthParam = Util.hmac_sha_256(sso.getSharedSecret(), newPinEnc + pinHashEnc).substring(0, 32);
                cmd = "A6"
                        + "01" + "01" // pinUvAuthProtocol
                        + "02" + "04" // subCommand
                        + "03" + keyAgreement   //keyAgreement
                        + "04" + "50" + pinUvAuthParam  // pinUbAuthParam
                        + "05" + "58" + "40" + newPinEnc    // newPinEnc
                        + "06" + "50" + pinHashEnc          // pinHashEn (related with currentPIN)
                ;
                cmd = "06" + cmd;
                break;
            case cp_sub_getPinUvAuthTokenUsingPin  :
                printLog("Send Client PIN : " + "getPinUvAuthTokenUsingPin");
                keyAgreement = "A5010203262001215820" + sso.getPublickey().substring(0,64) + "225820" + sso.getPublickey().substring(64);
//                String sha = Util.sha_256("30303030").substring(0, 16 * 2);
                String sha = Util.sha_256(userPIN).substring(0, 16 * 2);
                printLog("sha : " + sha);
                pinHashEnc = Util.aes_cbc(sso.getSharedSecret(), sha);
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

        credMg.setPinUvAuthToken(pinUvAuthToken);
        String cmd = credMg.commands(sub, param);

        String result = makeCommand(cmd);

        fido_result = result.substring(0,2);
        if(fido_result.equals("00")){
            credMg.responses(sub, result, param);
        }else{
            printLog("CredentialManagement is not successful");
        }

        return fido_result;
    }



    public String BioEnrollment(String sub, String... param) throws Exception{
        String fido_result = "";

        bio_api.setPinUvAuthToken(pinUvAuthToken);
        String cmd = bio_api.commands(sub, param);

        String result = makeCommand(cmd);

        fido_result = result.substring(0,2);
        if(fido_result.equals("00")){
            bio_api.responses(sub, result, param);
        }else{
            printLog("BioEnrollment is not successful");
        }

        return fido_result;
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

    public String padding_00(String str){
        if(str.length()/2 < 64){
            while(str.length()/2 != 64) {
                str = str + "00";
            }
        }
        return str;
    }


}
