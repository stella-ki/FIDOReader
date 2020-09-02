package com.challenge.fidoreader.fidoReader

import android.util.Log
import com.challenge.fidoreader.Exception.UserException
import com.challenge.fidoreader.fagment.data.CredentialItem
import com.challenge.fidoreader.fagment.data.FingerItem
import com.challenge.fidoreader.fidoReader.data.CBOR_CODE_CTAP1_ERR_SUCCESS
import com.challenge.fidoreader.fidoReader.data.CBOR_CODE_CTAP2_ERR_INVALID_OPTION
import com.challenge.fidoreader.fidoReader.data.CBOR_CODE_CTAP2_ERR_NO_CREDENTIALS
import com.challenge.fidoreader.fidoReader.data.RPs
import com.challenge.fidoreader.reader.Sender
import java.util.*


class Authenticator(var sender: Sender) {
    val TAG = "Authenticator"
    var LOG_MODE = false

    var tmpparams: ArrayList<String> = ArrayList<String>()

    var credMg: CredentialManagement_API = CredentialManagement_API()
    var clintPIN: ClientPIN_API = ClientPIN_API()
    var bio_api : BioEnrollment_API = BioEnrollment_API()

    fun setUserPIN(userPIN: String) :String{
        clintPIN.PIN = userPIN

        sender.startFIDO()

        clintPIN.reset()
        //Get Info
        sender.sendFIDO("04")

        ClientPIN(ClientPIN_API.cp_sub_getKeyAgreement)

        val result = ClientPIN(ClientPIN_API.cp_sub_getPinUvAuthTokenUsingPin)

        return result as String
    }

    fun getInfo():String{
        printLog("getInfo")
        sender.startFIDO()

        clintPIN.reset()

        //Get Info
        var result : String = sender.sendFIDO("04")
        result = result.substring(2, result.length)
        printLog("getInfo :" + result)

        return result
    }

    fun reset(): Boolean {
        printLog("reset")

        sender.startFIDO()

        //reset
        var result = sender.sendFIDO("07")

        if(result.length >= 2){
            result = result.substring(0,2)
        }

        return result == CBOR_CODE_CTAP1_ERR_SUCCESS
    }


    fun deleteCredential(cred_id: String): Boolean {
        printLog("deleteCredential")

        sender.startFIDO()

        clintPIN.reset()
        //Get Info
        sender.sendFIDO("04")

        ClientPIN(ClientPIN_API.cp_sub_getKeyAgreement)

        ClientPIN(ClientPIN_API.cp_sub_getPinUvAuthTokenUsingPin)

        tmpparams.clear()
        tmpparams.add(cred_id)
        val fido_result: String = CredentialManagement(CredentialManagement_API.cm_sub_deleteCredential, tmpparams) as String
        if (fido_result != CBOR_CODE_CTAP1_ERR_SUCCESS) {
            throw UserException("Credential deletion is failed")
        }
        return true
    }

    fun readEnrollInformation(): ArrayList<FingerItem>? {
        printLog("deleteCredential")

        val fido_result: String = BioEnrollment(BioEnrollment_API.be_sub_emurateEnrollments) as String

        if(fido_result == CBOR_CODE_CTAP2_ERR_INVALID_OPTION){
            throw UserException(CBOR_CODE_CTAP2_ERR_INVALID_OPTION);
        }

        if (fido_result != CBOR_CODE_CTAP1_ERR_SUCCESS){
            throw UserException("BioEnrollment is failed")
        }
        return bio_api.fingerList
    }


    fun deleteEnroll(templateID: String): Boolean {
        printLog("deleteEnroll")

        sender.startFIDO()

        clintPIN.reset()
        //Get Info
        sender.sendFIDO("04")

        ClientPIN(ClientPIN_API.cp_sub_getKeyAgreement)
        
        ClientPIN(ClientPIN_API.cp_sub_getPinUvAuthTokenUsingPin)

        tmpparams.clear()
        tmpparams.add(templateID)
        val fido_result: String = BioEnrollment(BioEnrollment_API.be_sub_removeEnrollment, tmpparams)
        if (fido_result != CBOR_CODE_CTAP1_ERR_SUCCESS) {
            throw UserException("BioEnrollment is failed")
        }
        return true
    }


    fun changeEnrollName(templateID: String, new_name: String): Boolean {
        printLog("changeEnrollName")

        sender.startFIDO()

        clintPIN.reset()
        //Get Info
        sender.sendFIDO("04")

        ClientPIN(ClientPIN_API.cp_sub_getKeyAgreement)
        
        ClientPIN(ClientPIN_API.cp_sub_getPinUvAuthTokenUsingPin)
        

        tmpparams.clear()
        tmpparams.add(templateID)
        tmpparams.add(new_name)
        val fido_result: String = BioEnrollment(BioEnrollment_API.be_sub_setFriendlyName, tmpparams)
        if (fido_result != CBOR_CODE_CTAP1_ERR_SUCCESS) {
            throw UserException("BioEnrollment is failed")
        }
        return true
    }

    fun getPINToken(): Boolean {
        printLog("getPinUvAuthTokenUsingPin")

        sender.startFIDO()

        clintPIN.reset()
        //Get Info
        sender.sendFIDO("04")

        ClientPIN(ClientPIN_API.cp_sub_getKeyAgreement)
        
        ClientPIN(ClientPIN_API.cp_sub_getPinUvAuthTokenUsingPin)
        
        return true
    }

    fun enrollfinger(): String {
        printLog("enrollfinger")

        tmpparams.clear()
        tmpparams.add("4E20")
        val fido_result: String = BioEnrollment(BioEnrollment_API.be_sub_enrollBegin,tmpparams)

        if (fido_result != CBOR_CODE_CTAP1_ERR_SUCCESS) {
            throw UserException("BioEnrollment is failed")
        }
        //val cnt = Integer.valueOf(bio_api.remainingSamples, 16)

        return bio_api.templateID
    }


    fun enrollNextfinger(templateID: String): Int {
        printLog("enrollCaptureNextSample")

        tmpparams.clear()
        tmpparams.add("4E20")
        tmpparams.add(templateID)
        val fido_result: String = BioEnrollment(BioEnrollment_API.be_sub_enrollCaptureNextSample, tmpparams)

        if (fido_result != CBOR_CODE_CTAP1_ERR_SUCCESS) {
            throw UserException("BioEnrollment is failed")
        }
        val cnt = Integer.valueOf(bio_api.remainingSamples, 16)
        printLog("remain : $cnt")
        return cnt
    }

    fun enrollCancel(): Boolean {
        printLog("enrollCancel")
        val fido_result: String = BioEnrollment(BioEnrollment_API.be_sub_cancelCurrentEnrollment) as String
        if (fido_result != CBOR_CODE_CTAP1_ERR_SUCCESS) {
            throw UserException("BioEnrollment is failed")
        }
        return true
    }

    fun getCredentialList(): ArrayList<CredentialItem>? {
        printLog("getCredentialList")

        val list = ArrayList<CredentialItem>()

        var num = 0
        val fido_result: String = CredentialManagement(CredentialManagement_API.cm_sub_enumerateRPsBegin) as String
        if (fido_result == CBOR_CODE_CTAP2_ERR_NO_CREDENTIALS) {
            throw UserException(CBOR_CODE_CTAP2_ERR_NO_CREDENTIALS)
        }
        while (num < credMg.rps.expectedSize()) {
            CredentialManagement(CredentialManagement_API.cm_sub_enumerateRPsGetNextRP)
            num++
        }

        var rp = "" //get First key value which is rp
        num = 0
        while (num < credMg.rps.getSize()) {
            rp = credMg.rps.getKey(num) as String //get First key value which is rp
            printLog("Read Credential about RP : $rp")

            tmpparams.clear()
            tmpparams.add(rp)
            CredentialManagement(CredentialManagement_API.cm_sub_enumerateCredentialsBegin, tmpparams)
            val credCunt = credMg.rps[rp]!!.getCredentialExpectedCnt()
            for (j in 0 until credCunt) {

                tmpparams.clear()
                tmpparams.add(rp)
                CredentialManagement(CredentialManagement_API.cm_sub_enumerateCredentialsGetNextCredential, tmpparams)
            }
            num++
        }

        num = 0
        while (num < credMg.rps.getSize()) {
            val tmprps = credMg.rps.getValue(num) as RPs
            for (j in tmprps.credentials.indices) {
                list.add(CredentialItem(tmprps.getCredential(j)!!.credentialID, tmprps.rp, tmprps.getCredential(j)!!.user))
            }
            num++
        }

        return list
    }

    fun setPIN(clientPIN: String): String? {
        sender.startFIDO()

        clintPIN.reset()

        ClientPIN(ClientPIN_API.cp_sub_getKeyAgreement)

        clintPIN.clientPIN = clientPIN
        val result = ClientPIN(ClientPIN_API.cp_sub_setPIN)

        return result
    }

    fun changePin(beforePIN: String?, newPIN: String?): String? {
        sender.startFIDO()

        clintPIN.reset()

        ClientPIN(ClientPIN_API.cp_sub_getKeyAgreement)

        clintPIN.originPIN = beforePIN!!
        clintPIN.clientPIN = newPIN!!
        val result = ClientPIN(ClientPIN_API.cp_sub_changePIN)

        return result
    }


    fun ClientPIN(sub: String): String? {
        val cmd: String = clintPIN.commands(sub) as String

        val result = sender.sendFIDO(cmd)

        if (result.substring(0, 2) == CBOR_CODE_CTAP1_ERR_SUCCESS) {
            clintPIN.responses(sub, result)
        } else {
            printLog("ClientPIN is not successful")
        }
        return result.substring(0, 2)
    }


    fun BioEnrollment(sub: String): String? {
        return BioEnrollment(sub, ArrayList<String>())
    }

    fun BioEnrollment(sub: String, params: ArrayList<String>): String {
        var fido_result = ""
        bio_api.pinUvAuthToken = clintPIN.pinUvAuthToken

        val cmd = bio_api.commands(sub, params) as String

        val result = sender.sendFIDO(cmd)
        if(result.length >= 2 ){
            fido_result = result.substring(0,2);
        }

        if (fido_result == CBOR_CODE_CTAP1_ERR_SUCCESS) {
            bio_api.responses(sub, result, params)
        } else {
            printLog("BioEnrollment is not successful")
        }
        return fido_result
    }

    fun CredentialManagement(sub: String): String? {
        return CredentialManagement(sub, ArrayList<String>())
    }

    fun CredentialManagement(sub: String, params: ArrayList<String>): String? {
        var fido_result = ""
        credMg.pinUvAuthToken = clintPIN.pinUvAuthToken

        val cmd: String = credMg.commands(sub, params) as String

        val result = sender.sendFIDO(cmd)
        if(result.length >= 2 ){
            fido_result = result.substring(0,2);
        }
        
        if (fido_result == CBOR_CODE_CTAP1_ERR_SUCCESS) {
            credMg.responses(sub, result, params)
        } else {
            printLog("CredentialManagement is not successful")
        }
        return fido_result
    }


    fun printLog(str: Any) {
        if (LOG_MODE) {
            println(str)
        } else {
            Log.v(TAG, str.toString())
        }
    }


}