package crawlerfj.crawler;

import common.eventhandlerinterface.BaseEventHandler;
import crawlerfj.crawlercase.dataentity.CrawlerLog;
import toolroom.httputil.HttpUtils;
import toolroom.httputil.RequestEntity;
import toolroom.httputil.ResponseEntity;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public abstract class ICrawlerfj {
    private static ThreadPoolExecutor threadPoolExecutor;

    private static Map<String, Future> futureMap;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(50,50,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>());
        futureMap = new HashMap<>();
    }

    protected ICrawlerfj(){
        CrawlerProxy.registerCrawler(this);
    }

    abstract public boolean CanHandle(BaseCrawlerConfig _config);

    abstract public void Crawling(BaseCrawlerConfig _config) throws Exception;

    protected void executeThread(Callable callable, BaseCrawlerConfig baseCrawlerConfig){
//        String threadID = UUID.randomUUID().toString().replace("-","");
        CrawlerCallable crawlerCallable = new CrawlerCallable(callable,baseCrawlerConfig);
        threadPoolExecutor.submit(crawlerCallable);
//        futureMap.put(threadID,threadPoolExecutor.submit(crawlerCallable));
    }

    private class CrawlerCallable implements Callable{
        Callable callable;

        BaseCrawlerConfig baseCrawlerConfig;

        public CrawlerCallable(Callable callable, BaseCrawlerConfig baseCrawlerConfig){
            this.callable = callable;
            this.baseCrawlerConfig = baseCrawlerConfig;
        }

        @Override
        public Object call() throws Exception {
            Thread.currentThread().setUncaughtExceptionHandler((thread,throwable) -> {
                try{
                    new CrawlerLog(new Timestamp(System.currentTimeMillis()), CrawlerLog.LogType.ERROR,throwable.toString(),baseCrawlerConfig.toString()).insert();
                    if(baseCrawlerConfig.getExceptionHandler() != null){
                        baseCrawlerConfig.getExceptionHandler().Excute(baseCrawlerConfig);
                    }
                }catch (Exception e){
                    // TODO 这里不懂要怎么处理
                    e.printStackTrace();
                }
            });
            Object result = callable.call();
            new CrawlerLog(new Timestamp(System.currentTimeMillis()), CrawlerLog.LogType.NORMAL,"爬取完成",baseCrawlerConfig.toString()).insert();
            return result;
        }
    }
}
