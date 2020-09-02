package com.challenge.fidoreader

import com.challenge.fidoreader.Util.atohex
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import com.fasterxml.jackson.dataformat.cbor.CBORParser
import org.junit.Test
import java.io.ByteArrayInputStream

class test {

    @Test
    fun test(){
        var result = "A70183665532465F5632684649444F5F325F306C4649444F5F325F315F50524502826B6372656450726F746563746B686D61632D7365637265740350F7C558A0F46511E8B5680800200C9A6604A762726BF5627570F5627576F464706C6174F469636C69656E7450696EF57563726564656E7469616C4D676D7450726576696577F5781B75736572566572696669636174696F6E4D676D7450726576696577F4068101070A08"
        val bais = ByteArrayInputStream(result.atohex())
        val cf = CBORFactory()
        val mapper = ObjectMapper(cf)
        var cborParser = cf.createParser(bais)
        var responseMap: Map<String, Any> = mapper.readValue(cborParser, object : TypeReference<Map<String?, Any?>?>() {})
    }
}