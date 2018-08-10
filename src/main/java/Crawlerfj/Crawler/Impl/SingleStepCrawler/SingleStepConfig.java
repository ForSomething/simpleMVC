package Crawlerfj.Crawler.Impl.SingleStepCrawler;

import Util.HttpUtil.RequestEntity;


public class SingleStepConfig {
    private RequestEntity requestEntity = null;

    private SingleStep singleStep = null;

    private Object userParam;

    public SingleStepConfig(){

    }

    public SingleStepConfig(RequestEntity requestEntity,SingleStep singleStep){
        this.requestEntity = requestEntity;
        this.singleStep = singleStep;
    }

    public RequestEntity getRequestEntity() {
        return requestEntity;
    }

    public SingleStep getSingleStep() {
        return singleStep;
    }

    public <T> T getUserParam() {
        return (T)userParam;
    }

    public void setSingleStep(SingleStep singleStep) {
        this.singleStep = singleStep;
    }

    public void setRequestEntity(RequestEntity requestEntity) {
        this.requestEntity = requestEntity;
    }

    public <T> void setUserParam(T userParam) {
        this.userParam = userParam;
    }
}
