package crawlerfj.crawler;

import common.eventhandlerinterface.BaseEventHandler;
import toolroom.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class BaseCrawlerConfig {
    private Map<String,Object> dataStore = new HashMap<>();

    private BaseEventHandler exceptionHandler;

    public <T> T getStoringData(String key) {
        return (T)dataStore.get(key);
    }

    public <T> void storeData(String key,T userParam) {
        this.dataStore.put(key,userParam);
    }

    public BaseEventHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(BaseEventHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public String toString(){
        HashMap<String,Object> logMap = new HashMap<>();
        logMap.put("storingData",dataStore);
        return JsonUtils.parse2Json(logMap);
    }
}
