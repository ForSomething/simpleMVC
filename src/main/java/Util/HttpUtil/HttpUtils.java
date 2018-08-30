package Util.HttpUtil;

import com.gargoylesoftware.htmlunit.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
    static {
        //TODO 这里应该可以配置
        BrowserVersion.setDefault(BrowserVersion.CHROME);
    }

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

    public static HttpResponse doPost(RequestEntity requestEntity) throws IOException {
        return null;
    }

    public static WebResponse doGetByBrowser(RequestEntity requestEntity) throws IOException {
        WebClient webClient;
        if(requestEntity.getBrowserConfig() != null && requestEntity.getBrowserConfig().getBrowserVersion() != null){
            //按照用户配置生成一个浏览器客户端对象
            webClient = InitWebClient(requestEntity.getBrowserConfig().getBrowserVersion());
        }else{
            //生成一个默认的浏览器对象
            webClient = InitWebClient(null);
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

        Page page = webClient.getPage(requestEntity.getRequestURL());
        //先阻塞在这里，等待js渲染
        webClient.waitForBackgroundJavaScript(requestEntity.getBrowserConfig().getWaitForJSRenderingTime());
        return page.getWebResponse();
    }

    public static WebResponse doPostByBrowser(RequestEntity requestEntity) throws IOException {
        return null;
    }

    private static WebClient InitWebClient(BrowserVersion browserVersion){
        WebClient webClient;
        if(browserVersion != null){
            //按照用户配置生成一个浏览器客户端对象
            webClient = new WebClient(browserVersion);
        }else{
            //生成一个默认的浏览器对象
            webClient = new WebClient(BrowserVersion.getDefault());
        }
        webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常, 这里选择不需要
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常, 这里选择不需要
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);//是否启用CSS, 因为不需要展现页面, 所以不需要启用
        webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX
        return webClient;
    }
}
