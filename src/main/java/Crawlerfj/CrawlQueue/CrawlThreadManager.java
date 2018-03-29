package Crawlerfj.CrawlQueue;

import Crawlerfj.Proxy.DefaultCrawlerProxy;

public class CrawlThreadManager {
    //标志位，指示StartAllThread方法是否被调用了
    private boolean hasStartAllThread = false;
    //默认的线程数是5条
    private int threadCount = 5;
    //线程池
    private CrawlThread[] threadPool;
    private static CrawlThreadManager instance = new CrawlThreadManager();

    private CrawlThreadManager(){}

    public static CrawlThreadManager GetInstance(){
        return instance;
    }

    //设置线程数
    public void SetThreadCount(int count){
        if(count > 0){
            CrawlThread[] tempPool;
            if(threadCount > count){
                //如果新设置的线程数比原有的线程数小
                tempPool = new CrawlThread[count];
                for(int index = 0; index < count; index++){
                    if(index < count){
                        //将0至count-1的线程赋到临时数组中
                        tempPool[index] = threadPool[index];
                    }else{
                        //count至最后一条线程将停止工作
                        threadPool[index].DisableThread();
                    }
                }
                //将临时数组赋给threadPool
                threadPool = tempPool;
            }else if(threadCount < count){
                //如果新设置的线程数比原有的线程数大
                tempPool = new CrawlThread[count];
                for(int index = 0; index < count; index++){
                    if(index < threadCount){
                        //将threadPool中的所有线程放入临时数组中
                        tempPool[index] = threadPool[index];
                    }else{
                        //然后将临时数组的剩余空位补齐
                        tempPool[index] = new CrawlThread();
                        tempPool[index].start();
                    }
                }
                //将临时数组赋给threadPool
                threadPool = tempPool;
            }
            threadCount = count;
        }
    }

    public void StartAllThread(){
        if(hasStartAllThread){
            return;
        }
        threadPool = new CrawlThread[threadCount];
        for(int index = 0; index < threadCount; index++){
            threadPool[index] = new CrawlThread();
            threadPool[index].start();
        }
        hasStartAllThread = true;
    }

    private class CrawlThread extends Thread{
        //标志位，指示是否允许此线程跑起来
        private boolean runnable = true;
        @Override
        public void run() {
            while(runnable){
                try {
                    DefaultCrawlerProxy.GetInstance().Crawling(CrawlQueueHandler.GetInstance().GetConfigEntityOutOfQueue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void DisableThread(){
            runnable = false;
        }
    }
}
