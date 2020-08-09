package com.challenge.fidoreader.Util

import java.io.ByteArrayOutputStream
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Util {

    /*public fun szByteHex2String(datain: Byte): String{
        val CHARS0F = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")

        val idata: Int = datain.toInt() and 0xFF
        val nibble_1 = idata shr 0x04 and 0x0F
        val nibble_2 = idata and 0x0F

        return CHARS0F[nibble_1] + CHARS0F[nibble_2]
    }*/

    companion object{
        val ZERO = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)

        @Throws(Exception::class)
        fun getHexString(data: ByteArray): String? {
            var szDataStr = ""
            for (ii in data.indices) {
                szDataStr += String.format("%02X", data[ii].toInt() and 0xFF)
            }
            return szDataStr
        }

        fun hexStringToByteArray(s: String): ByteArray?{
            var s = s
            val bytestream = ByteArrayOutputStream()

            val p: Pattern = Pattern.compile("[^\\s\\da-fA-F]");
            val m: Matcher = p.matcher(s)

            if(m.find()){
                return null
            }
            s = s.replace("\\s+".toRegex(), "")
            if(s.length % 2 != 0){
                return null
            }
            var i: Int = 0
            while(i < s.length){
                bytestream.write(s.substring(i, i+2).toInt(16))
                i += 2
            }
            return bytestream.toByteArray()
        }

        fun byteArrayToHexString(bytes: ByteArray): String{
            var sb: StringBuilder = StringBuilder()

            for (i in bytes.indices) {
                val tmp = Integer.toHexString(bytes[i].toInt() and 0xFF).toUpperCase()
                if (tmp.length == 1) {
                    sb.append("0$tmp")
                } else {
                    sb.append(tmp)
                }
            }

            return sb.toString()
        }

        @Throws(java.lang.Exception::class)
        fun getATRLeString(data: ByteArray): String? {
            return String.format("%02X", data.size or 0x80)
        }

        @Throws(java.lang.Exception::class)
        fun getATRXorString(b: ByteArray): String? {
            var Lrc = 0x00
            Lrc = b.size or 0x80
            Lrc = Lrc xor 0x81
            for (i in b.indices) {
                Lrc = Lrc xor (b[i].toInt() and 0xFF)
            }
            return String.format("%02X", Lrc)
        }

        fun atohex(data: String?): ByteArray? {
            var data = data
            val hexchars = "0123456789abcdef"
            data = data!!.replace(" ".toRegex(), "").toLowerCase()
            if (data == null) {
                return null
            }
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

        @Throws(java.lang.Exception::class)
        fun sha_256(plainText: ByteArray?): ByteArray? {
            val sha = MessageDigest.getInstance("SHA-256")
            sha.update(plainText)
            return sha.digest()
        }

        @Throws(java.lang.Exception::class)
        fun sha_256(plainText: String?): String? {
            return byteArrayToHexString(sha_256(hexStringToByteArray(plainText!!))!!)
        }

        @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
        fun hmac_sha_256(key: String?, input: String?): String? {
            val hasher = Mac.getInstance("HmacSHA256")
            hasher.init(SecretKeySpec(hexStringToByteArray(key!!), "HmacSHA256"))
            val hash = hasher.doFinal(hexStringToByteArray(input!!))
            return byteArrayToHexString(hash)
        }

        @Throws(java.lang.Exception::class)
        fun aes_cbc(keyData: ByteArray?, str: ByteArray?): ByteArray? {
            val key: SecretKey
            val cipher: Cipher
            var cipherText: ByteArray? = byteArrayOf(0x00.toByte())
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

        // Encryption
        @Throws(java.lang.Exception::class)
        fun aes_cbc(key: String?, str: String?): String? {
            val keyData = hexStringToByteArray(key!!)
            val encrypted = aes_cbc(keyData, hexStringToByteArray(str!!))
            return byteArrayToHexString(encrypted!!)
        }

        // Decryption
        @Throws(java.lang.Exception::class)
        fun aes_cbc_dec(keyData: ByteArray?, str: ByteArray?): ByteArray? {
            val key: SecretKey
            val cipher: Cipher
            var cipherText: ByteArray? = byteArrayOf(0x00.toByte())
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

        // Encryption
        @Throws(java.lang.Exception::class)
        fun aes_cbc_dec(key: String?, str: String?): String? {
            val keyData = hexStringToByteArray(key!!)
            val encrypted = aes_cbc_dec(keyData, hexStringToByteArray(str!!))
            return byteArrayToHexString(encrypted!!)
        }

        @Throws(java.lang.Exception::class)
        fun HMACSHA256(key: String?, input: String?): String? {
            val hasher = Mac.getInstance("HmacSHA256")
            hasher.init(SecretKeySpec(hexStringToByteArray(key!!), "HmacSHA256"))
            val hash = hasher.doFinal(hexStringToByteArray(input!!))
            return byteArrayToHexString(hash)
        }

        fun toHex(value: Int): String? {
            var n = Integer.toHexString(value).toUpperCase()
            n = if (n.length % 2 == 1) "0$n" else n
            return n
        }

        fun convertTohex(bytes: String): String? {
            val chars = bytes.toCharArray()
            val sb = StringBuffer()
            for (i in chars.indices) {
                sb.append(Integer.toHexString(chars[i].toInt()))
            }
            return sb.toString().toUpperCase()
        }

        fun ascii(str: String): String? {
            val chars = str.toCharArray()
            val sb = StringBuffer()
            for (i in chars.indices) {
                sb.append(Integer.toHexString(chars[i].toInt()))
            }
            return sb.toString().toUpperCase()
        }

        fun padding_00(str: String): String? {
            var str = str
            if (str.length / 2 < 64) {
                while (str.length / 2 != 64) {
                    str = str + "00"
                }
            }
            return str
        }

    }
}