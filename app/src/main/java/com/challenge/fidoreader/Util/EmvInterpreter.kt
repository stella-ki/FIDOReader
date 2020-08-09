package com.challenge.fidoreader

import com.challenge.fidoreader.Util.Ber_Tlv
import com.challenge.fidoreader.Util.Util
import java.io.ByteArrayInputStream

// 필요 없어보임
object EmvInterpreter {

    private enum class EMVDescription(private var szEmvTagStr: String, private var szDescriptionStr: String) {

        TAG001("0042", "Issuer Identification Number (IIN)"),
        TAG002("004F", "Application Identifier (AID)"),
        TAG003("0050", "Application Label"),
        TAG004("0051", "ISO-7816 Path"),
        TAG005("0057", "Track 2 Equivalent Data"),
        TAG006("005A", "Application PAN"),
        TAG007("0061", "Application Template"),
        TAG008("006F", "FCI Template"),
        TAG009("0070", "Record Template (EMV Proprietary"),
        TAG010("0071", "Issuer Script Template 1"),
        TAG011("0072", "Issuer Script Template 2"),
        TAG012("0073", "Directory Discretionary Template"),
        TAG013("0077", "Response Message Template Format 2"),
        TAG014("0080", "Response Message Template Format 1"),
        TAG015("0081", "Amount, Authorised (Binary)"),
        TAG016("0082", "Application Interchange Profile"),
        TAG017("0083", "Command Template"),
        TAG018("0084", "Dedicated File (DF) Name"),
        TAG019("0086", "Issuer Script Command"),
        TAG020("0087", "Application Priority Indicator"),
        TAG021("0088", "Short File Identifier (SFI)"),
        TAG022("0089", "Authorisation Code"),
        TAG023("008A", "Authorisation Response Cdoe"),
        TAG024("008C", "CDOL1"),
        TAG025("008D", "CDOL2"),
        TAG026("008E", "CVM List"),
        TAG027("008F", "CA Public Key Index"),
        TAG028("0090", "Issuer Public Key Certificate"),
        TAG029("0091", "Issuer Authentication Data"),
        TAG030("0092", "Issuer Public Key Remainder"),
        TAG031("0093", "Signed Static Application Data"),
        TAG032("0094", "Application File Locator (AFL)"),
        TAG033("0095", "Terminal Verification Results (TVR)"),
        TAG034("0097", "TDOL"),
        TAG035("0098", "TC Hash Value"),
        TAG036("0099", "Transaction PIN Data"),
        TAG037("009A", "Transaction Date"),
        TAG038("009B", "Transaction Status Information"),
        TAG039("009C", "Transaction Type"),
        TAG040("009D", "DDF Name"),
        TAG041("00A5", "FCI proprietary Template"),
        TAG101("5F20", "Cardholder Name"),
        TAG102("5F24", "Application Expiration Date"),
        TAG103("5F35", "Application Effective Date"),
        TAG104("5F28", "Issuer Country Code"),
        TAG105("5F2A", "Transaction Currency Code"),
        TAG106("5F2D", "Language Preference"),
        TAG107("5F30", "Service Code"),
        TAG0108("5F34","PAN Sequence Number"),
        TAG0109("5F36","Transaction Currency Exponent"),
        TAG0110("5F50","Issuer URL"),
        TAG0111("5F53","IBAN"),
        TAG0112("5F54","Bank Identifier Code (BIC)"),
        TAG0113("5F55","Issuer Country Code (alpha2 format)"),
        TAG0114("5F56","Issuer Country Code (alpha3 format)"),
        TAG0115("9F01","Acquirer Identifier"),
        TAG0116("9F02","Amount, Authorised (Numeric)"),
        TAG0117("9F03","Amount, Other (Numeric)"),
        TAG0118("9F04","Amount, Other (Binary)"),
        TAG0119("9F05","Application Discretionary Data"),
        TAG0120("9F06","Application Identifier (AID)"),
        TAG0121("9F07","Application Usage Control"),
        TAG0122("9F08","Application Version Number"),
        TAG0123("9F09","Application Version Number"),
        TAG0124("9F0B","Cardholder Name Extended"),
        TAG0125("9F0D","Issuer Action Code - Default"),
        TAG0126("9F0E","Issuer Action Code - Denial"),
        TAG0127("9F0F","Issuer Action Code - Online"),
        TAG0128("9F10","Issuer Application Data"),
        TAG0129("9F11","Issuer Code Table Index"),
        TAG0130("9F12","Application Preferred Name"),
        TAG0131("9F13","Last Online ATC Register"),
        TAG0132("9F14","Lower Consecutive Offline Limit"),
        TAG0133("9F15","Merchant Category Code"),
        TAG0134("9F16","Merchant Identifier"),
        TAG0135("9F17","PIN Try Counter"),
        TAG0136("9F18","Issuer Script Identifier"),
        TAG0137("9F1A","Terminal Country Code"),
        TAG0138("9F1B","Terminal Floor Limit"),
        TAG0139("9F1C","Terminal Identification"),
        TAG0140("9F1D","Terminal Risk Management Data"),
        TAG0141("9F1E","Interface Device (IFD) Serial Number"),
        TAG0142("9F1F","Track 1 Discretionary Data"),
        TAG0143("9F20","Track 2 Discretionary Data"),
        TAG0144("9F21","Transaction Time (HHMMSS)"),
        TAG0145("9F22","Certification Authority Public Key Index"),
        TAG0146("9F23","Upper Consecutive Offline Limit"),
        TAG0147("9F26","Application Cryptogram"),
        TAG0148("9F27","Cryptogram Information Data"),
        TAG0149("9F2D","ICC PIN Encipherment Pub Key Certificate"),
        TAG0150("9F2E","ICC PIN Encipherment Pub Key Exponent"),
        TAG0151("9F2F","ICC PIN Encipherment Pub Key Remainder"),
        TAG0152("9F32","Issuer Public Key Exponent"),
        TAG0153("9F33","Terminal Capabilities"),
        TAG0154("9F34","Cardholder Verification (CVM) Results"),
        TAG0155("9F35","Terminal Type"),
        TAG0156("9F36","Application Transaction Counter (ATC)"),
        TAG0157("9F37","Unpredictable Number"),
        TAG0158("9F38","Processing Options (PDOL)"),
        TAG0159("9F39","Point-of-Service (POS) Entry Mode"),
        TAG0160("9F3A","Amount, Reference Currency"),
        TAG0161("9F3B","Application Reference Currency"),
        TAG0162("9F3C","Transaction Reference Currency Code"),
        TAG0163("9F3D","Transaction Reference Currency Exponent"),
        TAG0164("9F40","Additional Terminal Capabilities"),
        TAG0165("9F41","Transaction Sequence Counter"),
        TAG0166("9F42","Application Currency Code"),
        TAG0167("9F43","Application Reference Currency Exponent"),
        TAG0168("9F44","Application Currency Exponent"),
        TAG0169("9F45","Data Authentication Code"),
        TAG0170("9F46","ICC Public Key Certificate"),
        TAG0171("9F47","ICC Public Key Exponent"),
        TAG0172("9F48","ICC Public Key Remainder"),
        TAG0173("9F49","Dynamic Data Authentication (DDOL)"),
        TAG0174("9F4A","Static Data Authentication Tag List"),
        TAG0175("9F4B","Signed Dynamic Application Data"),
        TAG0176("9F4C","ICC Dynamic Number"),
        TAG0177("9F4D","Log Entry"),
        TAG0178("9F4E","Merchant Name and Location"),
        TAG0179("9F4F","Log Format"),
        TAG0180("9F66","Terminal Transaction Qualifiers"),
        TAG0181("BF0C","FCI Issuer Discretionary Data");

        private fun getEMVCodeAsString(): String{
            return szEmvTagStr
        }

        private fun getDescription(): String{
            return szDescriptionStr
        }

        companion object{
            fun getEMVDescription(emv1: Byte, emv2: Byte): String{
                return getEMVDescription(Util.szByteHex2String(emv1) +
                        Util.szByteHex2String(emv2))

            }

            private fun getEMVDescription(EmvStr: String): String{
                val EmvDescs = EMVDescription.values()

                var ii = 0
                for(ii in EmvDescs.indices){
                    val EmvDesc = EmvDescs[ii]

                    if(EmvDesc.getEMVCodeAsString().equals(EmvStr, true)){
                        return EmvDesc.getDescription()
                    }
                }

                return "(UNRECOGNIZED)";
            }
        }

    }

    private fun getTagDescription(tagBytes: ByteArray): String{
        if(tagBytes.size > 1){
            return (EMVDescription.getEMVDescription(tagBytes[0], tagBytes[1]))
        }
        else{
            return (EMVDescription.getEMVDescription(0x00.toByte(), tagBytes[0]))
        }
    }

    private fun PrintValue(byteArray: ByteArray): String{
        val length = byteArray.size
        var buf = StringBuilder()
        // var ii = 0

        for(ii in 0 until length){
            if( (byteArray[ii] >= 0x20.toByte()) && (byteArray[ii] < 0x7F.toByte()) ){
                buf.append(byteArray[ii].toChar())
            }
            else{
                var buf_no = StringBuilder()
                return buf_no.toString()
            }
        }
        return buf.toString()
    }

    private fun getTagValueAsString(value: ByteArray): String{
        var buf = StringBuilder()
        buf.append("\n")
        buf.append("    >Inter  ")

        var ValueStr = PrintValue(value)
        buf.append(ValueStr)

        if(ValueStr.length > 1){
            return buf.toString()
        }

        return ""
    }

    private fun AddSpace(s: String): String{
        var buf = StringBuilder()

        for(ii in 0 until s.length){
            var c = s[ii];

            buf.append(c)

            var nextPos = ii + 1
            if( (nextPos % 2 == 0) && (nextPos != s.length) ){
                buf.append(" ")
            }
        }
        return buf.toString()
    }

    private fun byteArrayToHexString(byteArray: ByteArray): String{
        val length = byteArray.size

        var hexData = StringBuilder()
        var mybyte: Int

        for(ii in 0 until length){
            mybyte = (0x000000ff and byteArray[ii].toInt()) or 0xffffff00.toInt()
            hexData.append(Integer.toHexString(mybyte).substring(6))
        }

        return hexData.toString().toUpperCase()
    }

    private fun ShowDOLInterpretation(data: ByteArray): String{
        var buf = StringBuilder()
        val stream = ByteArrayInputStream(data)

        var firstLine: Boolean = true

        while(stream.available() > 0){
            if(firstLine){
                firstLine = false
                buf.append("DOL Interpretation:\n")
            }
            else{
                buf.append("\n")
            }

            var tlv: Ber_Tlv = Ber_Tlv.getNextTLV(stream, true)

            var tagBytes: ByteArray = tlv.tagBytes
            var lengthBytes: ByteArray = tlv.rawEncodedLengthBytes

            buf.append("         *Tag ")
            buf.append(AddSpace(byteArrayToHexString(tagBytes)))
            buf.append("\n")
            buf.append("         *Len ")
            buf.append(AddSpace(byteArrayToHexString(lengthBytes)))
            buf.append("\n")
            buf.append("         * ")
            buf.append(getTagDescription(tagBytes))
        }
        return buf.toString()
    }

    private fun isDOL(tagBytes: ByteArray): Boolean{
        if(tagBytes[0] == 0x9F.toByte()){
            if( (tagBytes[1] == 0x38.toByte()) ||
                (tagBytes[1] == 0x49.toByte()) ||
                (tagBytes[1] == 0x4F.toByte()) ){
                return true
            }
            if( (tagBytes[0] == 0x8C.toByte()) ||
                (tagBytes[0] == 0x8D.toByte()) ){
                return true
            }
        }
        return false
    }

    private fun ShowEMV_Interpretation(data: ByteArray): String{
        var buf = StringBuilder()
        val stream = ByteArrayInputStream(data)

        while(stream.available() > 0){
            buf.append("\n")

            val tlv: Ber_Tlv = Ber_Tlv.getNextTLV(stream, false)

            val tagBytes: ByteArray = tlv.tagBytes
            val lengthBytes: ByteArray = tlv.rawEncodedLengthBytes
            val valueBytes: ByteArray = tlv.valueBytes

            buf.append("Tag ")
            buf.append(AddSpace(byteArrayToHexString(tagBytes)))
            buf.append(" ")
            buf.append(getTagDescription(tagBytes))
            buf.append("\n")
            buf.append("    >Len ")
            buf.append(AddSpace(byteArrayToHexString(lengthBytes)))

            if(tagBytes[0].toInt().shr(5) and 0x01 == 1){
                buf.append(" ( Constructed Data Object )")
                buf.append(ShowEMV_Interpretation(valueBytes))
            }
            else{
                buf.append(("\n"))

                if(isDOL(tagBytes)){
                    buf.append("    >Value")
                    buf.append("  ")
                    val ValueStr: String = AddSpace(byteArrayToHexString(valueBytes))

                    if(ValueStr.length > 34){
                        buf.append(":")
                        buf.append("\n")
                    }
                    buf.append(ValueStr)
                    buf.append("\n")
                    buf.append(ShowDOLInterpretation(valueBytes))
                }
                else{
                    buf.append("    >Value")
                    buf.append("  ")

                    val ValueStr: String = AddSpace(byteArrayToHexString(valueBytes))

                    if(ValueStr.length > 34){
                        buf.append(":")
                        buf.append("\n")
                    }
                    buf.append(ValueStr)
                    buf.append(ShowDOLInterpretation(valueBytes))
                }
            }
        }

        return buf.toString()
    }


}