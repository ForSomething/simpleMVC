package sid.service.fantds;

import org.apache.commons.io.FileUtils;
import sid.utils.executor.JavaScriptExecutor;

import java.io.File;
import java.util.Map;

public class DirectBankIntfCaller {
    public Map<String,Object> processCall(String processFilePath,Map<String,Object> context) throws Exception{
        String javascript = FileUtils.readFileToString(new File(processFilePath),"utf-8");
        JavaScriptExecutor executor = new JavaScriptExecutor(context,javascript);
        return executor.execute(Map.class);
    }
}
