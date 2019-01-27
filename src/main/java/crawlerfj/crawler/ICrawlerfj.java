package crawlerfj.crawler;

import toolroom.httputil.RequestEntity;
import toolroom.httputil.ResponseEntity;
import crawlerfj.request.DefaultRequest;

public abstract class ICrawlerfj {
    abstract public boolean CanHandle(Object _config);

    abstract public void Crawling(Object _config) throws Exception;

    protected ResponseEntity DoRequest(RequestEntity requestEntity) throws Exception {
        return DefaultRequest.GetInstance().doRequest(requestEntity);
    }
}
