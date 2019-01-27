package toolroom.httputil;

import java.util.Map;

public class ResponseEntity {
    private int stateCode;

    private String baseUrl;

    private byte[] content;

    private String domain;

    private String protocol;

    private Map<String,String> responseHeaderMap;

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

    public void setResponseHeaderMap(Map<String, String> responseHeaderMap) {
        this.responseHeaderMap = responseHeaderMap;
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

    public Map<String, String> getResponseHeaderMap() {
        return responseHeaderMap;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
