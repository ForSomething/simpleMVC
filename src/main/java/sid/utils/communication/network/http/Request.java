package sid.utils.communication.network.http;

import sid.utils.CommonStringUtils;
import sid.utils.CommonUrlUtils;

import java.util.HashMap;
import java.util.Map;

public class Request implements Cloneable {
    private String requestURL = null;

    private String domain = null;

    private String charSet = "utf-8";

    private int timeoutSeconds = 0;

    private Map<String,Object> headerMap = new HashMap<>();

    private Map<String,Object> paramMap = new HashMap<>();

    private Map<String,Object> cookieMap = new HashMap<>();

    private RequestMethod requestMethod = RequestMethod.GET;

    public String getRequestURL() {
        return requestURL;
    }

    public Map<String, Object> getHeaderMap() {
        return headerMap;
    }

    public Map<String, Object> getCookieMap() {
        return cookieMap;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getDomain() {
        return domain;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
        this.domain = CommonUrlUtils.getDomain(requestURL);
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setHeader(String key, Object value){
        put(headerMap,key,value);
    }

    public void setParam(String key,Object value){
        put(paramMap,key,value);
    }

    public void setCookie(String key,Object value){
        put(cookieMap,key,value);
    }

    public void removeHeader(String key){
        remove(headerMap,key);
    }

    public void removeParam(String key){
        remove(paramMap,key);
    }

    public void removeCookie(String key){
        remove(cookieMap,key);
    }

    public void clearHeader(){
        headerMap.clear();
    }

    public void clearParam(){
        paramMap.clear();
    }

    public void clearCookie(){
        cookieMap.clear();
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = CommonStringUtils.isEmptyOrWihtespace(charSet) ? "utf-8" : charSet;
    }

    public void setHeaderMap(Map<String, Object> headerMap) {
        setMap(this.headerMap,headerMap);
    }

    public void setParamMap(Map<String, Object> paramMap) {
        setMap(this.paramMap,paramMap);
    }

    public void setCookieMap(Map<String, Object> cookieMap) {
        setMap(this.cookieMap,cookieMap);
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : 0;
    }

    private void setMap(Map<String, Object> destMap, Map<String, Object> otherMap){
        if(otherMap == null){
            return;
        }
        destMap.clear();
        if(otherMap.keySet().contains(null)){
            otherMap.remove(null);
        }
        destMap.putAll(otherMap);
    }

    private void put(Map<String,Object> map, String key, Object value){
        if(CommonStringUtils.isEmptyOrWihtespace(key)){
            return;
        }
        map.put(key.trim(),value);
    }

    private void remove(Map<String,Object> map,Object key){
        if(map.containsKey(key)){
            map.remove(key);
        }
    }

    public enum RequestMethod{
        GET,
        POST
    }

    public enum ContentType{
        APPLICATION_JSON("application/json"),
        APPLICATION_XML("application/xml"),
        X_WWW_FORM_URLENCODED("x-www-form-urlencoded"),
        MULTIPART_FORM_DATA("multipart/form-data"),
        TEXT_PLAIN("text/plain");

        private String value;
        ContentType(String value) {
            this.value = value;
        }

        public static ContentType getByActuallyValue(String value){
            ContentType[] items = ContentType.values();
            for(ContentType one : items){
                if(one.toString().equals(value)){
                    return one;
                }
            }
            return null;
        }

        public static boolean containValue(String value){
            try{
                valueOf(value);
                return true;
            }catch (Exception e){
                return false;
            }
        }

        @Override
        public String toString(){
            return value;
        }
    }

    @Override
    public Request clone() throws CloneNotSupportedException {
        return (Request)super.clone();
    }
}
