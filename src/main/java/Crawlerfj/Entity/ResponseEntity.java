package Crawlerfj.Entity;

public class ResponseEntity {
    private int stateCode;

    private String baseUrl;

    private String content;

    private String domain;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setContent(String contentHtml) {
        this.content = contentHtml;
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

    public String getContent() {
        return content;
    }

    public String getDomain() {
        return domain;
    }
}
