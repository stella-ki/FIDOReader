package com.challenge.fidoreader.fidoReader

import android.util.Log
import com.challenge.fidoreader.Util.Util
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import java.io.ByteArrayInputStream

abstract class FIDO2_API{
    open val TAG = "FIDO2_API"
    open var LOG_MODE = false


    abstract fun commands(params: ArrayList<String>):String?
    abstract fun responses(res:String, params: ArrayList<String>):String?

    abstract fun commands(sub:String, params: ArrayList<String>):String?
    abstract fun responses(sub:String, res:String, params: ArrayList<String>):String?

    abstract fun commands(sub:String):String?
    abstract fun responses(sub:String, res:String):String?

    var pinUvAuthToken:String = ""

    fun printLog(str: Object?){
        printLog(str.toString())
    }

    fun printLog(str: String){
        if(LOG_MODE){
            System.out.println(str.toString())
        }else{
            Log.v(TAG, str.toString())
        }
    }

    fun getCBORDataFromResponse(res: String): JsonNode? {
        //printLog(res);
        var res = res
        res = res.replace(" ".toRegex(), "")
        res = res.substring(2)
        return if (res == "") {
            null
        } else getCBORData(res)
    }

    fun getCBORData(res:String): JsonNode?{
        var bais: ByteArrayInputStream = ByteArrayInputStream(Util.atohex(res))
        var cf : CBORFactory = CBORFactory()
        var mapper = ObjectMapper(cf)
        val jnode = mapper.readValue(bais, JsonNode::class.java)
        return jnode
    }



}