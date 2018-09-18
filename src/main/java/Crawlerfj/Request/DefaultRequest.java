package Crawlerfj.Request;


import Crawlerfj.Common.Regex;
import Util.HttpUtil.HttpUtils;
import Util.StringUtils;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import Util.HttpUtil.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DefaultRequest {
    private static DefaultRequest instance = new DefaultRequest();

    public static DefaultRequest GetInstance(){
        return instance;
    }

    private DefaultRequest(){}

    public ResponseEntity doRequest(RequestEntity requestEntity) throws IOException {
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

    private static ResponseEntity doRequestByBrowser(RequestEntity requestEntity) throws IOException {
        WebResponse response;
        switch (requestEntity.getRequestMethod()){
            case GET: return HttpUtils.doGetByBrowser(requestEntity);
            case POST:return HttpUtils.doPostByBrowser(requestEntity);
            default: return null;
        }
    }
}
