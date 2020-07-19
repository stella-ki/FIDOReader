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

import org.bouncycastle.jcajce.provider.symmetric.AES;

import java.security.MessageDigest;

public class Util
{

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
            szDataStr += String.format("%02X ", data[ii] & 0xFF);
        }
        return szDataStr;
    }


    public static String getATRLeString(byte[] data) throws Exception
    {
        return String.format("%02X ", data.length | 0x80);
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
        return String.format("%02X ", Lrc);
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
        return Util.getHexString(sha_256(Util.atohex(plainText)));
    }


    public static byte[] aes_cbc(byte[] plainText, byte[] keyText) throws Exception{
        //TODO
        return plainText;
    }


    public static String aes_cbc(String plainText, String keyText) throws Exception{
        return Util.getHexString(aes_cbc(Util.atohex(plainText), Util.atohex(keyText)));
    }

}
