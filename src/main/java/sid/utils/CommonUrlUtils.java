package sid.utils;

public class CommonUrlUtils {
    public static String getDomain(String url){
        String temp;
        //获取域名的正则，匹配以//开头的并且之后的子串中不包含/的字符串,//后面的子串必须包含至少一个字符
        return CommonStringUtils.isEmptyOrWihtespace(temp = CommonStringUtils.getFirstMatch(CommonStringUtils.toString(url),"//[^/]{1,}")) ? temp : temp.substring(2);
    }

    public static String getProtocol(String url){
        if(CommonStringUtils.isEmptyOrWihtespace(url)){
            return CommonStringUtils.emptyString;
        }
        int protocolEndIndex = url.indexOf("://");
        if(protocolEndIndex <= 0){
            return CommonStringUtils.emptyString;
        }
        return url.substring(0,protocolEndIndex).trim();
    }
}
