package Crawlerfj.Common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    //获取域名的正则，匹配以//开头的并且之后的子串中不包含/的字符串,//后面的子串必须包含至少一个字符
    private static String getDomainRegex = "//[^/]{1,}";

    public static String getDomain(String url){
        Matcher matcher = Pattern.compile(getDomainRegex).matcher(url);
        if(matcher.find()){
            return matcher.group(0).substring(2); //去掉前面的//
        }
        return null;
    }
}
