package crawlerfj.crawler;

import toolroom.httputil.HttpUtils;
import toolroom.httputil.RequestEntity;
import toolroom.httputil.ResponseEntity;

public abstract class ICrawlerfj {
    abstract public boolean CanHandle(Object _config);

    abstract public void Crawling(Object _config) throws Exception;

    protected ResponseEntity DoRequest(RequestEntity requestEntity) throws Exception {
        if(requestEntity.getBrowserConfig() != null){
            switch (requestEntity.getRequestMethod()){
                case GET: return HttpUtils.doGet(requestEntity);
                case POST: return HttpUtils.doPost(requestEntity);
                default: return null;
            }
        }else{
            switch (requestEntity.getRequestMethod()){
                case GET: return HttpUtils.doGetByBrowser(requestEntity);
                case POST:return HttpUtils.doPostByBrowser(requestEntity);
                default: return null;
            }
        }
    }
}
