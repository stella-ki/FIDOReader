package com.challenge.fidoreader.fido;

import com.challenge.fidoreader.Util.MapList;
import com.challenge.fidoreader.Util.Util;
import com.fasterxml.jackson.databind.JsonNode;


public class CredentialManagement_API extends FIDO2_API  {
    public final static String TAG = "CredentialManagement_API";

    public static final String cm_sub_getCredsMetadata	                    =  "1" ;
    public static final String cm_sub_enumerateRPsBegin	                    =  "2" ;
    public static final String cm_sub_enumerateRPsGetNextRP	                =  "3" ;
    public static final String cm_sub_enumerateCredentialsBegin	            =  "4" ;
    public static final String cm_sub_enumerateCredentialsGetNextCredential =  "5" ;
    public static final String cm_sub_deleteCredential	                    =  "6" ;


    MapList<String, RPs> rps;

    public CredentialManagement_API(){
        pinUvAuthToken = "";
        rps = new MapList<String, RPs>();
    }

    public MapList<String, RPs> getRps(){
        return rps;
    }

    @Override
    public String commands(String sub, String[] params) throws Exception{
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
                rp = params[0];
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
                printLog("Send Credential Management : "+"deleteCredential " + params[0]);
                String credentialID = params[0].replaceAll("\"","");
                // = Util.convertTohex(credentialID);
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

        return cmd;
    }

    @Override
    public String responses(String sub, String res, String... params) throws Exception{
        JsonNode jnode = getCBORDataFromResponse(res);

        if(res.equals("") || jnode == null){
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
                rp = params[0];
                tmpRPs = rps.get(rp);
                user = jnode.get("6").get("name").toString();
                CredentialID = Util.getHexString(jnode.get("7").get("id").binaryValue());
                publicKey = jnode.get("8").toString();
                int totalcredCount = Integer.parseInt(jnode.get("9").toString());
                tmpRPs.setCredentialExpectedCnt(totalcredCount - 1);
                tmpRPs.addCredential(new Credential(user, CredentialID, publicKey));
                break;
            case cm_sub_enumerateCredentialsGetNextCredential :
                rp = params[0];
                tmpRPs = rps.get(rp);
                user = jnode.get("6").get("name").toString();
                CredentialID = Util.getHexString(jnode.get("7").get("id").binaryValue());
                publicKey = jnode.get("8").toString();
                tmpRPs.addCredential(new Credential(user, CredentialID, publicKey));
                break;
            case cm_sub_deleteCredential	                  :
                break;
            default :
                throw new Exception("Subcommand value is wrong");
        }

        return "";
    }

    @Override
    public String commands(String... params) throws Exception {
        throw new Exception("This method is not supported");
    }

    @Override
    public String responses(String res, String... params) throws Exception {
        throw new Exception("This method is not supported");
    }
}
