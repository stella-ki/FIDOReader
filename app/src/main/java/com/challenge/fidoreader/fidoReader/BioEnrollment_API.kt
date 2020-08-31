package com.challenge.fidoreader.fidoReader

import com.challenge.fidoreader.Util.*
import com.challenge.fidoreader.fagment.data.FingerItem
import com.fasterxml.jackson.databind.JsonNode


class BioEnrollment_API : FIDO2_API()  {
    override var TAG:String = "BioEnrollment_API"

    companion object{
        const val be_getbiomodality                   =  "0"
        const val be_sub_enrollBegin                  =  "1"
        const val be_sub_enrollCaptureNextSample      =  "2"
        const val be_sub_cancelCurrentEnrollment      =  "3"
        const val be_sub_emurateEnrollments           =  "4"
        const val be_sub_setFriendlyName              =  "5"
        const val be_sub_removeEnrollment             =  "6"
        const val be_sub_getFingerprintSensorInfo     =  "7"
    }

    var fingerList = ArrayList<FingerItem>()
    var lastEnrollSampleStatus = "00"
    var remainingSamples = "00"
    var templateID = ""

    init {
        pinUvAuthToken = ""
    }

    override fun commands(sub: String, params: ArrayList<String>):String? {
        var pinUvAuthParam:String = ""
        var cmd:String = ""

        var templateID = ""
        var templateFriendlyName = ""
        var timeoutMiliseconds = ""
        var subparam = ""

        when (sub){
            be_sub_enrollBegin -> {
                printLog("Send BioEnrollment : "+"enrollBegin")
                timeoutMiliseconds = params[0].replace("\"".toRegex(), "")
                if(timeoutMiliseconds != null){
                    timeoutMiliseconds = "A1" + "03" + "19" + timeoutMiliseconds
                }else{
                    timeoutMiliseconds = "A0"
                }
                pinUvAuthParam = HMACSHA256(pinUvAuthToken, "01" + "01" + timeoutMiliseconds).substring(0, 16*2)
                cmd = ("A5"
                    + "01" + "01" //modality
                    + "02" + "01" //sub command index
                    + "03" + timeoutMiliseconds // subcommand params
                    + "04" + "01" // pinUvAuthProtocol
                    + "05" + "58"+ (pinUvAuthParam.length/2).toHex() + pinUvAuthParam) //pinUvAuthParam
                cmd = "40$cmd"
            }
            be_sub_enrollCaptureNextSample -> {
                printLog("Send BioEnrollment : "+"enrollCaptureNextSample")

                timeoutMiliseconds = params[0].replace("\"".toRegex(), "")
                templateID = params[1].replace("\"".toRegex(), "")
                if(timeoutMiliseconds != null){
                    templateID = "01" + "41" + templateID
                    timeoutMiliseconds = "03" + "19" + timeoutMiliseconds
                    subparam = "A2" + templateID + timeoutMiliseconds
                }else{
                    templateID = "01" + "41" + templateID
                    subparam = "A1" + templateID
                }
                pinUvAuthParam = HMACSHA256(pinUvAuthToken, "01" + "02" + subparam).substring(0, 16*2)
                cmd = ("A5"
                    + "01" + "01" //modality
                    + "02" + "02" //sub command index
                    + "03" + subparam // subcommand params
                    + "04" + "01" // pinUvAuthProtocol
                    + "05" + "58"+ (pinUvAuthParam.length / 2).toHex() + pinUvAuthParam) //pinUvAuthParam
                cmd = "40$cmd"
            }
            be_sub_cancelCurrentEnrollment -> {
                printLog("Send BioEnrollment : "+"cancelCurrentEnrollment")
                cmd = ("A2"
                    + "01" + "01" //modality
                    + "02" + "03") //subcommand index
                cmd = "40$cmd"
            }
            be_sub_emurateEnrollments -> {
                printLog("Send BioEnrollment : "+"emurateEnrollments")
                pinUvAuthParam = HMACSHA256(pinUvAuthToken, "01" + "04")!!.substring(0, 16*2)

                cmd = ("A4"
                    + "01" + "01" //modality
                    + "02" + "04" //sub command index
                    + "04" + "01" // pinUvAuthProtocol
                    + "05" + "58"+ (pinUvAuthParam.length/2).toHex() + pinUvAuthParam) //pinUvAuthParam
                cmd = "40$cmd"
            }
            be_sub_setFriendlyName -> {
                printLog("Send BioEnrollment : "+"setFriendlyName")
                templateID = params[0].replace("\"".toRegex(), "")
                templateFriendlyName = params[1].replace("\"".toRegex(), "")
                templateFriendlyName = templateFriendlyName.ascii();
                printLog("Send BioEnrollment : setFriendlyName : $templateFriendlyName");

                templateID = "01" + "41" + templateID
                templateFriendlyName = "02" + getLengthUTFString(templateFriendlyName.length/2) + templateFriendlyName
                subparam = "A2" + templateID + templateFriendlyName

                pinUvAuthParam = HMACSHA256(pinUvAuthToken, "01" + "05" + subparam)!!.substring(0, 16*2)
                cmd = ("A5"
                    + "01" + "01" //modality
                    + "02" + "05" //sub command index
                    + "03" + subparam // subcommand params
                    + "04" + "01" // pinUvAuthProtocol
                    + "05" + "58"+ (pinUvAuthParam.length/2).toHex() + pinUvAuthParam) //pinUvAuthParam
                cmd = "40$cmd"
            }
            be_sub_removeEnrollment -> {
                printLog("Send BioEnrollment : "+"be_sub_removeEnrollment ")
                templateID = params[0].replace("\"".toRegex(), "")

                templateID = "01" + "41" + templateID
                subparam = "A1" + templateID + templateFriendlyName

                pinUvAuthParam = HMACSHA256(pinUvAuthToken, "01" + "06" + subparam)!!.substring(0, 16*2)
                cmd = ("A5"
                    + "01" + "01" //modality
                    + "02" + "06" //sub command index
                    + "03" + subparam // subcommand params
                    + "04" + "01" // pinUvAuthProtocol
                    + "05" + "58"+ (pinUvAuthParam.length/2).toHex() + pinUvAuthParam) //pinUvAuthParam
                cmd = "40$cmd"
            }

            be_sub_getFingerprintSensorInfo -> {
                printLog("Send BioEnrollment : "+"getFingerprintSensorInfo")
                cmd = ("A2"
                        + "01" + "01" //modality
                        + "02" + "07")//subcommand index
                cmd = "40$cmd"
            }
            be_getbiomodality -> {
                printLog("Send BioEnrollment : "+"be_getbiomodality")
                cmd = ("A1"
                        + "06" + "F5") //modality
                cmd = "40$cmd"
            }
            else ->{
                throw Exception("Subcommand value is wrong")
            }
        }

        return cmd
    }

    override fun responses(sub: String, res: String, params: ArrayList<String>):String? {
        var jnode: JsonNode? = getCBORDataFromResponse(res) ?: return null

        when (sub){
            be_getbiomodality -> {
                var modality = jnode!!.get("1").toString().replace("\"".toRegex(), "")
            }
            be_sub_enrollBegin -> {
                templateID = jnode!!.get("4").binaryValue().byteArrayToHexString();
                lastEnrollSampleStatus = jnode!!.get("5").toString();
                remainingSamples = jnode!!.get("6").toString();
            }
            be_sub_enrollCaptureNextSample -> {
                lastEnrollSampleStatus = jnode!!.get("5").toString();
                remainingSamples = jnode!!.get("6").toString();
            }
            be_sub_cancelCurrentEnrollment -> {

            }
            be_sub_emurateEnrollments -> {
                fingerList.clear();
                val list = jnode!!["7"]
                val cntFP = list.size()

                for (i in 0 until cntFP) {
                    fingerList.add(FingerItem(list[i]["1"].binaryValue().byteArrayToHexString(), list[i]["2"].toString().replace("\"".toRegex(), ""), 1))
                }
            }
            be_sub_setFriendlyName -> {

            }
            be_sub_removeEnrollment -> {

            }
            be_sub_getFingerprintSensorInfo -> {

            }
            else -> {
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