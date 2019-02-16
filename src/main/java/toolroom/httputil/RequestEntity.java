package toolroom.httputil;

import crawlerfj.common.Const;
import com.gargoylesoftware.htmlunit.BrowserVersion;

import java.util.HashMap;
import java.util.Map;

public class RequestEntity {
    private String requestURL = null;

    private Map<String,String> requestHeaderMap = null;

    private Map<String,String> requestParamMap = null;

    private Map<String,String> cookieMap = null;

    private Const.requestMethod requestMethod = null;

    public String getRequestURL() {
        return requestURL;
    }

    public Map<String, String> getRequestHeaderMap() {
        return requestHeaderMap;
    }

    public Map<String, String> getCookieMap() {
        return cookieMap;
    }

    public Map<String, String> getRequestParamMap() {
        return requestParamMap;
    }

    public Const.requestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void setRequestMethod(Const.requestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setRequestHeader(String key,String value){
        if(requestHeaderMap == null){
            requestHeaderMap = new HashMap<>();
        }
        if(requestHeaderMap.containsKey(key)){
            requestHeaderMap.replace(key,value);
        }else{
            requestHeaderMap.put(key,value);
        }
    }

    public void setRequestParam(String key,String value){
        if(requestParamMap == null){
            requestParamMap = new HashMap<>();
        }
        if(requestParamMap.containsKey(key)){
            requestParamMap.replace(key,value);
        }else{
            requestParamMap.put(key,value);
        }
    }

    public void setCookie(String key,String value){
        if(cookieMap == null){
            cookieMap = new HashMap<>();
        }
        if(cookieMap.containsKey(key)){
            cookieMap.replace(key,value);
        }else{
            cookieMap.put(key,value);
        }
    }

    public void RemoveRequestHeader(String key){
        if(requestHeaderMap != null && requestHeaderMap.containsKey(key)){
            requestHeaderMap.remove(key);
        }
    }

    public void RemoveRequestParam(String key){
        if(requestParamMap != null && requestParamMap.containsKey(key)){
            requestParamMap.remove(key);
        }
    }

    public void RemoveCookie(String key){
        if(cookieMap != null &&cookieMap.containsKey(key)){
            cookieMap.remove(key);
        }
    }

    public void ClearRequestHeader(){
        if(requestHeaderMap != null){
            requestHeaderMap.clear();
        }
    }

    public void ClearRequestParam(){
        if(requestParamMap != null){
            requestParamMap.clear();
        }
    }

    public void ClearCookie(){
        if(cookieMap != null){
            cookieMap.clear();
        }
    }
}
