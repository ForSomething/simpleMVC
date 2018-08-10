package Util;

import java.io.UnsupportedEncodingException;

public class StringUtil {
    static public String emptyString = "";

    static public byte[] GetByteArray(String str,String charsetName) throws UnsupportedEncodingException {
        if(str != null){
            byte[] byteArray = str.getBytes(charsetName);
            return byteArray;
        }
        return null;
    }

    static public boolean IsNullOrEmpty(String str){
        if(str == null || str.equals("")){
            return true;
        }
        return false;
    }

    static public boolean IsNullOrWihtespace(String str){
        if(str == null || str.trim().equals("")){
            return true;
        }
        return false;
    }
}
