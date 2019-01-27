package toolroom;

public class Const {
    //http请求的超时时间，单位为毫秒
    public static final int HttpConnectTimeOut = 5000;

    //京东搜索列表地址
    public static final String jdSearchURLStr = "https://search.jd.com/Search?keyword=%%kw%%&enc=utf-8&wq=%%kw%%";

    //京东价格请求地址
    public static final String jdPriceURLStr = "https://p.3.cn/prices/mgets?skuIds=%%ii%%";
}
