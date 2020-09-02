package com.challenge.fidoreader.Util

import android.os.Parcelable
import com.challenge.fidoreader.reader.CardReader
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

lateinit var cardReader: CardReader

val ZERO = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)


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
    var bais: ByteArrayInputStream = ByteArrayInputStream(res.atohex())
    var cf : CBORFactory = CBORFactory()
    var mapper = ObjectMapper(cf)
    val jnode = mapper.readValue(bais, JsonNode::class.java)
    return jnode
}


fun getATRLeString(data: ByteArray): String {
    return String.format("%02X", data.size or 0x80)
}

fun getATRXorString(b: ByteArray): String {
    var Lrc = b.size or 0x80
    Lrc = Lrc xor 0x81
    for (i in b.indices) {
        Lrc = Lrc xor (b[i].toInt() and 0xFF)
    }
    return String.format("%02X", Lrc)
}

fun hmac_sha_256(key: String, input: String): String {
    val hasher = Mac.getInstance("HmacSHA256")
    hasher.init(SecretKeySpec(key.hexStringToByteArray(), "HmacSHA256"))
    val hash = hasher.doFinal(input.hexStringToByteArray())
    return hash.byteArrayToHexString()
}


fun aes_cbc(keyData: ByteArray, str: ByteArray): ByteArray {
    val key: SecretKey
    val cipher: Cipher
    var cipherText: ByteArray = byteArrayOf(0x00.toByte())
    try {
        key = SecretKeySpec(keyData, "AES")
        cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(ZERO))
        cipherText = cipher.doFinal(str)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return cipherText
}

fun aes_cbc(key: String, str: String): String {
    val keyData = key.hexStringToByteArray()
    val encrypted = aes_cbc(keyData, str.hexStringToByteArray())
    return encrypted.byteArrayToHexString()
}

fun aes_cbc_dec(keyData: ByteArray, str: ByteArray): ByteArray {
    val key: SecretKey
    val cipher: Cipher
    var cipherText: ByteArray = byteArrayOf(0x00.toByte())
    try {
        key = SecretKeySpec(keyData, "AES")
        cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(ZERO))
        cipherText = cipher.doFinal(str)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return cipherText
}

fun aes_cbc_dec(key: String, str: String): String{
    val keyData = key.hexStringToByteArray()
    val encrypted = aes_cbc_dec(keyData, str.hexStringToByteArray())
    return encrypted.byteArrayToHexString()
}

fun HMACSHA256(key: String, input: String): String {
    val hasher = Mac.getInstance("HmacSHA256")
    hasher.init(SecretKeySpec(key.hexStringToByteArray(), "HmacSHA256"))
    val hash = hasher.doFinal(input.hexStringToByteArray())
    return hash.byteArrayToHexString()
}

fun getLengthUTFString(length: Int): String? {
    return if (length > 0x17) {
        "78" + length.toHex()
    } else {
        (0x60 + length).toHex()
    }
}

fun ByteArray.getHexString(): String{
    var szDataStr = ""
    for (ii in this.indices) {
        szDataStr += String.format("%02X", this[ii].toInt() and 0xFF)
    }
    return szDataStr
}


fun String.atohex() : ByteArray {
    var data = this
    val hexchars = "0123456789abcdef"
    data = data.replace(" ".toRegex(), "").toLowerCase()

    val hex = ByteArray(data.length / 2)
    var ii = 0
    while (ii < data.length) {
        val i1 = hexchars.indexOf(data[ii])
        val i2 = hexchars.indexOf(data[ii + 1])
        hex[ii / 2] = (i1 shl 4 or i2).toByte()
        ii += 2
    }
    return hex
}

fun String.hexStringToByteArray() : ByteArray {
    var s = this
    val bytestream = ByteArrayOutputStream()

    val p: Pattern = Pattern.compile("[^\\s\\da-fA-F]");
    val m: Matcher = p.matcher(s)

    if(m.find()){
        throw Exception("String format is not Hex")
    }
    s = s.replace("\\s+".toRegex(), "")
    /*if(s.length % 2 != 0){
        return null
    }*/
    var i: Int = 0
    while(i < s.length){
        bytestream.write(s.substring(i, i+2).toInt(16))
        i += 2
    }
    return bytestream.toByteArray()
}

fun String.sha_256() : String {
    return this.hexStringToByteArray().sha_256().byteArrayToHexString()
}

fun ByteArray.sha_256() : ByteArray {
    return MessageDigest.getInstance("SHA-256").digest(this)
}
fun ByteArray.byteArrayToHexString() : String {
    var sb: StringBuilder = StringBuilder()

    for (i in this.indices) {
        val tmp = Integer.toHexString(this[i].toInt() and 0xFF).toUpperCase()
        if (tmp.length == 1) {
            sb.append("0$tmp")
        } else {
            sb.append(tmp)
        }
    }

    return sb.toString()
}

fun String.padding() : String {
    var str = this
    if (str.length / 2 < 64) {
        while (str.length / 2 != 64) {
            str += "00"
        }
    }
    return str
}

//convertTohex
fun String.ascii() : String {
    val chars = this.toCharArray()
    val sb = StringBuffer()
    for (i in chars.indices) {
        sb.append(Integer.toHexString(chars[i].toInt()))
    }
    return sb.toString().toUpperCase()
}


fun Int.toHex() : String {
    var n = Integer.toHexString(this).toUpperCase()
    n = if (n.length % 2 == 1) "0$n" else n
    return n
}


data class ParcelableActivityData(var cls : Class<Any>,
                                  var isEnd : Boolean,
                                  var errormsg :String,
                                  var listkeyword : String){

}