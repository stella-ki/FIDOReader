package com.challenge.fidoreader.fidoReader

import com.challenge.fidoreader.Util.*
import com.fasterxml.jackson.databind.JsonNode

class CredentialManagement_API: FIDO2_API(){
    override var TAG:String = "CredentialManagement_API"

    companion object{
        const val cm_sub_getCredsMetadata = "1"
        const val cm_sub_enumerateRPsBegin = "2"
        const val cm_sub_enumerateRPsGetNextRP = "3"
        const val cm_sub_enumerateCredentialsBegin = "4"
        const val cm_sub_enumerateCredentialsGetNextCredential = "5"
        const val cm_sub_deleteCredential = "6"
    }

    var rps: MapList<String, RPs> = MapList()

    override fun commands(sub: String, params: ArrayList<String>):String? {
        var pinUvAuthParam= ""
        var cmd= ""
        var rp= ""
        var rpIDHash= ""

        when(sub){
            cm_sub_getCredsMetadata ->{
                printLog("Send Credential Management : " + "getCredsMetadata")
                pinUvAuthParam = HMACSHA256(pinUvAuthToken, "01").toString().substring(0, 16 * 2)
                cmd = ("A3"
                        + "01" + "01" //subcommand index
                        + "03" + "01" // pinUvAuthProtocol
                        + "04" + "58" + (pinUvAuthParam.length / 2).toHex() + pinUvAuthParam) //pinUvAuthParam

                cmd = "41$cmd"

            }
            cm_sub_enumerateRPsBegin ->{
                printLog("Send Credential Management : " + "enumerateRPsBegin")
                pinUvAuthParam = HMACSHA256(pinUvAuthToken, "02").toString().substring(0, 16 * 2)
                cmd = ("A3"
                        + "01" + "02" //subcommand index
                        + "03" + "01" // pinUvAuthProtocol
                        + "04" + "58" + (pinUvAuthParam.length / 2).toHex() + pinUvAuthParam) //pinUvAuthParam

                cmd = "41$cmd"
            }
            cm_sub_enumerateRPsGetNextRP ->{
                printLog("Send Credential Management : " + "enumerateRPsGetNextRP")
                cmd = ("A1"
                        + "01" + "03") //subcommand index

                cmd = "41$cmd"
            }
            cm_sub_enumerateCredentialsBegin ->{
                printLog("Send Credential Management : " + "enumerateCredentialsBegin")
                rp = params[0]
                rp = rp.ascii()
                rpIDHash = rp.sha_256()
                rpIDHash = "A10158" + (rpIDHash.length / 2).toHex() + rpIDHash
                pinUvAuthParam = HMACSHA256(pinUvAuthToken, "04$rpIDHash").toString().substring(0, 16 * 2)

                cmd = ("A4"
                        + "01" + "04" //subcommand index
                        + "02" + rpIDHash //subcommand params
                        + "03" + "01" // pinUvAuthProtocol
                        + "04" + "58" + (pinUvAuthParam.length / 2).toHex() + pinUvAuthParam) //pinUvAuthParam


                cmd = "41$cmd"
            }
            cm_sub_enumerateCredentialsGetNextCredential ->{
                printLog("Send Credential Management : " + "enumerateCredentialsGetNextCredential")
                cmd = ("A1"
                        + "01" + "05") //subcommand index

                cmd = "41$cmd"
            }
            cm_sub_deleteCredential ->{
                printLog("Send Credential Management : " + "deleteCredential " + params[0])
                var credentialID = params[0].replace("\"".toRegex(), "")
                // = Util.convertTohex(credentialID);
                credentialID = "A1" + "02" + "A2" + "64" + "74797065" + "6A" + "7075626C69632D6B6579" + "62" + "6964" + "58" + (credentialID.length / 2).toHex() + credentialID
                pinUvAuthParam = HMACSHA256(pinUvAuthToken, "06$credentialID").toString().substring(0, 16 * 2)

                cmd = ("A4"
                        + "01" + "06" //subcommand index
                        + "02" + credentialID //subcommand params
                        + "03" + "01" // pinUvAuthProtocol
                        + "04" + "58" + (pinUvAuthParam.length / 2).toHex() + pinUvAuthParam) //pinUvAuthParam

                cmd = "41$cmd"
            }else -> {
                throw Exception("Subcommand value is wrong")
            }

        }

        return cmd;

    }

    override fun responses(sub: String, res: String, params: ArrayList<String>):String? {
        var jnode: JsonNode? = getCBORDataFromResponse(res) ?: return null

        var rp = ""
        var rpIDHash = ""
        var user = ""
        var CredentialID = ""
        var publicKey = ""

        var tmpRPs:RPs?

        when(sub){
            cm_sub_getCredsMetadata->{

            }
            cm_sub_enumerateRPsBegin->{
                rps.clear()
                rp = jnode!!["3"]["id"].toString().replace("\"".toRegex(), "")
                rpIDHash = jnode["4"].toString()
                val totalRpCount = jnode["5"].toString().toInt()
                rps.add(rp, RPs(rp, rpIDHash))
                rps.setExpectedSize(totalRpCount - 1)
            }
            cm_sub_enumerateRPsGetNextRP->{
                rp = jnode!!["3"]["id"].toString().replace("\"".toRegex(), "")
                rpIDHash = jnode["4"].toString()
                rps.add(rp, RPs(rp, rpIDHash))
            }
            cm_sub_enumerateCredentialsBegin->{
                rp = params[0]
                tmpRPs = rps[rp] as RPs
                user = jnode!!["6"]["name"].toString()
                CredentialID = jnode["7"]["id"].binaryValue().getHexString()
                publicKey = jnode["8"].toString()
                val totalcredCount = jnode["9"].toString().toInt()
                tmpRPs.setCredentialExpectedCnt(totalcredCount - 1)
                tmpRPs.addCredential(Credential(user, CredentialID, publicKey))
            }
            cm_sub_enumerateCredentialsGetNextCredential->{
                rp = params[0]
                tmpRPs = rps[rp] as RPs
                user = jnode!!["6"]["name"].toString()
                CredentialID = jnode["7"]["id"].binaryValue().getHexString()
                publicKey = jnode["8"].toString()
                tmpRPs.addCredential(Credential(user, CredentialID, publicKey))
            }
            cm_sub_deleteCredential->{

            }
            else->{
                throw Exception("Subcommand value is wrong")
            }
        }
        return ""
    }

    override fun commands(sub: String): String? {
        throw Exception("This method is not supported")
    }

    override fun responses(sub: String, res: String): String? {
        throw Exception("This method is not supported")
    }

    override fun commands(params: ArrayList<String>):String? {
        throw Exception("This method is not supported")
    }

    override fun responses(res: String, params: ArrayList<String>):String? {
        throw Exception("This method is not supported")
    }

}