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
        HttpResponse response;
        switch (requestEntity.getRequestMethod()){
            case GET: response = HttpUtils.doGet(requestEntity);break;
            case POST:response = HttpUtils.doPost(requestEntity);break;
            default: response = null;
        }
        if(response == null){
            return null;
        }
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
            responseEntity = new ResponseEntity();
            responseEntity.setStateCode(response.getStatusLine().getStatusCode());
            responseEntity.setBaseUrl(requestEntity.getRequestURL());
            responseEntity.setContent(ReadInputStreamIntoByteArray(response.getEntity().getContent()));
            //先从http头中获取域名，如果获取不到，则用正则表达式从url中抠出域名
            Header[] headers = response.getHeaders("Host");
            if(headers != null && headers.length > 0){
                responseEntity.setDomain(headers[0].getValue());
            }else{
                responseEntity.setDomain(Regex.getDomain(requestEntity.getRequestURL()));
            }

            Map<String,String> responseHeaderMap = new HashMap<>();
            headers = response.getAllHeaders();
            for(Header header : headers){
                responseHeaderMap.put(header.getName(),header.getValue());
            }
            responseEntity.setResponseHeaderMap(responseHeaderMap);
        }
        return responseEntity;
    }

    private static ResponseEntity doRequestByBrowser(RequestEntity requestEntity) throws IOException {
        WebResponse response;
        switch (requestEntity.getRequestMethod()){
            case GET: response = HttpUtils.doGetByBrowser(requestEntity);break;
            case POST:response = HttpUtils.doPostByBrowser(requestEntity);break;
            default: response = null;
        }
        if(response == null){
            return null;
        }
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setStateCode(response.getStatusCode());
        responseEntity.setBaseUrl(requestEntity.getRequestURL());
        responseEntity.setContent(ReadInputStreamIntoByteArray(response.getContentAsStream()));
        //先从http头中获取域名，如果获取不到，则用正则表达式从url中抠出域名
        String domain = response.getResponseHeaderValue("Host");
        if(StringUtils.IsNullOrEmpty(domain)){
            responseEntity.setDomain(Regex.getDomain(requestEntity.getRequestURL()));
        }else{
            responseEntity.setDomain(domain);
        }

        Map<String,String> responseHeaderMap = new HashMap<>();
        for(NameValuePair one : response.getResponseHeaders()){
            responseHeaderMap.put(one.getName(),one.getValue());
        }
        responseEntity.setResponseHeaderMap(responseHeaderMap);
        return responseEntity;
    }

    private static byte[] ReadInputStreamIntoByteArray(InputStream inputStream) throws IOException {
        int bufferSize = 1024;
        byte[] content = new byte[0];
        byte[] buffer = new byte[bufferSize];
        while (true){
            int count = inputStream.read(buffer);
            if(count <= 0){
                break;
            }
            byte[] newArray = new byte[content.length + count];
            System.arraycopy(content,0,newArray,0,content.length);
            System.arraycopy(buffer,0,newArray,content.length,count);
            content = newArray;
        }
        return content;
    }
}
