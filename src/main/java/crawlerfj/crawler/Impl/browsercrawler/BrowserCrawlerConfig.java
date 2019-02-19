package crawlerfj.crawler.Impl.browsercrawler;

import common.eventhandlerinterface.BaseEventHandler;
import crawlerfj.crawler.BaseCrawlerConfig;
import toolroom.JsonUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BrowserCrawlerConfig extends BaseCrawlerConfig {
    private BrowserTask browserTask;

    private List<String> browserHistory;

    public BrowserTask getBrowserTask() {
        return browserTask;
    }

    public void setBrowserTask(BrowserTask browserTask) {
        this.browserTask = (browser,config) -> {
            try{
                browserTask.execute(browser,config);
            }finally {
                config.browserHistory = browser.getHistory();
            }
        };
    }

    @Override
    public String toString(){
        Map<String,Object> logMap = JsonUtils.parseJsonString2Map(super.toString());
        logMap.put("browserHistory",browserHistory);
        return JsonUtils.parse2Json(logMap);
    }

    @FunctionalInterface
    public interface BrowserTask{
        void execute(Browser browser,BrowserCrawlerConfig config) throws Exception;
    }

    public static abstract class Browser{
        List<String> history = new LinkedList<>();

        protected abstract void _toPage(String url);

        public void toPage(String url){
            history.add("to page:" + url);
            _toPage(url);
        }

        protected abstract String _getPageContent();

        public String getPageContent(){
            return _getPageContent();
        }

        protected abstract void _executeScript(String script);

        public void executeScript(String script){
            history.add("script:" + script);
            _executeScript(script);
        }

        protected List<String> getHistory(){
            return history;
        }
    }
}
