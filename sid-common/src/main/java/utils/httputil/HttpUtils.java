package utils.httputil;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import utils.RegexUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
    public static Response doGet(Request request) throws IOException {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = clientBuilder.build();
        HttpGet httpGet = new HttpGet(request.getRequestURL());
        //加入自定义的请求头
        Map<String,String> requestHeaderMap;
        if((requestHeaderMap = request.getRequestHeaderMap()) != null && requestHeaderMap.size() > 0){
            for(String key : requestHeaderMap.keySet()){
                httpGet.setHeader(key,requestHeaderMap.get(key));
            }
        }
        HttpResponse response = httpClient.execute(httpGet);
        Response responseEntity = new Response();
        responseEntity.setStateCode(response.getStatusLine().getStatusCode());
        responseEntity.setBaseUrl(request.getRequestURL());
        responseEntity.setContent(ReadInputStreamIntoByteArray(response.getEntity().getContent()));
        //先从http头中获取域名，如果获取不到，则用正则表达式从url中抠出域名
        Header[] headers = response.getHeaders("Host");
        if(headers != null && headers.length > 0){
            responseEntity.setDomain(headers[0].getValue());
        }else{
            responseEntity.setDomain(RegexUtils.getDomain(request.getRequestURL()));
        }
        //获取协议
        responseEntity.setProtocol(response.getProtocolVersion().getProtocol());
        Map<String,String> responseHeaderMap = new HashMap<>();
        headers = response.getAllHeaders();
        for(Header header : headers){
            responseHeaderMap.put(header.getName(),header.getValue());
        }
        responseEntity.setResponseHeaderMap(responseHeaderMap);
        return responseEntity;
    }

    public static Response doPost(Request request) throws IOException {
        return null;
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
