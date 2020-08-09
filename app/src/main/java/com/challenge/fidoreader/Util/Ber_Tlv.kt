package com.challenge.fidoreader.Util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class Ber_Tlv {

    private var TagHex: ByteArray ?= null
    private var rawEncodedLengthBytes: ByteArray ?= null
    private var valueBytes: ByteArray ?= null

    constructor(TagHex: ByteArray, rawEncodedLengthBytes: ByteArray, valueBytes: ByteArray?){
        if(valueBytes != null){
            this.valueBytes = valueBytes
        }
        this.rawEncodedLengthBytes = rawEncodedLengthBytes
        this.TagHex = TagHex
    }

    companion object{
        private fun isBitSet(data: Byte, offset: Int): Boolean{
            if( (data.toInt().shr(offset - 1) and 0x01)  == 1){
                return true
            }
            return false
        }

        private fun iatoi(data: ByteArray, length: Int): Int{
            var atoi: Int = 0

            for(ii in 0 until length){
                atoi += data[ii].toInt() and 0xFF shl 8 * (data.size - ii - 1)
            }
            return atoi
        }

        private fun get_TLV_Tag(stream: ByteArrayInputStream): ByteArray{
            var tagBAOS = ByteArrayOutputStream()

            val tagFirstOctet = stream.read()
            val MASK: Byte = 0x1F

            tagBAOS.write(tagFirstOctet.toInt())

            if( tagFirstOctet and MASK.toInt() == MASK.toInt() ){
                do{
                    var nextOctet: Int = stream.read()

                    if(nextOctet < 0){
                        break
                    }

                    var tlvIdNextOctet = nextOctet.toByte()
                    tagBAOS.write(tlvIdNextOctet.toInt())

                    if(!isBitSet(tlvIdNextOctet, 8)){
                        break
                    }
                }while (true);
            }

            return tagBAOS.toByteArray()
        }

        private fun get_TLV_Length(stream: ByteArrayInputStream): Int{
            var length = 0
            var length_aux = stream.read()

            if(length_aux <= 128){
                length = length_aux
            }
            else{
                var numberOfLengthOctets = length_aux and 127
                length_aux = 0

                for(i in 0 until numberOfLengthOctets){
                    var nextLengthOctet = stream.read()
                    length_aux = length_aux shl 8
                    length_aux = length_aux or nextLengthOctet
                }
                length = length_aux
            }
            return length
        }

        private fun getNextTLV(stream: ByteArrayInputStream, bDOL: Boolean): Ber_Tlv{
            stream.mark(0)

            var peekInt = stream.read()
            var peekByte = peekInt.toByte()

            while( (peekInt != -1) &&
                   ( (peekByte == 0xFF.toByte()) || (peekByte == 0x00.toByte()) ) ){
                stream.mark(0)
                peekInt = stream.read()
                peekByte = peekInt.toByte()
            }

            stream.reset()

            var tagIdBytes: ByteArray = get_TLV_Tag(stream)

            stream.mark(0)

            var posBefore: Int = stream.available()

            var length: Int = get_TLV_Length(stream)
            val posAfter = stream.available()
            stream.reset()
            val lengthBytes = ByteArray(posBefore - posAfter)
            stream.read(lengthBytes, 0, lengthBytes.size)

            val rawLength = iatoi(lengthBytes, lengthBytes.size)
            var valueBytes: ByteArray ?= null

            if(bDOL){
                var tlv: Ber_Tlv = Ber_Tlv(tagIdBytes, lengthBytes, null)
                return tlv
            }

            if(rawLength == 128){
                stream.mark(0)
                var prevOctet: Int = 1
                var curOctet: Int = 0
                var len: Int = 0

                while(true){
                    len++
                    curOctet = stream.read()

                    if( (prevOctet == 0) && (curOctet == 0) ){
                        break
                    }
                    prevOctet = curOctet
                }
                len -= 2
                valueBytes = ByteArray(len)
                stream.reset()
                stream.read(valueBytes, 0, len)
                length = len
            }
            else{
                valueBytes = ByteArray(length)
                stream.read(valueBytes, 0, length)
            }
            stream.mark(0)
            peekInt = stream.read()
            peekByte = peekInt.toByte()

            while( peekInt != -1 && (peekByte == 0xFF.toByte() || peekByte == 0x00.toByte())){
                stream.mark(0)
                peekInt = stream.read()
                peekByte = peekInt.toByte()
            }
            stream.reset()

            var tlv: Ber_Tlv = Ber_Tlv(tagIdBytes, lengthBytes, valueBytes)
            return tlv
        }
    }

    public fun getTagBytes(): ByteArray?{
        return TagHex
    }

    public fun getRawEncodedLengthBytes(): ByteArray?{
        return rawEncodedLengthBytes
    }

    public fun getValueBytes(): ByteArray?{
        return valueBytes
    }


}