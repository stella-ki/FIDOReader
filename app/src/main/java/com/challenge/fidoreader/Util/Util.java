/*
Copyright 2014  Jose Maria ARROYO jm.arroyo.castejon@gmail.com

APDUSenderContactLess is free software: you can redistribute it and/or modify
it  under  the  terms  of the GNU General Public License  as published by the 
Free Software Foundation, either version 3 of the License, or (at your option) 
any later version.

APDUSenderContactLess is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package com.challenge.fidoreader.Util;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Util
{
    static byte[] ZERO =
            {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};


    public static String szByteHex2String(byte datain)
    {
        String[] CHARS0F = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

        int idata = datain & 0xFF;
        int nibble_1 = (idata >> 0x04) & 0x0F;
        int nibble_2 = idata & 0x0F;

        return CHARS0F[nibble_1] + CHARS0F[nibble_2];
    }


    public static String getHexString(byte[] data) throws Exception
    {
        String szDataStr = "";
        for (int ii=0; ii < data.length; ii++)
        {
            szDataStr += String.format("%02X", data[ii] & 0xFF);
        }
        return szDataStr;
    }

    public static byte[] hexStringToByteArray(String s) {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        Pattern p = Pattern.compile("[^\\s\\da-fA-F]");
        Matcher m = p.matcher(s);
        if(m.find()){
            return null;
        }
        s = s.replaceAll("\\s+", "");
        if(s.length()%2 != 0){
            return null;
        }
        for(int i = 0; i<s.length(); i+=2){
            bytestream.write(Integer.parseInt(s.substring(i,i+2), 16));

        }
        return bytestream.toByteArray();
    }


    public static String byteArrayToHexString(byte[] bytes){

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i< bytes.length; i++){

            String tmp = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
            if(tmp.length() == 1){
                sb.append("0" + tmp);
            }else{
                sb.append(tmp);
            }
        }

        return sb.toString();
    }


    public static String getATRLeString(byte[] data) throws Exception
    {
        return String.format("%02X", data.length | 0x80);
    }


    public static String getATRXorString(byte[] b) throws Exception
    {
        int Lrc=0x00;
        Lrc = b.length | 0x80;
        Lrc = Lrc^0x81;
        for (int i=0; i < b.length; i++)
        {
            Lrc = Lrc^(b[i] & 0xFF);
        }
        return String.format("%02X", Lrc);
    }


    public static byte[] atohex(String data)
    {
        String hexchars = "0123456789abcdef";

        data = data.replaceAll(" ","").toLowerCase();
        if (data == null)
        {
            return null;
        }
        byte[] hex = new byte[data.length() / 2];

        for (int ii = 0; ii < data.length(); ii += 2)
        {
            int i1 = hexchars.indexOf(data.charAt(ii));
            int i2 = hexchars.indexOf(data.charAt(ii + 1));
            hex[ii/2] = (byte)((i1 << 4) | i2);
        }
        return hex;
    }

    public static byte[] sha_256(byte[] plainText) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        sha.update(plainText);
        return sha.digest();
    }


    public static String sha_256(String plainText) throws Exception{
        return Util.byteArrayToHexString
                (sha_256(Util.hexStringToByteArray(plainText)));
    }

    public static String hmac_sha_256(String key, String input) throws NoSuchAlgorithmException, InvalidKeyException{
        Mac hasher = Mac.getInstance("HmacSHA256");
        hasher.init(new SecretKeySpec(Util.hexStringToByteArray(key), "HmacSHA256"));
        byte[] hash = hasher.doFinal(Util.hexStringToByteArray(input));

        return Util.byteArrayToHexString(hash);
    }


    public static byte[] aes_cbc(byte[] keyData, byte[] str) throws Exception{
        SecretKey key;
        Cipher cipher;

        byte[] cipherText = {(byte)0x00, };

        try{
            key = new SecretKeySpec(keyData, "AES");
            cipher = Cipher.getInstance("AES/CBC/NoPadding");

            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ZERO));

            cipherText = cipher.doFinal(str);
        }catch(Exception e){
            e.printStackTrace();
        }

        return cipherText;
    }


    // Encryption
    public static String aes_cbc(String key, String str) throws Exception {

        byte[] keyData = Util.hexStringToByteArray(key);
        byte[] encrypted = aes_cbc(keyData, Util.hexStringToByteArray(str));

        return Util.byteArrayToHexString(encrypted);
    }


    // Decryption
    public static byte[] aes_cbc_dec(byte[] keyData, byte[] str) throws Exception{
        SecretKey key;
        Cipher cipher;

        byte[] cipherText = {(byte)0x00, };

        try{
            key = new SecretKeySpec(keyData, "AES");
            cipher = Cipher.getInstance("AES/CBC/NoPadding");

            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ZERO));

            cipherText = cipher.doFinal(str);
        }catch(Exception e){
            e.printStackTrace();
        }

        return cipherText;
    }


    // Encryption
    public static String aes_cbc_dec(String key, String str) throws Exception {

        byte[] keyData = Util.hexStringToByteArray(key);
        byte[] encrypted = aes_cbc_dec(keyData, Util.hexStringToByteArray(str));

        return Util.byteArrayToHexString(encrypted);
    }
    
    public static String HMACSHA256(String key, String input) throws Exception{
        Mac hasher = javax.crypto.Mac.getInstance("HmacSHA256");
        hasher.init(new SecretKeySpec(Util.hexStringToByteArray(key),"HmacSHA256"));
        byte[] hash = hasher.doFinal(Util.hexStringToByteArray(input));
        return Util.byteArrayToHexString(hash);
    }
    
    public static String toHex(int value){
        String n = Integer.toHexString(value).toUpperCase();
        n = (n.length() % 2 == 1 ? "0" + n : n);
        return n;
    }

    public static String convertTohex(String bytes) {
        char[] chars = bytes.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++){
            sb.append(Integer.toHexString((int)chars[i]));
        }

        return sb.toString().toUpperCase();

    }

    public static String ascii(String str){
        char[] chars = str.toCharArray();

        StringBuffer sb = new StringBuffer();

        for(int i = 0 ; i < chars.length ; i++){
            sb.append(Integer.toHexString((int)chars[i]));
        }

        return sb.toString().toUpperCase();
    }
}
