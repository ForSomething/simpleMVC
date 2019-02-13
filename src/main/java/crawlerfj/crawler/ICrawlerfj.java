package crawlerfj.crawler;

import toolroom.httputil.HttpUtils;
import toolroom.httputil.RequestEntity;
import toolroom.httputil.ResponseEntity;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ICrawlerfj {
    private static ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(50,50,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>());
    }

    abstract public boolean CanHandle(Object _config);

    abstract public void Crawling(Object _config) throws Exception;

    protected void executeThread(Runnable runnable){
        threadPoolExecutor.execute(runnable);
    }
}
