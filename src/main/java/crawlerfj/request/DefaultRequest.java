package crawlerfj.request;


import toolroom.httputil.HttpUtils;
import com.gargoylesoftware.htmlunit.WebResponse;
import toolroom.httputil.*;

import java.io.IOException;

public class DefaultRequest {
    private static DefaultRequest instance = new DefaultRequest();

    public static DefaultRequest GetInstance(){
        return instance;
    }

    private DefaultRequest(){}

    public ResponseEntity doRequest(RequestEntity requestEntity) throws Exception {
        if(requestEntity.getBrowserConfig() != null){
            return doRequestByBrowser(requestEntity);
        }else{
            return doSimpleRequest(requestEntity);
        }
    }

    private static ResponseEntity doSimpleRequest(RequestEntity requestEntity) throws IOException {
        ResponseEntity response;
        switch (requestEntity.getRequestMethod()){
            case GET: response = HttpUtils.doGet(requestEntity);break;
            case POST:response = HttpUtils.doPost(requestEntity);break;
            default: response = null;
        }
        if(response == null){
            return null;
        }
        if(response.getStateCode() == 301){
            //TODO 如果状态code是301，说明需要重定向
//            Header[] headers = response.getHeaders("Location");
//            if(headers != null && headers.length > 0){
//                //获取重定向url，进行重定向
                //TODO 重定向应该还要可以用新的请求头和请求参数
//                    String redirectUrl = headers[0].getValue();
//                    entity = doRequest(method,redirectUrl,param);
//            }
        }
        return response;
    }

    private static ResponseEntity doRequestByBrowser(RequestEntity requestEntity) throws Exception {
        WebResponse response;
        switch (requestEntity.getRequestMethod()){
            case GET: return HttpUtils.doGetByBrowser(requestEntity);
            case POST:return HttpUtils.doPostByBrowser(requestEntity);
            default: return null;
        }
    }
}
