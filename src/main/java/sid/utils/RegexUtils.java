package sid.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegexUtils {
    private static Map<String,Pattern> patternMap = new HashMap<>();

    static Pattern compileRegex(String regex, boolean cache){
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
