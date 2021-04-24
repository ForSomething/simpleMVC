package sid.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonStringUtils extends StringUtils {
    public static final String emptyString = "";

    public static boolean isEmptyOrWihtespace(String s){
        return isEmpty(s) || isBlank(s);
    }

    public static String toString(Object o){
        return o == null ? emptyString : o.toString().trim();
    }

    public static String getRandomString(int lenth){
        return RandomStringUtils.random(lenth);
    }

    public static String getFirstMatch(String s,String regex){
        String[] temp = getAllMatchs(s,regex);
        return temp.length > 0 ? temp[0] : CommonStringUtils.emptyString;
    }

    public static String[] getAllMatchs(String s,String regex){
        List<String> resultList = new LinkedList<>();
        if(CommonStringUtils.isEmptyOrWihtespace(regex)){
            return resultList.toArray(new String[0]);
        }
        Pattern pattern = RegexUtils.compileRegex(regex,false);
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()){
            resultList.add(matcher.group());
        }
        return resultList.toArray(new String[0]);
    }
}
