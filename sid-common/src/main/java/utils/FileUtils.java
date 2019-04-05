package utils;

import java.io.*;
import java.util.ArrayList;

public class FileUtils {
    public static void WriteLines(String[] lines, String fileName,boolean append) throws IOException {
        fileName = fileName.replace("/","\\");
        CreateFolderIfNotExists(fileName.substring(0,fileName.lastIndexOf('\\')));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName,append));
        for(String text : lines){
            bufferedWriter.write(text);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static void WriteLine(String line, String fileName,boolean append) throws IOException {
        WriteLines(new String[]{line},fileName,append);
    }

    public static void WriteBinaryData(byte[] data, String fileName, boolean append) throws IOException {
        fileName = fileName.replace("/","\\");
        CreateFolderIfNotExists(fileName.substring(0,fileName.lastIndexOf('\\')));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName,append));
        bos.write(data);
        bos.flush();
        bos.close();
    }

    public static String[] ReadLinesFromFile(String fileName) throws IOException {
        fileName = fileName.replace("/","\\");
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
        return lines.toArray(new String[1]);
    }

    private static void CreateFolderIfNotExists(String folderName){
        File file = new File(folderName);
        if(!file.exists()) {
            file.mkdirs();
        }
    }
}
