package sid.utils.executor;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.LogManager;
import sid.exception.RTCDNotSuccessException;
import sid.utils.JsonUtils;
import sid.utils.miscellaneous.CommonLogger;

import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.util.Map;

public class JavaScriptExecutor {
    private Map<String,Object> context;
    private String script;

    public JavaScriptExecutor(Map<String,Object> context, String script){
        this.context = context;
        this.script= script;
    }

    public <T> T execute(Class<T> resultType) throws Exception {
        Object result;
        try{
            CommonLogger.info("JavaScriptExecutor开始执行，context为");
            CommonLogger.info(JsonUtils.toJson(context));
            result = new ScriptEngineManager().getEngineByName("JavaScript").eval(this.script,new SimpleBindings(){{
                put("context",context);
            }});
            CommonLogger.info("JavaScriptExecutor执行完毕");
            if(result != null){
                CommonLogger.info(JsonUtils.toJson(result));
            }
        }catch (Exception e){
            //先检查是RTCDNotSuccessException导致的，是则抛出
            Throwable temp = e;
            while (temp != null){
                if(temp instanceof RTCDNotSuccessException){
                    throw (RTCDNotSuccessException)temp;
                }
                temp = temp.getCause();
            }
            throw e;
        }
        if(resultType == null){
            return null;
        }
        if(Map.class.isAssignableFrom(resultType) && !(result instanceof Map)){
            return (T) BeanUtils.describe(result);
        }
        return (T)result;
    }
}
