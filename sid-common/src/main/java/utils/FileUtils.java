package utils;

import java.io.*;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.regex.Matcher;

public class FileUtils {
    public static void writeLine(String line, String fileName,boolean append) throws IOException {
        String[] lines = {line};
        writeLines(lines,fileName,append);
    }

    public static void writeLines(String[] lines, String fileName,boolean append) throws IOException {
        fileName = fileName.replaceAll("[/\\\\]",Matcher.quoteReplacement(File.separator));
        createFolderIfNotExists(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName,append));
        try{
            for(String text : lines){
                bufferedWriter.write(text);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        }finally {
            bufferedWriter.close();
        }
    }

    public static void write(String text, String fileName, boolean append) throws IOException {
        write(text == null ? null : text.getBytes(),fileName,append);
    }

    public static void write(byte[] data, String fileName, boolean append) throws IOException {
        fileName = fileName.replaceAll("[/\\\\]",Matcher.quoteReplacement(File.separator));
        createFolderIfNotExists(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName,append));
        try{
            bos.write(data);
            bos.flush();
        }finally {
            bos.close();
        }
    }

    public static String[] readLines(String fileName) throws IOException {
        fileName = fileName.replaceAll("[/\\\\]",Matcher.quoteReplacement(File.separator));
        File file = new File(fileName);
        if(!file.exists()){
            return null;
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        ArrayList<String> lines = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null){
            lines.add(line);
        }
        return lines.toArray(new String[0]);
    }

    private static void createFolderIfNotExists(String filePath){
        int lastsfsdf = filePath.lastIndexOf(File.separator);
        if(lastsfsdf <= 0){
            return;
        }
        filePath = filePath.substring(0,lastsfsdf);
        File file = new File(filePath);
        if(!file.exists()) {
            file.mkdirs();
        }
    }
}
