package sid.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CommonFileUtils extends FileUtils {
    private static Pattern illegalCharInFileNamePattern = RegexUtils.compileRegex("[\\s\\\\/:\\*\\?\\\"<>\\|]",true);
    public static String toLegalFilename(String filename){
        return illegalCharInFileNamePattern.matcher(filename).replaceAll(CommonStringUtils.emptyString);
    }

    public static String getExtensions(String filename){
        filename = CommonStringUtils.toString(filename);
        int index = filename.lastIndexOf(".");
        return index > 0 && index < filename.length() - 1 ? filename.substring(index+1) : CommonStringUtils.emptyString;
    }

    public static void writeStringToFileAsOneLine(File file, String data, boolean append) throws IOException {
        writeLines(file,new ArrayList<String>(1){{add(data);}},append);
    }

    public static void writeStringToFileAsOneLine(File file, String data, String encoding, boolean append) throws IOException {
        writeLines(file,encoding,new ArrayList<String>(1){{add(data);}},append);
    }
}
