package com.challenge.fidoreader.fidoReader

import android.nfc.tech.IsoDep
import android.util.Log
import com.challenge.fidoreader.Exception.UserException
import com.challenge.fidoreader.Util.Util
import com.challenge.fidoreader.fagment.CredentialItem
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*

class Authenticator {
    val TAG = "Authenticator"
    open var LOG_MODE = false

    var myTag:IsoDep? = null

    var byteAPDU: ByteArray? = null
    var resAPDU: ByteArray? = null
    var sw:String? = null

    var tmpparams: ArrayList<String> = ArrayList<String>()

    var credMg: CredentialManagement_API = CredentialManagement_API()
    var clintPIN: ClientPIN_API = ClientPIN_API()

    fun getInfo():String{
        bSendAPDU("00A4040008A0000006472F000100")
        assertSW("9000")

        printLog("getInfo")

        clintPIN.reset()

        var result : String = bSendAPDU("80100000010400")
        assertSW("9000")

        result = result.substring(2, result.length - 4)

        return result
    }

    fun getInfo_parse(result: String): JsonNode?{

        val bais = ByteArrayInputStream(Util.atohex(result))

        val cf = CBORFactory()
        val mapper = ObjectMapper(cf)
        try {
            val jnode = mapper.readValue(bais, JsonNode::class.java)
            printLog(jnode)
            return jnode
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun deleteCredential(cred_id: String): Boolean {
        printLog("deleteCredential")

        bSendAPDU("00A4040008A0000006472F000100")
        assertSW("9000")

        getInfo()
        assertSW("9000")

        ClientPIN(ClientPIN_API.cp_sub_getKeyAgreement)
        assertSW("9000")

        ClientPIN(ClientPIN_API.cp_sub_getPinUvAuthTokenUsingPin)
        assertSW("9000")

        tmpparams.clear()
        tmpparams.add(cred_id)
        val fido_result: String = CredentialManagement(CredentialManagement_API.cm_sub_deleteCredential, tmpparams) as String
        if (fido_result != "00") {
            throw UserException("Credential deletion is failed")
        }
        return true
    }



    fun getCredentialList(): ArrayList<CredentialItem>? {
        printLog("getCredentialList")

        val list = ArrayList<CredentialItem>()
        bSendAPDU("00A4040008A0000006472F000100")
        assertSW("9000")

        getInfo()
        assertSW("9000")

        ClientPIN(ClientPIN_API.cp_sub_getKeyAgreement)
        assertSW("9000")

        ClientPIN(ClientPIN_API.cp_sub_getPinUvAuthTokenUsingPin)
        assertSW("9000")

        var num = 0
        val fido_result: String = CredentialManagement(CredentialManagement_API.cm_sub_enumerateRPsBegin) as String
        if (fido_result == "2E") {
            throw UserException("Credential is not exist")
        }
        while (num < credMg.rps.expectedSize()) {
            CredentialManagement(CredentialManagement_API.cm_sub_enumerateRPsGetNextRP)
            num++
        }

        var rp = "" //get First key value which is rp
        num = 0
        while (num < credMg.rps.size) {
            rp = credMg.rps.getKey(num) as String //get First key value which is rp
            printLog("Read Credential about RP : $rp")

            tmpparams.clear()
            tmpparams.add(rp)
            CredentialManagement(CredentialManagement_API.cm_sub_enumerateCredentialsBegin, tmpparams)
            val credCunt = credMg.rps[rp].getCredentialExpectedCnt()
            for (j in 0 until credCunt) {

                tmpparams.clear()
                tmpparams.add(rp)
                CredentialManagement(CredentialManagement_API.cm_sub_enumerateCredentialsGetNextCredential, tmpparams)
            }
            num++
        }

        num = 0
        while (num < credMg.rps.size) {
            val tmprps = credMg.rps.getValue(num) as RPs
            for (j in tmprps.credentials.indices) {
                list.add(CredentialItem(tmprps.getCredential(j)!!.credentialID, tmprps.rp, tmprps.getCredential(j)!!.user))
            }
            num++
        }

        return list
    }

    fun setPIN(clientPIN: String?): String? {
        bSendAPDU("00A4040008A0000006472F000100")
        assertSW("9000")

        clintPIN.reset()

        ClientPIN(ClientPIN_API.cp_sub_getKeyAgreement)
        assertSW("9000")

        clintPIN.clientPIN = clientPIN!!
        val result = ClientPIN(ClientPIN_API.cp_sub_setPIN)
        assertSW("9000")

        return result
    }

    fun changePin(beforePIN: String?, newPIN: String?): String? {
        bSendAPDU("00A4040008A0000006472F000100")
        assertSW("9000")

        clintPIN.reset()

        ClientPIN(ClientPIN_API.cp_sub_getKeyAgreement)
        assertSW("9000")

        clintPIN.originPIN = beforePIN!!
        clintPIN.clientPIN = newPIN!!
        val result = ClientPIN(ClientPIN_API.cp_sub_changePIN)
        assertSW("9000")

        return result
    }


    fun ClientPIN(sub: String): String? {
        val result: String = clintPIN.commands(sub) as String
        if (result.substring(0, 2) == "00") {
            clintPIN.responses(sub, result)
        } else {
            printLog("ClientPIN is not successful")
        }
        return result.substring(0, 2)
    }

    fun CredentialManagement(sub: String): String? {
        return CredentialManagement(sub, ArrayList<String>())
    }

    fun CredentialManagement(sub: String, params: ArrayList<String>): String? {
        var fido_result = ""
        credMg.pinUvAuthToken = clintPIN.pinUvAuthToken
        val cmd: String = credMg.commands(sub, params) as String
        val result = makeCommand(cmd)
        fido_result = result.substring(0, 2)
        if (fido_result == "00") {
            credMg.responses(sub, result, params)
        } else {
            printLog("CredentialManagement is not successful")
        }
        return fido_result
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
            response = bSendAPDU("80100000" + Util.toHex(len_cmd_data/2) + cData + "00")
            responseData = response.substring(0, response.length-4)
        }else{
            while(off_data < len_cmd_data - 480 - 2){
                bSendAPDU("90100000" + Util.toHex(240) + cData.substring(off_data, 480))
                off_data += 480
                isLong = true
            }
            len_remain_data = len_cmd_data - off_data

            response = bSendAPDU("80100000" + Util.toHex(len_remain_data / 2) + cData.substring(off_data, off_data + len_remain_data) + "")
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
        byteAPDU = Util.atohex(stringAPDU)
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
            val tmp = Util.getHexString(resAPDU)
            sw = tmp.substring(tmp.length - 4)
            return Util.getHexString(resAPDU)
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
            printLog("IFD - " + Util.getHexString(data))
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        ra = try {
            myTag!!.transceive(data)
        } catch (e: IOException) {
            printLog("************************************")
            printLog("         NO CARD RESPONSE")
            printLog("************************************")
            throw Exception("No Card Response")
        }
        try {
            printLog("")
            printLog("ICC - " + Util.getHexString(ra))
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return ra
    }


    fun printLog(str: Any) {
        if (LOG_MODE) {
            println(str)
        } else {
            Log.v(TAG, str.toString())
        }
    }


}