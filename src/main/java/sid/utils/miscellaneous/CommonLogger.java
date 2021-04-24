package sid.utils.miscellaneous;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sid.utils.executor.JavaScriptExecutor;

public class CommonLogger {
    private static final Logger logger = LogManager.getLogger(JavaScriptExecutor.class);

    public static void info(Object obj){
        LogManager.getLogger(Thread.currentThread().getStackTrace()[2].getClass()).info(obj);
    }

    public static void info(Object obj,Throwable e){
        LogManager.getLogger(Thread.currentThread().getStackTrace()[2].getClass()).info(obj,e);
    }
}
