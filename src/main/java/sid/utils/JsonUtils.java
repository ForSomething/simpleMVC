package sid.utils;

import com.google.gson.Gson;
import org.apache.commons.beanutils.BeanUtils;

public class JsonUtils {
    public static <T> T parse(String json,Class<T> targetClass){
        if(CommonStringUtils.isEmptyOrWihtespace(json)){
            return null;
        }
        return new Gson().fromJson(json,targetClass);
    }

    public static boolean tryParse(String json,Object targetObj){
        if(CommonStringUtils.isEmptyOrWihtespace(json) || targetObj == null){
            return false;
        }
        try{
            Object resultObj = parse(json,targetObj.getClass());
            BeanUtils.copyProperties(targetObj,resultObj);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public static String toJson(Object value){
        return new Gson().toJson(value);
    }
}
