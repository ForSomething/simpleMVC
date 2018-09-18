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
    abstract public boolean CanHandle(Object _config);

    abstract public void Crawling(Object _config) throws Exception;

    protected ResponseEntity DoRequest(RequestEntity requestEntity) throws IOException {
        return DefaultRequest.GetInstance().doRequest(requestEntity);
    }
}
