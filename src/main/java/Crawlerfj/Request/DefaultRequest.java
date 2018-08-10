package Crawlerfj.Request;


import Crawlerfj.Common.Regex;
import Util.HttpUtil.HttpUtil;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import Util.HttpUtil.*;

import java.io.IOException;
import java.io.InputStream;

public class DefaultRequest {
    private static DefaultRequest instance = new DefaultRequest();

    public static DefaultRequest GetInstance(){
        return instance;
    }

    private DefaultRequest(){}

    public ResponseEntity doRequest(RequestEntity requestEntity) throws IOException {
        HttpResponse response;
        switch (requestEntity.getRequestMethod()){
            case GET: response = HttpUtil.doGet(requestEntity);break;
            case POST:response = HttpUtil.doPost(requestEntity);break;
            default: response = null;
        }
        if(response != null){
            ResponseEntity responseEntity = null;
            if(response.getStatusLine().getStatusCode() == 301){
                //如果状态code是301，说明需要重定向
                Header[] headers = response.getHeaders("Location");
                if(headers != null && headers.length > 0){
                    //获取重定向url，进行重定向
                    //TODO 重定向应该还要可以用新的请求头和请求参数
//                    String redirectUrl = headers[0].getValue();
//                    entity = doRequest(method,redirectUrl,param);
                }
            }else{
                InputStream responseIS = response.getEntity().getContent();
                int bufferSize = 1024;
                byte[] content = new byte[0];
                byte[] buffer = new byte[bufferSize];
                while (true){
                    int count = responseIS.read(buffer);
                    if(count <= 0){
                        break;
                    }
                    byte[] newArray = new byte[content.length + count];
                    System.arraycopy(content,0,newArray,0,content.length);
                    System.arraycopy(buffer,0,newArray,content.length,count);
                    content = newArray;
                }
                responseEntity = new ResponseEntity();
                responseEntity.setStateCode(response.getStatusLine().getStatusCode());
                responseEntity.setBaseUrl(requestEntity.getRequestURL());
                responseEntity.setContent(content);
                //先从http头中获取域名，如果获取不到，则用正则表达式从url中抠出域名
                Header[] headers = response.getHeaders("Host");
                if(headers != null && headers.length > 0){
                    responseEntity.setDomain(headers[0].getValue());
                }else{
                    responseEntity.setDomain(Regex.getDomain(requestEntity.getRequestURL()));
                }
            }
            return responseEntity;
        }
        return null;
    }
}
