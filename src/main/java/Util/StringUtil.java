package Util;

import java.io.UnsupportedEncodingException;

public class StringUtil {
    static public byte[] GetByteArray(String str) throws UnsupportedEncodingException {
        if(str != null){
            byte[] byteArray = str.getBytes("UTF-8");
            return byteArray;
        }
        return null;
    }
}
