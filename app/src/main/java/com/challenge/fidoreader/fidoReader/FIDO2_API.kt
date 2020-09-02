package com.challenge.fidoreader.fidoReader

import android.util.Log
import com.challenge.fidoreader.Util.atohex
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

    fun printLog(str: Any?){
        printLog(str.toString())
    }

    fun printLog(str: String){
        if(LOG_MODE){
            System.out.println(str.toString())
        }else{
            Log.v(TAG, str.toString())
        }
    }



}