package com.challenge.fidoreader.reader

import android.nfc.tech.IsoDep
import android.util.Log
import com.challenge.fidoreader.Util.atohex
import com.challenge.fidoreader.Util.cardReader
import com.challenge.fidoreader.Util.getHexString
import com.challenge.fidoreader.Util.toHex
import java.io.IOException

class NFCSender : Sender(){
    val TAG = "NFCSender"
    var LOG_MODE = false

    var byteAPDU: ByteArray? = null
    var resAPDU: ByteArray? = null
    var sw:String? = null

    override fun startFIDO(){
        bSendAPDU("00A4040008A0000006472F000100")
        assertSW("9000")
    }

    override fun sendFIDO(cmd: String) : String{
        var result = makeCommand(cmd)
        assertSW("9000")
        return result
    }


    fun assertSW(sw: String) {
        if (sw != this.sw) {
            throw java.lang.Exception("Excepted SW [ " + sw + " ], and return SW [ " + this.sw + " ]")
        }
    }

    fun makeCommand(cData: String):String{
        var len_cmd_data = cData.length
        var off_data = 0
        var len_remain_data = 0
        var response:String = ""
        var responseData:String = ""

        var isLong = false

        if(len_cmd_data <= 480){
            response = bSendAPDU("80100000" + (len_cmd_data/2).toHex() + cData + "00")
            responseData = response.substring(0, response.length-4)
        }else{
            while(off_data < len_cmd_data - 480 - 2){
                bSendAPDU("90100000" + 240.toHex() + cData.substring(off_data, 480))
                off_data += 480
                isLong = true
            }
            len_remain_data = len_cmd_data - off_data

            response = bSendAPDU("80100000" + (len_remain_data / 2).toHex() + cData.substring(off_data, off_data + len_remain_data) + "")
            responseData = response.substring(0, response.length - 4)

        }


        while (this.sw == "6100" || this.sw == "6C00") {
            response = bSendAPDU("80C0000000")
            responseData += response.substring(0, response.length - 4)
        }


        if (this.sw!!.substring(2, 4) != "00") {
            response = bSendAPDU("80C00000" + this.sw!!.substring(2, 4))
            responseData += response.substring(0, response.length - 4)
        }


        /*if(getSW().equals("9000")){

        }*/

        if (isLong) {
            printLog("Response - $responseData")
        }

        return responseData
    }

    fun bSendAPDU(stringAPDU: String):String{
        byteAPDU = stringAPDU.atohex()
        resAPDU = transceives(byteAPDU)


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

        try {
            val tmp = resAPDU!!.getHexString()
            sw = tmp.substring(tmp.length - 4)
            return resAPDU!!.getHexString()
        } catch (e1: java.lang.Exception) {
            sw = ""
            e1.printStackTrace()
        }

        return ""
    }

    fun transceives(data: ByteArray?):ByteArray{

        var ra: ByteArray? = null

        try {
            printLog("***COMMAND APDU***")
            printLog("")
            printLog("IFD - " + data!!.getHexString())
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        ra = try {
            cardReader.myTag!!.transceive(data)
        } catch (e: IOException) {
            printLog("************************************")
            printLog("         NO CARD RESPONSE")
            printLog("************************************")
            throw Exception("No Card Response")
        }
        try {
            printLog("")
            printLog("ICC - " + ra!!.getHexString())
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return ra!!
    }


    fun printLog(str: Any) {
        if (LOG_MODE) {
            println(str)
        } else {
            Log.v(TAG, str.toString())
        }
    }
}