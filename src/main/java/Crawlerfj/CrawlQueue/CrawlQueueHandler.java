package Crawlerfj.CrawlQueue;

import java.util.LinkedList;
import java.util.List;

public class CrawlQueueHandler {
    private static CrawlQueueHandler instance = new CrawlQueueHandler();
    private List<Object> configList;

    private CrawlQueueHandler(){
        configList = new LinkedList<>();
    }

    public static CrawlQueueHandler GetInstance(){
        return instance;
    }

    public synchronized boolean isQueueEmpty(){
        return configList.isEmpty();
    }

    public synchronized Object GetConfigEntityOutOfQueue(){
        if(isQueueEmpty()){
            //如果队列中已经没有了配置对象，则将当前线程挂起
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Object configEntity = configList.get(0);
        configList.remove(configEntity);
        return configEntity;
    }

    public synchronized void PutConfigEntityIntoQueue(Object configEntity){
        configList.add(configEntity);
        //在往队列中添加新的配置对象的时候，通知在获取配置对象时被挂起的线程，让其恢复运行，进行爬取
        notify();
    }
}
