package Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Map;

public class HttpHelper {
    public static HttpResponse doGet(String urlStr, Map<String,String> header) throws IOException {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = clientBuilder.build();
        HttpGet httpGet = new HttpGet(urlStr);
        //加入自定义的请求头
        if(header != null && header.size() > 0){
            for(String key : header.keySet()){
                httpGet.setHeader(key,header.get(key));
            }
        }
        return httpClient.execute(httpGet);
    }

    public  static  HttpResponse doPost(String urlStr, Map<String,String> header, Map<String,String> param) throws IOException {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = clientBuilder.build();
        HttpGet httpGet = new HttpGet(urlStr);
        return httpClient.execute(httpGet);
    }
}
