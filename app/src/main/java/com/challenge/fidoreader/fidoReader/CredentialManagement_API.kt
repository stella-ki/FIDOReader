package com.challenge.fidoreader.fidoReader

import com.challenge.fidoreader.Util.MapList
import com.challenge.fidoreader.Util.Util
import com.fasterxml.jackson.databind.JsonNode

class CredentialManagement_API: FIDO2_API(){
    override var TAG:String = "CredentialManagement_API"

    companion object{
        val cm_sub_getCredsMetadata = "1"
        val cm_sub_enumerateRPsBegin = "2"
        val cm_sub_enumerateRPsGetNextRP = "3"
        val cm_sub_enumerateCredentialsBegin = "4"
        val cm_sub_enumerateCredentialsGetNextCredential = "5"
        val cm_sub_deleteCredential = "6"
    }

    var rps: MapList<String, RPs> = MapList()

    override fun commands(sub: String, params: ArrayList<String>):String? {
        var pinUvAuthParam:String = ""
        var cmd:String = ""
        var rp:String = ""
        var rpIDHash:String = ""

        when(sub){
            cm_sub_getCredsMetadata ->{
                printLog("Send Credential Management : " + "getCredsMetadata")
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "01").substring(0, 16 * 2)
                cmd = ("A3"
                        + "01" + "01" //subcommand index
                        + "03" + "01" // pinUvAuthProtocol
                        + "04" + "58" + Util.toHex(pinUvAuthParam.length / 2) + pinUvAuthParam) //pinUvAuthParam

                cmd = "41$cmd"

            }
            cm_sub_enumerateRPsBegin ->{
                printLog("Send Credential Management : " + "enumerateRPsBegin")
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "02").substring(0, 16 * 2)
                cmd = ("A3"
                        + "01" + "02" //subcommand index
                        + "03" + "01" // pinUvAuthProtocol
                        + "04" + "58" + Util.toHex(pinUvAuthParam.length / 2) + pinUvAuthParam) //pinUvAuthParam

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
                rp = Util.convertTohex(rp)
                rpIDHash = Util.sha_256(rp)
                rpIDHash = "A10158" + Util.toHex(rpIDHash.length / 2) + rpIDHash
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "04$rpIDHash").substring(0, 16 * 2)

                cmd = ("A4"
                        + "01" + "04" //subcommand index
                        + "02" + rpIDHash //subcommand params
                        + "03" + "01" // pinUvAuthProtocol
                        + "04" + "58" + Util.toHex(pinUvAuthParam.length / 2) + pinUvAuthParam) //pinUvAuthParam


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
                credentialID = "A1" + "02" + "A2" + "64" + "74797065" + "6A" + "7075626C69632D6B6579" + "62" + "6964" + "58" + Util.toHex(credentialID.length / 2) + credentialID
                pinUvAuthParam = Util.HMACSHA256(pinUvAuthToken, "06$credentialID").substring(0, 16 * 2)

                cmd = ("A4"
                        + "01" + "06" //subcommand index
                        + "02" + credentialID //subcommand params
                        + "03" + "01" // pinUvAuthProtocol
                        + "04" + "58" + Util.toHex(pinUvAuthParam.length / 2) + pinUvAuthParam) //pinUvAuthParam

                cmd = "41$cmd"
            }else -> {
                throw Exception("Subcommand value is wrong")
            }

        }

        return cmd;

    }

    override fun responses(sub: String, res: String, params: ArrayList<String>):String? {
        var jnode: JsonNode? = getCBORDataFromResponse(res) ?: return null

        var rp: String = ""
        var rpIDHash: String = ""
        var user: String = ""
        var CredentialID: String = ""
        var publicKey: String = ""

        var tmpRPs:RPs? = null

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
                tmpRPs = rps[rp]
                user = jnode!!["6"]["name"].toString()
                CredentialID = Util.getHexString(jnode["7"]["id"].binaryValue())
                publicKey = jnode["8"].toString()
                val totalcredCount = jnode["9"].toString().toInt()
                tmpRPs.setCredentialExpectedCnt(totalcredCount - 1)
                tmpRPs.addCredential(Credential(user, CredentialID, publicKey))
            }
            cm_sub_enumerateCredentialsGetNextCredential->{
                rp = params[0]
                tmpRPs = rps[rp]
                user = jnode!!["6"]["name"].toString()
                CredentialID = Util.getHexString(jnode["7"]["id"].binaryValue())
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