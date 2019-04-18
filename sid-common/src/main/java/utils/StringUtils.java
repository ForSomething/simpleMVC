package utils;

import org.apache.commons.beanutils.BeanUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class StringUtils {
    static public String emptyString = "";

    static public byte[] GetByteArray(String str,String charsetName) throws UnsupportedEncodingException {
        if(str != null){
            byte[] byteArray = str.getBytes(charsetName);
            return byteArray;
        }
        return null;
    }

    static public boolean isNullOrEmpty(String str){
        return str == null || str.equals("");
    }

    static public boolean isNullOrWihtespace(String str){
        return str == null || str.trim().equals("");
    }

    static public <T> T getValueByTemplate(String srcString,String templateString,Class disObjectClass) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T disObject = (T)disObjectClass.newInstance();
        HashMap<String,Object> valueMap = new HashMap<>();
        
        BeanUtils.populate(disObject,valueMap);
        return disObject;
    }
}
