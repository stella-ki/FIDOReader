package com.challenge.fidoreader;

import com.challenge.fidoreader.Util.Util;

import org.junit.Test;

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
        String testData = "00A101A50102032620012158204B8353E9D16F38DA7639F91B49458B46FE99C11FB64C7B95754DDA6BD63498402258204B366A774AB8E9F4CD2F1FEF8CB590FE24AA7BE2C065487344F6B448736E6C939000";

        /*try {
            JsonNode t  = authenticator.getInfo_parse(testData);
            System.out.println(t.asText());
            System.out.println(t.get("1"));
            System.out.println(Util.getHexString(t.get("1").get("-2").binaryValue()));
            System.out.println(Util.getHexString(t.get("1").get("-3").binaryValue()));
        }catch (Exception e){
            e.printStackTrace();
        }
*/


        try {
            //authenticator.ClientPINparse(Authenticator.cp_sub_getKeyAgreement, testData);

            //String input = "blueberryplum.niblueberryplum.ni";
            //String key = Util.convertTohex(input);

            String test2 = "4E74B80EE771037ADD9FFFB3B984138FA8E0B04955F19F9933A14CF8F476C6EC ";
            String test1 = "TnS4DudxA3rdn/+zuYQTj6jgsElV8Z+ZM6FM+PR2xuw=";

            System.out.println(Util.byteArrayToHexString(test1.getBytes()));
            System.out.println(Util.convertTohex(test2));


            //System.out.println(key);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}