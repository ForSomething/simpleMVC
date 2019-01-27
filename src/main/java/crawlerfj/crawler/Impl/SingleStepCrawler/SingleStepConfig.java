package crawlerfj.crawler.Impl.SingleStepCrawler;

import common.eventhandlerinterface.BaseEventHandler;
import toolroom.httputil.RequestEntity;

import java.util.HashMap;
import java.util.Map;


public class SingleStepConfig {
    private RequestEntity requestEntity = null;

    private SingleStep singleStep = null;

    private Map<String,Object> userParam;

    BaseEventHandler beforeCrawlingHandler;

    BaseEventHandler afterCrawlingHandler;

    BaseEventHandler exceptionHandler;

    public SingleStepConfig(){
        userParam = new HashMap<>();
    }

    public SingleStepConfig(RequestEntity requestEntity,SingleStep singleStep){
        this();
        this.requestEntity = requestEntity;
        this.singleStep = singleStep;
    }

    public RequestEntity getRequestEntity() {
        return requestEntity;
    }

    public SingleStep getSingleStep() {
        return singleStep;
    }

    public <T> T getUserParam(String key) {
        return (T)userParam.get(key);
    }

    public void setSingleStep(SingleStep singleStep) {
        this.singleStep = singleStep;
    }

    public void setRequestEntity(RequestEntity requestEntity) {
        this.requestEntity = requestEntity;
    }

    public <T> void setUserParam(String key,T userParam) {
        if(this.userParam.containsKey(key)){
            this.userParam.replace(key,userParam);
        }else{
            this.userParam.put(key,userParam);
        }
    }

    public BaseEventHandler getBeforeCrawlingHandler() {
        return beforeCrawlingHandler;
    }

    public void setBeforeCrawlingHandler(BaseEventHandler beforeCrawlingHandler) {
        this.beforeCrawlingHandler = beforeCrawlingHandler;
    }

    public BaseEventHandler getAfterCrawlingHandler() {
        return afterCrawlingHandler;
    }

    public void setAfterCrawlingHandler(BaseEventHandler afterCrawlingHandler) {
        this.afterCrawlingHandler = afterCrawlingHandler;
    }

    public BaseEventHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(BaseEventHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
