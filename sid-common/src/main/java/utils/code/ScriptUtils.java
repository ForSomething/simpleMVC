package utils.code;

import com.googlecode.aviator.AviatorEvaluator;
import utils.StringUtils;

import java.util.Map;

public class ScriptUtils extends testc {
    public ScriptUtils() throws Exception {
    }

    public static Object exec(String script, Object... params){
        return StringUtils.isNullOrWihtespace(script) ? null : AviatorEvaluator.exec(script,params);
    }

    public static Object execute(String script, Map<String,Object> env){
        return StringUtils.isNullOrWihtespace(script) ? null : AviatorEvaluator.execute(script,env);
    }
}