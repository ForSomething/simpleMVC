package Crawlerfj.Crawler.Impl.SingleStepCrawler;

import Crawlerfj.Crawler.ICrawlerfj;
import Util.HttpUtil.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SingleStepCrawler extends ICrawlerfj {
    ICrawlerfj.RequestFinishCallBack callBack;

    private ThreadPoolExecutor threadPoolExecutor;

    private Map<String,SingleStepConfig> SingleStepConfigPool;

    private Lock singleStepPoolLock;

    private static SingleStepCrawler instance = new SingleStepCrawler();

    private SingleStepCrawler(){
        threadPoolExecutor = new ThreadPoolExecutor(5,5,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>());
        SingleStepConfigPool = new HashMap<>();
        singleStepPoolLock = new ReentrantLock();

        callBack = (serialNumber, responseEntity) -> {
            singleStepPoolLock.lock();
            SingleStepConfig stepConfig = SingleStepConfigPool.get(serialNumber);
            singleStepPoolLock.unlock();
            if(stepConfig != null){
                threadPoolExecutor.execute(new StepExecuteRunnable(responseEntity,stepConfig));
            }
        };
    }

    public static SingleStepCrawler GetInstance(){
        return instance;
    }

    @Override
    public boolean CanHandle(Object _config) {
        return _config != null && SingleStepConfig.class.getCanonicalName().equals(_config.getClass().getCanonicalName());
    }

    @Override
    public void Crawling(Object _config) {
        SingleStepConfig config = (SingleStepConfig)_config;
        String serialNumber = super.DoRequestAsync(config.getRequestEntity(),callBack);
        singleStepPoolLock.lock();
        if(SingleStepConfigPool.containsKey(serialNumber)){
            SingleStepConfigPool.replace(serialNumber,config);
        }else {
            SingleStepConfigPool.put(serialNumber,config);
        }
        singleStepPoolLock.unlock();
    }

    private class StepExecuteRunnable implements Runnable{
        ResponseEntity responseEntity;

        SingleStepConfig singleStepConfig;

        public StepExecuteRunnable(ResponseEntity responseEntity,SingleStepConfig singleStepConfig){
            this.responseEntity = responseEntity;
            this.singleStepConfig = singleStepConfig;
        }

        @Override
        public void run() {
            singleStepConfig.getSingleStep().Execute(responseEntity,singleStepConfig);
        }
    }
}
