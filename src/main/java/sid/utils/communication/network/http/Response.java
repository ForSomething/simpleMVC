package sid.utils.communication.network.http;

import sid.utils.CommonStringUtils;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private int stateCode;

    private String baseUrl;

    private byte[] content;

    private String domain;

    private String protocol;

    private String charSet;

    private Map<String,Object> headerMap = new HashMap<>();

    private Map<String,Object> cookieMap = new HashMap<>();

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public int getStateCode() {
        return stateCode;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public byte[] getContent() {
        return content;
    }

    public String getDomain() {
        return domain;
    }

    public Map<String, Object> getHeaderMap() {
        return headerMap;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, Object> getCookieMap() {
        return cookieMap;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = CommonStringUtils.isEmptyOrWihtespace(charSet) ? "utf-8" : charSet;
    }

    public void setCookieMap(Map<String, Object> cookieMap) {
        setMap(this.cookieMap,cookieMap);
    }

    public void setHeaderMap(Map<String, Object> headerMap) {
        setMap(this.headerMap,headerMap);
    }

    public void setCookie(String key, Object value){
        put(cookieMap,key,value);
    }

    public void setHeader(String key, Object value){
        put(headerMap,key,value);
    }

    public void removeCookie(String key){
        remove(cookieMap,key);
    }

    public void removeHeader(String key){
        remove(headerMap,key);
    }

    public void clearCookie(){
        cookieMap.clear();
    }

    public void clearHeader(){
        headerMap.clear();
    }

    private void put(Map<String,Object> map, String key, Object value){
        if(CommonStringUtils.isEmptyOrWihtespace(key)){
            return;
        }
        map.put(key.trim(),value);
    }

    private void remove(Map<String,Object> map,String key){
        if(map.containsKey(key)){
            map.remove(key);
        }
    }

    private void setMap(Map<String, Object> destMap,Map<String, Object> otherMap){
        if(otherMap == null){
            return;
        }
        destMap.clear();
        if(otherMap.keySet().contains(null)){
            otherMap.remove(null);
        }
        destMap.putAll(otherMap);
    }
}
