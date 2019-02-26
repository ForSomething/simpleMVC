package crawlerfj.crawler.Impl.SingleStepCrawler;

import common.eventhandlerinterface.BaseEventHandler;
import crawlerfj.crawler.BaseCrawlerConfig;
import toolroom.JsonUtils;
import toolroom.httputil.RequestEntity;
import toolroom.httputil.ResponseEntity;

import java.util.Map;


public class SingleStepConfig extends BaseCrawlerConfig {
    private RequestEntity requestEntity;

    private SingleStep singleStep;

    public RequestEntity getRequestEntity() {
        return requestEntity;
    }

    public SingleStep getSingleStep() {
        return singleStep;
    }

    public void setSingleStep(SingleStep singleStep) {
        this.singleStep = singleStep;
    }

    public void setRequestEntity(RequestEntity requestEntity) {
        this.requestEntity = requestEntity;
    }

    @Override
    public String toString(){
        Map<String,Object> logMap = JsonUtils.parseJsonString2Map(super.toString());
        logMap.put("requestEntity",requestEntity);
        return JsonUtils.parse2Json(logMap);
    }

    @FunctionalInterface
    public interface SingleStep {
        void Execute (ResponseEntity responseEntity, SingleStepConfig singleStepConfig) throws Exception;
    }
}
