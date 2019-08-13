package utils.communication.network.http;

import utils.RegexUtils;
import utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Request implements Cloneable {
    private String requestURL = null;

    private String domain = null;

    private String charSet = "utf-8";

    private int timeoutSeconds = 0;

    private Map<String,String> headerMap = new HashMap<>();

    private Map<String,String> paramMap = new HashMap<>();

    private Map<String,String> cookieMap = new HashMap<>();

    private RequestMethod requestMethod = RequestMethod.GET;

    public String getRequestURL() {
        return requestURL;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public Map<String, String> getCookieMap() {
        return cookieMap;
    }

    public Map<String, String> getParamMap() {
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
        this.domain = RegexUtils.getDomain(requestURL);
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setHeader(String key, String value){
        put(headerMap,key,value);
    }

    public void setParam(String key,String value){
        put(paramMap,key,value);
    }

    public void setCookie(String key,String value){
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
        this.charSet = StringUtils.isNullOrWihtespace(charSet) ? "utf-8" : charSet;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        setMap(this.headerMap,headerMap);
    }

    public void setMap(Map<String, String> paramMap) {
        setMap(this.paramMap,paramMap);
    }

    public void setCookieMap(Map<String, String> cookieMap) {
        setMap(this.cookieMap,cookieMap);
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : 0;
    }

    private void setMap(Map<String, String> destMap, Map<String, String> otherMap){
        if(otherMap == null){
            return;
        }
        destMap.clear();
        if(otherMap.keySet().contains(null)){
            otherMap.remove(null);
        }
        destMap.putAll(otherMap);
    }

    private void put(Map<String,String> map, String key, String value){
        if(StringUtils.isNullOrWihtespace(key)){
            return;
        }
        map.put(key.trim(),value);
    }

    private void remove(Map<String,String> map,String key){
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
