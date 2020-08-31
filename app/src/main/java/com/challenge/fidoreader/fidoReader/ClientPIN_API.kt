package com.challenge.fidoreader.fidoReader

import com.challenge.fidoreader.Util.*

class ClientPIN_API: FIDO2_API(){
    override var TAG:String = "ClientPIN_API"

    var sso:SharedSecretObject = SharedSecretObject()
    var clientPIN:String = ""
    var originPIN:String = ""
    var PIN :String = ""

    companion object{
        const val cp_sub_getPINRetries = "1"
        const val cp_sub_getKeyAgreement = "2"
        const val cp_sub_setPIN = "3"
        const val cp_sub_changePIN = "4"
        const val cp_sub_getPinUvAuthTokenUsingPin = "5"
        const val cp_sub_getPinUvAuthTokenUsingUv = "6"
        const val cp_sub_getUVRetries = "7"
    }

    fun reset(){
        sso.reset()
        pinUvAuthToken = ""
    }


    fun setUserPIN(userPIN: String) {
        this.PIN = userPIN
    }

    override fun commands(sub: String): String?     {
        var cmd = ""
        var keyAgreement = ""
        var newPinEnc = ""
        var pinUvAuthParam = ""
        var pinHashEnc = ""
        when (sub) {
            cp_sub_getPINRetries -> printLog("Send Client PIN : " + "getPINRetries")
            cp_sub_getKeyAgreement -> {
                printLog("Send Client PIN : " + "getKeyAgreement")
                cmd = "06A20101020200"
            }
            cp_sub_setPIN -> {
                printLog("Send Client PIN : " + "setPIN")
                keyAgreement = "A5010203262001215820" + sso.publickey.substring(0, 64) + "225820" + sso.publickey.substring(64)
                newPinEnc = clientPIN.padding() // clientPIN -> User INPUT
                newPinEnc = aes_cbc(sso.shareSecret, newPinEnc).toString()
                val pinUvAuthParam = hmac_sha_256(sso.shareSecret, newPinEnc).toString().substring(0, 32)
                cmd = ("A5"
                        + "01" + "01" // pinUvAuthProtocol
                        + "02" + "03" // subCommand
                        + "03" + keyAgreement //keyAgreement
                        + "04" + "50" + pinUvAuthParam // pinUbAuthParam
                        + "05" + "58" + "40" + newPinEnc) // newPinEnc
                cmd = "06$cmd"
            }
            cp_sub_changePIN -> {
                printLog("Send Client PIN : " + "changePIN")
                keyAgreement = "A5010203262001215820" + sso.publickey.substring(0, 64) + "225820" + sso.publickey.substring(64)
                newPinEnc = clientPIN.padding()// clientPIN -> User INPUT (new PIN)
                newPinEnc = aes_cbc(sso.shareSecret, newPinEnc).toString()
                var pinHashEnc = originPIN.sha_256() // currentPIN
                pinHashEnc = aes_cbc(sso.shareSecret, pinHashEnc.substring(0, 32)).toString()
                pinUvAuthParam = hmac_sha_256(sso.shareSecret, newPinEnc + pinHashEnc).toString().substring(0, 32)
                cmd = ("A6"
                        + "01" + "01" // pinUvAuthProtocol
                        + "02" + "04" // subCommand
                        + "03" + keyAgreement //keyAgreement
                        + "04" + "50" + pinUvAuthParam // pinUbAuthParam
                        + "05" + "58" + "40" + newPinEnc // newPinEnc
                        + "06" + "50" + pinHashEnc) // pinHashEn (related with currentPIN)
                cmd = "06$cmd"
            }
            cp_sub_getPinUvAuthTokenUsingPin -> {
                printLog("Send Client PIN : " + "getPinUvAuthTokenUsingPin")
                keyAgreement = "A5010203262001215820" + sso.publickey.substring(0, 64) + "225820" + sso.publickey.substring(64)
                val sha = "30303030".sha_256().toString().substring(0, 16 * 2)
                printLog("sha : $sha")
                pinHashEnc = aes_cbc(sso.shareSecret, sha).toString()
                printLog("pinHashEnc : $pinHashEnc")
                cmd = ("A4"
                        + "01" + "01" //pinUvAuthProtocol
                        + "02" + "05" // subCommand index
                        + "03" + keyAgreement //keyAgreement
                        + "06" + "58" + (pinHashEnc.length / 2).toHex() + pinHashEnc) //pinHashEnc
                cmd = "06$cmd"
            }
            cp_sub_getPinUvAuthTokenUsingUv -> printLog("Send Client PIN : " + "getPinUvAuthTokenUsingUv")
            cp_sub_getUVRetries -> printLog("Send Client PIN : " + "getUVRetries")
        }

        return cmd
    }

    override fun responses(sub: String, res: String): String? {

        val jnode = getCBORDataFromResponse(res)
        if (res == "" || jnode == null) {
            return ""
        }
        when (sub) {
            cp_sub_getPINRetries -> {
            }
            cp_sub_getKeyAgreement -> {

                //get Authenticator public key
                val a_publickey = jnode["1"]["-2"].binaryValue().getHexString() + jnode["1"]["-3"].binaryValue().getHexString() //TODO get authenticator public key
                sso.generateSharedSecret(a_publickey)
                printLog(sso.toString())
                return "801000000606A20101020200"
            }
            cp_sub_setPIN -> {
            }
            cp_sub_changePIN -> {
            }
            cp_sub_getPinUvAuthTokenUsingPin -> {
                val encPINUvAuthToken = jnode["2"].binaryValue().getHexString() //TODO
                //printLog(encPINUvAuthToken);
                pinUvAuthToken = aes_cbc_dec(sso.shareSecret, encPINUvAuthToken).toString()
            }
            cp_sub_getPinUvAuthTokenUsingUv -> {
            }
            cp_sub_getUVRetries -> {
            }
        }
        return "80100000010400"
    }


    override fun commands(sub: String, params: ArrayList<String>):String? {
        throw Exception("This method is not supported")
    }

    override fun responses(sub: String, res: String, params: ArrayList<String>):String? {
        throw Exception("This method is not supported")
    }

    override fun commands(params: ArrayList<String>):String? {
        throw Exception("This method is not supported")
    }

    override fun responses(res: String, params: ArrayList<String>):String? {
        throw Exception("This method is not supported")
    }

}