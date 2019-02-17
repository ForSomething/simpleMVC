package crawlerfj.crawler.Impl.browsercrawler;

import common.eventhandlerinterface.BaseEventHandler;

import java.util.HashMap;
import java.util.Map;

public class BrowserCrawlerConfig {
    private BrowserTask browserTask;

    private Map<String,Object> userParam;

    private BaseEventHandler exceptionHandler;

    public BrowserCrawlerConfig(){
        userParam = new HashMap<>();
    }

    public <T> T getUserParam(String key) {
        return (T)userParam.get(key);
    }

    public <T> void setUserParam(String key,T userParam) {
        if(this.userParam.containsKey(key)){
            this.userParam.replace(key,userParam);
        }else{
            this.userParam.put(key,userParam);
        }
    }

    public BrowserTask getBrowserTask() {
        return browserTask;
    }

    public void setBrowserTask(BrowserTask browserTask) {
        this.browserTask = browserTask;
    }

    public BaseEventHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(BaseEventHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @FunctionalInterface
    public interface BrowserTask{
        void execute(Browser browser,BrowserCrawlerConfig config) throws Exception;
    }

    public interface Browser{
        void toPage(String url);

        String getPageContent();

        void executeScript(String script);
    }
}
