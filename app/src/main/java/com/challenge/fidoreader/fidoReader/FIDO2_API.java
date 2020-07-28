package com.challenge.fidoreader.fidoReader;

import android.util.Log;

import com.challenge.fidoreader.Util.Util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import java.io.ByteArrayInputStream;

public abstract class FIDO2_API {
    public final static String TAG = "FIDO2_API";
    public final static boolean LOG_MODE = false;

    public abstract String commands(String... params) throws Exception;
    public abstract String responses(String res, String... params) throws Exception;

    public abstract String commands(String sub, String... params) throws Exception;
    public abstract String responses(String sub, String res, String... params) throws Exception;

    String pinUvAuthToken;

    public void setPinUvAuthToken(String pinUvAuthToken){
        this.pinUvAuthToken = pinUvAuthToken;
    }

    public void printLog(Object str){
        if(LOG_MODE){
            System.out.println(str);
        }else{
            Log.v(TAG, str.toString());
        }
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
}
