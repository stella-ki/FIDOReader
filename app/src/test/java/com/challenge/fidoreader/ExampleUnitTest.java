package com.challenge.fidoreader;

import com.challenge.fidoreader.Util.Util;
import com.challenge.fidoreader.fido.Authenticator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        Authenticator authenticator = new Authenticator();


       // String testData = "00A70183665532465F5632684649444F5F325F306C4649444F5F325F315F50524502826B6372656450726F746563746B686D61632D7365637265740350F7C558A0F46511E8B5680800200C9A6604A762726BF5627570F5627576F464706C6174F469636C69656E7450696EF47563726564656E7469616C4D676D7450726576696577F5781B75736572566572696669636174696F6E4D676D7450726576696577F4068101070A0818209000";
        String testData = "A70183665532465F5632684649444F5F325F306C4649444F5F325F315F50524502826B6372656450726F746563746B686D61632D7365637265740350F7C558A0F46511E8B5680800200C9A6604A762726BF5627570F5627576F464706C6174F469636C69656E7450696EF47563726564656E7469616C4D676D7450726576696577F5781B75736572566572696669636174696F6E4D676D7450726576696577F4068101070A081820";

        try {
            JsonNode t  = authenticator.getInfo_parse(testData);
            System.out.println(t.get("1"));
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}