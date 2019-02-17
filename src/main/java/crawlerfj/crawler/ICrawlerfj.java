package crawlerfj.crawler;

import toolroom.httputil.HttpUtils;
import toolroom.httputil.RequestEntity;
import toolroom.httputil.ResponseEntity;

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

    abstract public boolean CanHandle(Object _config);

    abstract public void Crawling(Object _config) throws Exception;

    protected void executeThread(Callable callable){
        String threadID = UUID.randomUUID().toString().replace("-","");
        futureMap.put(threadID,threadPoolExecutor.submit(new CrawlerCallable(callable,null)));
    }

    private class CrawlerCallable implements Callable{
        Callable callable;

        Thread.UncaughtExceptionHandler exceptionHandler;

        public CrawlerCallable(Callable callable, Thread.UncaughtExceptionHandler exceptionHandler){
            this.callable = callable;
            this.exceptionHandler = exceptionHandler;
        }

        @Override
        public Object call() throws Exception {
            Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler);
            return callable.call();
        }
    }
}
