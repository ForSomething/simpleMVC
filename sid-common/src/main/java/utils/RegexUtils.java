package utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    private static Map<String,Pattern> patternMap = new HashMap<>();

    //获取域名的正则，匹配以//开头的并且之后的子串中不包含/的字符串,//后面的子串必须包含至少一个字符
    public static String getDomain(String url){
        String temp;
        //去掉前面的//
        return StringUtils.isNullOrEmpty(temp = getFirstSubstring(StringUtils.toString(url),"//[^/]{1,}")) ? temp : temp.substring(2);
    }

    public static String getProtocol(String url){
        if(StringUtils.isNullOrWihtespace(url)){
            return StringUtils.emptyString;
        }
        int protocolEndIndex = url.indexOf("://");
        if(protocolEndIndex <= 0){
            return StringUtils.emptyString;
        }
        return url.substring(0,protocolEndIndex);
    }

    private static Pattern illegalCharInFileNamePattern = compileRegex("[\\s\\\\/:\\*\\?\\\"<>\\|]",true);
    public static String fileNameFilter(String fileName){
        return illegalCharInFileNamePattern.matcher(fileName).replaceAll(StringUtils.emptyString);
    }

    public static String getExtensions(String path){
        String temp;
        //去掉前面的.
        return StringUtils.isNullOrEmpty(temp = getFirstSubstring(StringUtils.toString(path),"\\.\\w+$")) ? temp : temp.substring(1);
    }

    public static String getFirstSubstring(String srcString,String regex){
        String[] temp = getAllSubstring(srcString,regex);
        return temp.length > 0 ? temp[0] : StringUtils.emptyString;
    }

    public static String[] getAllSubstring(String srcString,String regex){
        List<String> resultList = new LinkedList<>();
        if(StringUtils.isNullOrWihtespace(regex) || StringUtils.isNullOrEmpty(srcString)){
            return resultList.toArray(new String[0]);
        }
        Pattern pattern = compileRegex(regex,false);
        Matcher matcher = pattern.matcher(srcString);
        while (matcher.find()){
            resultList.add(matcher.group());
        }
        return resultList.toArray(new String[0]);
    }

    private static Pattern compileRegex(String regex,boolean cache){
        if(patternMap.containsKey(regex)){
            return patternMap.get(regex);
        }
        Pattern pattern = Pattern.compile(regex);
        if(cache){
            patternMap.put(regex,pattern);
        }
        return pattern;
    }
}
