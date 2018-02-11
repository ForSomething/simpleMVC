package Crawlerfj.Common;

public class StringUtil {
    static public String emptyString = "";

    static public boolean IsNullOrEmpty(String str){
        if(str == null || str.equals("")){
            return true;
        }
        return false;
    }

    static public boolean IsNullOrWihtespace(String str){
        if(str == null || str.trim().equals("")){
            return true;
        }
        return false;
    }
}
