package crawlerfj.crawler.Impl.SingleStepCrawler;

import crawlerfj.crawler.BaseCrawlerConfig;
import crawlerfj.crawler.ICrawlerfj;
import toolroom.httputil.HttpUtils;
import toolroom.httputil.RequestEntity;
import toolroom.httputil.ResponseEntity;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SingleStepCrawler extends ICrawlerfj {
    private static SingleStepCrawler instance = new SingleStepCrawler();

    private SingleStepCrawler(){

    }

    public static SingleStepCrawler GetInstance(){
        return instance;
    }

    @Override
    public boolean CanHandle(BaseCrawlerConfig _config) {
        return _config != null && SingleStepConfig.class.getCanonicalName().equals(_config.getClass().getCanonicalName());
    }

    @Override
    public void Crawling(BaseCrawlerConfig _config) {
        SingleStepConfig config = (SingleStepConfig)_config;
        super.executeThread(()->{
            if(config.getBeforeCrawlingHandler() != null){
                config.getBeforeCrawlingHandler().Excute(config);
            }
            config.getSingleStep().Execute(doRequest(config.getRequestEntity()),config);
            if(config.getAfterCrawlingHandler() != null){
                config.getAfterCrawlingHandler().Excute(config);
            }
            return null;
        },config);
    }

    private ResponseEntity doRequest(RequestEntity requestEntity) throws Exception {
        switch (requestEntity.getRequestMethod()){
            case GET: return HttpUtils.doGet(requestEntity);
            case POST: return HttpUtils.doPost(requestEntity);
            default: return null;
        }
    }
}
