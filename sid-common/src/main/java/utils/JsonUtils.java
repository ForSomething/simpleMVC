package utils;

import com.google.gson.Gson;

import java.util.Map;

public class JsonUtils {
    public static Map<String,Object> parseJsonString2Map(String srcString){
        if(StringUtils.isNullOrWihtespace(srcString)){
            return null;
        }
        return new Gson().fromJson(srcString,Map.class);
    }

    public static boolean tryParseJsonString2Map(String srcString,Map<String,Object> disMap){
        if(StringUtils.isNullOrWihtespace(srcString) || disMap == null){
            return false;
        }
        try{
            Map<String,Object> tempMap = new Gson().fromJson(srcString,Map.class);
            for(String key : tempMap.keySet()){
                disMap.put(key,tempMap.get(key));
            }
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public static String parse2Json(Object value){
        return new Gson().toJson(value);
    }
}
