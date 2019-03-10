package crawlerfj.crawler;

import toolroom.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class BaseCrawlerConfig {
    private Map<String,Object> dataStore = new HashMap<>();

    private CrawlerExceptionHandler exceptionHandler;

    public <T> T getStoringData(String key) {
        return (T)dataStore.get(key);
    }

    public <T> void storeData(String key,T userParam) {
        this.dataStore.put(key,userParam);
    }

    public CrawlerExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(CrawlerExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public String toString(){
        HashMap<String,Object> logMap = new HashMap<>();
        logMap.put("storingData",dataStore);
        return JsonUtils.parse2Json(logMap);
    }

    @FunctionalInterface
    public interface CrawlerExceptionHandler {
        void execute(Exception exception,BaseCrawlerConfig baseCrawlerConfig);
    }
}
