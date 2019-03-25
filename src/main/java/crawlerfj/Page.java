package crawlerfj;

import common.constvaslue.Events;
import crawlerfj.environment.IEnvironmentCore;
import crawlerfj.function.Callback;
import crawlerfj.function.ExceptionHandler;
import dao.bo.CrawlerLog;
import eventManegement.EventManeger;
import toolroom.JsonUtils;
import toolroom.httputil.Request;
import toolroom.httputil.Response;

import java.io.*;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Page implements Cloneable {
    private static ThreadPoolExecutor threadPoolExecutor;

    private Request request;

    private Response $response;

    private HashMap<String,Object> dataStore = new HashMap<>();

    private EnvironmentType environmentType;

    private IEnvironmentCore $environmentCore;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(50,50,1, TimeUnit.MINUTES,new LinkedBlockingQueue<>());
    }

    private Page(){}

    public Page(Request request, EnvironmentType environmentType){
        this.request = request;
        this.environmentType = environmentType;
    }

    public Page(String url, EnvironmentType environmentType){
        this.request = new Request();
        this.request.setRequestURL(url);
        this.request.setRequestMethod(Request.RequestMethod.GET);
        this.environmentType = environmentType;
    }

    public Page fetchResponseSync() throws Exception {
        this.setResponse(this.$environmentCore.request(this.getRequest()));
        return this;
    }

    public void fetchResponseAsyn(Callback callback, ExceptionHandler exceptionHandler){
        _fetchResponseAsyn(callback,exceptionHandler);
    }

    private void _fetchResponseAsyn(Callback callback, ExceptionHandler exceptionHandler){
        Page page;
        try {
            page = this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        threadPoolExecutor.submit(()->{
            if(page.$environmentCore == null){
                page.$environmentCore = IEnvironmentCore.getInstance(page.environmentType);
            }
            Object result = null;
            try{
                page.setResponse(page.$environmentCore.request(page.getRequest()));
                callback.execute(page);
//                new CrawlerLog(new Timestamp(System.currentTimeMillis()), CrawlerLog.LogType.NORMAL,"爬取完成", page.toString()).insert();
                EventManeger.on(Events.ON_THREAD_COMPLETA);
            }catch (Exception e){
                e.printStackTrace();
//                new CrawlerLog(new Timestamp(System.currentTimeMillis()), CrawlerLog.LogType.ERROR,e.toString(), page.toString()).insert();
                EventManeger.on(Events.ON_THREAD_ERROR);
                if(exceptionHandler != null){
                    exceptionHandler.execute(e, page,callback);
                }
            }finally {
                page.$environmentCore.free();
            }
            return result;
        });
    }

    public <T> T getStoringData(String key) {
        return (T)dataStore.get(key);
    }

    public <T> Page storeData(String key,T userParam) {
        this.dataStore.put(key,userParam);
        return this;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return $response;
    }

    private void setResponse(Response response) {
        this.$response = response;
    }

    public Page clone() throws CloneNotSupportedException {
        Page newPage = new Page();
        newPage.setRequest(this.request.clone());
        newPage.dataStore = (HashMap<String, Object>) this.dataStore.clone();
        newPage.environmentType = this.environmentType;
        return newPage;
    }

    @Override
    public String toString(){
        HashMap<String,Object> logMap = new HashMap<>();
        logMap.put("storingData",dataStore);
        logMap.put("request",request);
        logMap.put("responseCode",$response == null ? "未获取到response" : $response.getStateCode());
        return JsonUtils.parse2Json(logMap);
    }
}
