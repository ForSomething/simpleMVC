package Util.HttpUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Map;

public class HttpUtil {
    public static HttpResponse doGet(RequestEntity requestEntity) throws IOException {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = clientBuilder.build();
        HttpGet httpGet = new HttpGet(requestEntity.getRequestURL());
        //加入自定义的请求头
        Map<String,String> requestHeaderMap;
        if((requestHeaderMap = requestEntity.getRequestHeaderMap()) != null && requestHeaderMap.size() > 0){
            for(String key : requestHeaderMap.keySet()){
                httpGet.setHeader(key,requestHeaderMap.get(key));
            }
        }
        return httpClient.execute(httpGet);
    }

    public  static  HttpResponse doPost(RequestEntity requestEntity) throws IOException {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = clientBuilder.build();
        HttpGet httpGet = new HttpGet(requestEntity.getRequestURL());
        return httpClient.execute(httpGet);
    }
}
