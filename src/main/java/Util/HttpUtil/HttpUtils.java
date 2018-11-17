package Util.HttpUtil;

import Crawlerfj.Common.Regex;
import Util.StringUtils;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
    static {
        //TODO 这里应该可以配置
        BrowserVersion.setDefault(BrowserVersion.CHROME);
    }

    public static ResponseEntity doGet(RequestEntity requestEntity) throws IOException {
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
        HttpResponse response = httpClient.execute(httpGet);
        ResponseEntity responseEntity = new ResponseEntity();
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

    public static ResponseEntity doPost(RequestEntity requestEntity) throws IOException {
        return null;
    }

    public static ResponseEntity doGetByBrowser(RequestEntity requestEntity) throws IOException {
        WebClient webClient;
        if(requestEntity.getBrowserConfig() != null && requestEntity.getBrowserConfig().getBrowserVersion() != null){
            //按照用户配置生成一个浏览器客户端对象
            webClient = InitWebClient(requestEntity.getBrowserConfig().getBrowserVersion(),requestEntity.getBrowserConfig().getWaitForJSRenderingTime());
        }else{
            //生成一个默认的浏览器对象
            webClient = InitWebClient(null,requestEntity.getBrowserConfig().getWaitForJSRenderingTime());
        }
        //加入自定义的请求头
        Map<String,String> requestHeaderMap;
        if((requestHeaderMap = requestEntity.getRequestHeaderMap()) != null && requestHeaderMap.size() > 0){
            for(String key : requestHeaderMap.keySet()){
                webClient.addRequestHeader(key,requestHeaderMap.get(key));
            }
        }
        //加入cookie
        Map<String,String> cookieMap;
        if((cookieMap = requestEntity.getCookieMap()) != null && cookieMap.size() > 0){
            URL url = new URL(requestEntity.getRequestURL());
            for(String key : cookieMap.keySet()){
                webClient.addCookie(key,url,cookieMap.get(key));
            }
        }

        //获取页面
        Page page = webClient.getPage(requestEntity.getRequestURL());
        //等待一段时间，让js有时间执行
        webClient.waitForBackgroundJavaScript(requestEntity.getBrowserConfig().getWaitForJSRenderingTime());
        WebResponse response = page.getWebResponse();
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
        responseEntity.setProtocol(page.getUrl().getProtocol());

        Map<String,String> responseHeaderMap = new HashMap<>();
        for(NameValuePair one : response.getResponseHeaders()){
            responseHeaderMap.put(one.getName(),one.getValue());
        }
        responseEntity.setResponseHeaderMap(responseHeaderMap);
        return responseEntity;
    }

    public static ResponseEntity doPostByBrowser(RequestEntity requestEntity) throws IOException {
        return null;
    }

    private static WebClient InitWebClient(BrowserVersion browserVersion,long waitForJS){
        WebClient webClient;
        if(browserVersion != null){
            //按照用户配置生成一个浏览器客户端对象
            webClient = new WebClient(browserVersion);
        }else{
            //生成一个默认的浏览器对象
            webClient = new WebClient(BrowserVersion.getDefault());
        }
        webClient.getOptions().setTimeout(30000);//设置“浏览器”的请求超时时间
        webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常, 这里选择不需要
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常, 这里选择不需要
        webClient.getOptions().setCssEnabled(true);//是否启用CSS, 有些js会计算css相关东西，所以是true
        webClient.getOptions().setDownloadImages(true);//是否下载图片，最好还是下载，做得和真的浏览器一样最好
        webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX，这个的作用是告诉浏览器在ajax结束后重新同步异步的XHR
        webClient.getOptions().setScreenHeight(1080);
        webClient.getOptions().setScreenWidth(1920);
//        webClient.waitForBackgroundJavaScript(waitForJS);//设置JS后台等待执行时间
//        webClient.setJavaScriptTimeout(waitForJS);//设置JS执行的超时时间

        return webClient;
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
