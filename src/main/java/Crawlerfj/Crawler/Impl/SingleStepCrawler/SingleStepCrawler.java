package Crawlerfj.Crawler.Impl.SingleStepCrawler;

import Crawlerfj.Crawler.ICrawlerfj;
import Util.HttpUtil.RequestEntity;
import Util.HttpUtil.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SingleStepCrawler extends ICrawlerfj {
    private ThreadPoolExecutor threadPoolExecutor;

    private static SingleStepCrawler instance = new SingleStepCrawler();

    private SingleStepCrawler(){
        threadPoolExecutor = new ThreadPoolExecutor(5,5,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>());
    }

    public static SingleStepCrawler GetInstance(){
        return instance;
    }

    @Override
    public boolean CanHandle(Object _config) {
        return _config != null && SingleStepConfig.class.getCanonicalName().equals(_config.getClass().getCanonicalName());
    }

    @Override
    public void Crawling(Object _config) throws Exception {
        SingleStepConfig config = (SingleStepConfig)_config;
        threadPoolExecutor.execute(new StepExecuteRunnable(config,this));
    }

    private ResponseEntity DoRequestBySupper(RequestEntity requestEntity) throws IOException {
        return super.DoRequest(requestEntity);
    }

    private class StepExecuteRunnable implements Runnable{
        SingleStepConfig singleStepConfig;

        SingleStepCrawler crawler;

        public StepExecuteRunnable(SingleStepConfig singleStepConfig,SingleStepCrawler crawler){
            this.crawler = crawler;
            this.singleStepConfig = singleStepConfig;
        }

        @Override
        public void run() {
            try {
                singleStepConfig.getSingleStep().Execute(crawler.DoRequestBySupper(singleStepConfig.getRequestEntity()),singleStepConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
