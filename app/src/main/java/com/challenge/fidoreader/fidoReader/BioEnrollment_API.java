package com.challenge.fidoreader.fidoReader;

import com.challenge.fidoreader.Util.MapList;
import com.challenge.fidoreader.Util.Util;
import com.fasterxml.jackson.databind.JsonNode;


public class BioEnrollment_API extends FIDO2_API  {
    public final static String TAG = "BioEnrollment_API";

    public static final String be_getbiomodality	                    =  "0" ;
    public static final String be_sub_enrollBegin	                    =  "1" ;
    public static final String be_sub_enrollCaptureNextSample	                    =  "2" ;
    public static final String be_sub_cancelCurrentEnrollment	                =  "3" ;
    public static final String be_sub_emurateEnrollments	            =  "4" ;
    public static final String be_sub_setFriendlyName =  "5" ;
    public static final String be_sub_removeEnrollment	                    =  "6" ;
    public static final String be_sub_getFingerprintSensorInfo	                    =  "7" ;

    public BioEnrollment_API(){
        pinUvAuthToken = "";
    }

    @Override
    public String commands(String sub, String[] params) throws Exception{
        String pinUvAuthParam = "";
        String cmd = "";

        String templateID = "";
        String templateFriendlyName = "";
        String timeoutMiliseconds = "";

        switch (sub){
            case be_sub_enrollBegin	                  :
                printLog("Send BioEnrollment : "+"enrollBegin");
                timeoutMiliseconds = params[0].replaceAll("\"","");
                if(timeoutMiliseconds != null){
                    timeoutMiliseconds = "A1" + "03" + "19" + timeoutMiliseconds;
                }else{
                    timeoutMiliseconds = "A0";
                }
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "01" + "01" + timeoutMiliseconds).substring(0, 16*2);
                cmd = "A5"
                        + "01" + "01" //modality 
                        + "02" + "01" //sub command index
                        + "03" + timeoutMiliseconds // subcommand params
                        + "04" + "01" // pinUvAuthProtocol
                        + "05" + "58"+ Util.toHex(pinUvAuthParam.length()/2) + pinUvAuthParam; //pinUvAuthParam
                cmd = "40" + cmd;
                break;
            case be_sub_enrollCaptureNextSample	                  :
                printLog("Send BioEnrollment : "+"enrollCaptureNextSample");    
                String subparam = "";
                timeoutMiliseconds = params[0].replaceAll("\"","");
                templateID = params[1].replaceAll("\"","");
                if(timeoutMiliseconds != null){
                    templateID = "01" + "41" + templateID
                    timeoutMiliseconds = "03" + "19" + timeoutMiliseconds;
                    subparam = "A2" + templateID + timeoutMiliseconds
                }else{
                    templateID = "01" + "41" + templateID
                    subparam = "A1" + templateID
                }
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "01" + "02" + subparam).substring(0, 16*2);
                cmd = "A5"
                        + "01" + "01" //modality 
                        + "02" + "02" //sub command index
                        + "03" + subparam // subcommand params
                        + "04" + "01" // pinUvAuthProtocol
                        + "05" + "58"+ Util.toHex(pinUvAuthParam.length()/2) + pinUvAuthParam; //pinUvAuthParam
                cmd = "40" + cmd;
                break;
            case be_sub_cancelCurrentEnrollment	              :
                printLog("Send BioEnrollment : "+"cancelCurrentEnrollment");
                cmd = "A2"
                        + "01" + "01"; //modality
                        + "02" + "03"; //subcommand index
                cmd = "40" + cmd;
                break;
            case be_sub_emurateEnrollments	            	          :
                printLog("Send BioEnrollment : "+"emurateEnrollments");
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "01" + "04").substring(0, 16*2);

                cmd = "A4"
                        + "01" + "01" //modality 
                        + "02" + "04" //sub command index
                        + "04" + "01" // pinUvAuthProtocol
                        + "05" + "58"+ Util.toHex(pinUvAuthParam.length()/2) + pinUvAuthParam; //pinUvAuthParam
                cmd = "40" + cmd;

                break;
            case be_sub_setFriendlyName :
                printLog("Send BioEnrollment : "+"setFriendlyName");
                String subparam = "";
                templateID = params[0].replaceAll("\"","");
                templateFriendlyName = params[1].replaceAll("\"","");
                
                templateID = "01" + "41" + templateID
                templateFriendlyName = "02" + (60 + templateFriendlyName.length()/2) + templateFriendlyName;
                subparam = "A2" + templateID + templateFriendlyName
                    
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "01" + "05" + subparam).substring(0, 16*2);
                cmd = "A5"
                        + "01" + "01" //modality 
                        + "02" + "05" //sub command index
                        + "03" + subparam // subcommand params
                        + "04" + "01" // pinUvAuthProtocol
                        + "05" + "58"+ Util.toHex(pinUvAuthParam.length()/2) + pinUvAuthParam; //pinUvAuthParam
                cmd = "40" + cmd;
                break;
            case be_sub_removeEnrollment	                  :
                printLog("Send BioEnrollment : "+"be_sub_removeEnrollment ");
                String subparam = "";
                templateID = params[0].replaceAll("\"","");
                
                templateID = "01" + "41" + templateID
                subparam = "A1" + templateID + templateFriendlyName
                    
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "01" + "06" + subparam).substring(0, 16*2);
                cmd = "A5"
                        + "01" + "01" //modality 
                        + "02" + "06" //sub command index
                        + "03" + subparam // subcommand params
                        + "04" + "01" // pinUvAuthProtocol
                        + "05" + "58"+ Util.toHex(pinUvAuthParam.length()/2) + pinUvAuthParam; //pinUvAuthParam
                cmd = "40" + cmd;
                break;
            
            case be_sub_getFingerprintSensorInfo	                  :
                printLog("Send BioEnrollment : "+"getFingerprintSensorInfo");
                cmd = "A2"
                        + "01" + "01"; //modality
                        + "02" + "07"; //subcommand index
                cmd = "40" + cmd;
                break;
            case be_getbiomodality	                  :
                printLog("Send BioEnrollment : "+"be_getbiomodality");
                cmd = "A1"
                        + "06" + "F5"; //modality
                cmd = "40" + cmd;
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
            case be_getbiomodality	:
                String modality = jnode.get("1").toString().replaceAll("\"", "");
                break;
            case be_sub_enrollBegin	                  :
                String templateID = jnode.get("4").toString();
                String lastEnrollSampleStatus = jnode.get("5").toString();
                String remainingSamples = jnode.get("6").toString();
                break;
            case be_sub_enrollCaptureNextSample	              :                
                String lastEnrollSampleStatus = jnode.get("5").toString();
                String remainingSamples = jnode.get("6").toString();
                break;
            case be_sub_cancelCurrentEnrollment	          :
                
                break;
            case be_sub_emurateEnrollments :
                String templateInfo = jnode.get("7").toString();
                CredentialID = Util.getHexString(jnode.get("7").get("id").binaryValue());
                publicKey = jnode.get("8").toString();
                tmpRPs.addCredential(new Credential(user, CredentialID, publicKey));
                break;
            case be_sub_setFriendlyName 	                  :
                break;
            case be_sub_removeEnrollment 	                  :
                break;
            case be_sub_getFingerprintSensorInfo 	                  :
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
