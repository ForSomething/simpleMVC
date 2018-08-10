package Crawlerfj.Crawler;

import Util.HttpUtil.RequestEntity;
import Util.HttpUtil.ResponseEntity;
import Crawlerfj.Request.DefaultRequest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ICrawlerfj {
    private ThreadPoolExecutor threadPoolExecutor;

    private int number = 0;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private static Date date = new Date();

    abstract public boolean CanHandle(Object _config);

    abstract public void Crawling(Object _config);

    public ICrawlerfj(){
        threadPoolExecutor = new ThreadPoolExecutor(5,5,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>());
    }

    protected String DoRequestAsync(RequestEntity requestEntity, RequestFinishCallBack callBack){
        String serialNumber = GetSerialNumber();
        threadPoolExecutor.execute(new CrawlerRunnable(serialNumber,requestEntity,callBack));
        return serialNumber;
    }

    private synchronized String GetSerialNumber(){
        if(number == 100)
            number = 0;
        number += 1;
        return sdf.format(date) + String.valueOf(number);
    }

    private class CrawlerRunnable implements Runnable{
        private String serialNumber;

        private RequestEntity requestEntity;

        private RequestFinishCallBack requestFinishCallBack;

        public CrawlerRunnable(String serialNumber, RequestEntity requestEntity, RequestFinishCallBack requestFinishCallBack){
            this.serialNumber = serialNumber;
            this.requestEntity = requestEntity;
            this.requestFinishCallBack = requestFinishCallBack;
        }
        @Override
        public void run() {
            try {
                requestFinishCallBack.Execute(serialNumber,DefaultRequest.GetInstance().doRequest(requestEntity));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FunctionalInterface
    public interface RequestFinishCallBack{
        public void Execute(String serialNumber, ResponseEntity responseEntity);
    }
}
