package utils;

import java.io.UnsupportedEncodingException;

public class StringUtils {
    static public String emptyString = "";

    static public byte[] GetByteArray(String str,String charsetName) throws UnsupportedEncodingException {
        if(str != null){
            byte[] byteArray = str.getBytes(charsetName);
            return byteArray;
        }
        return null;
    }

    static public boolean IsNullOrEmpty(String str){
        return str == null || str.equals("");
    }

    static public boolean IsNullOrWihtespace(String str){
        return str == null || str.trim().equals("");
    }
}
