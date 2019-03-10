package crawlerfj.crawler;

import common.constvaslue.Events;
import common.eventhandlerinterface.BaseEventHandler;
import crawlerfj.crawlercase.dataentity.CrawlerLog;
import eventManegement.EventManeger;
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

        // TODO 这个地方日志和事件还有用户自动一异常处理器的执行，他们之间的顺序要考虑一下
        @Override
        public Object call() throws Exception {
            Object result = null;
            try{
                result = callable.call();
                new CrawlerLog(new Timestamp(System.currentTimeMillis()), CrawlerLog.LogType.NORMAL,"爬取完成",baseCrawlerConfig.toString()).insert();
                EventManeger.on(Events.ON_THREAD_COMPLETA);
            }catch (Exception e){
                e.printStackTrace();
                new CrawlerLog(new Timestamp(System.currentTimeMillis()), CrawlerLog.LogType.ERROR,e.toString(),baseCrawlerConfig.toString()).insert();
                EventManeger.on(Events.ON_THREAD_ERROR);
                if(baseCrawlerConfig.getExceptionHandler() != null){
                    baseCrawlerConfig.getExceptionHandler().execute(e,baseCrawlerConfig);
                }
            }
            return result;
        }
    }
}
