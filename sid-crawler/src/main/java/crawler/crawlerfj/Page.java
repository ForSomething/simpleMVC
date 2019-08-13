package crawler.crawlerfj;

import crawler.crawlerfj.environment.IEnvironmentCore;
import crawler.crawlerfj.function.Callback;
import crawler.crawlerfj.function.ExceptionHandler;
import utils.JsonUtils;
import utils.communication.network.http.Request;
import utils.communication.network.http.Response;

import java.util.HashMap;
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
        if(this.$environmentCore == null){
            this.$environmentCore = IEnvironmentCore.getInstance(this.environmentType);
        }
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
            //implements了Cloneable，这个异常应该是不会发生了的
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
            }catch (Exception e){
                e.printStackTrace();
                if(exceptionHandler != null){
                    exceptionHandler.execute(e, page,callback);
                }
            }finally {
                //page.$environmentCore.free(); // todo 这个free好像没有道理，难道异步获取之后就不能再使用这个环境了么
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
