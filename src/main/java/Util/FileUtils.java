package Util;

import java.io.*;

public class FileUtils {
    private static FileUtils instance = new FileUtils();

    public static FileUtils GetInstance(){
        return instance;
    }

    public void WriteLines(String[] lines, String fileName,boolean append){
        fileName = fileName.replace("/","\\");
        CreateFolderIfNotExists(fileName.substring(0,fileName.lastIndexOf('\\')));
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileName,append));
            for(String text : lines){
                bufferedWriter.write(text);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void WriteBinaryData(byte[] data,String fileName,boolean append){
        fileName = fileName.replace("/","\\");
        CreateFolderIfNotExists(fileName.substring(0,fileName.lastIndexOf('\\')));
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(fileName,append));
            bos.write(data);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] ReadLinesFromFile(){
        return null;
    }

    private void CreateFolderIfNotExists(String folderName){
        File file = new File(folderName);
        if(!file.exists()) {
            file.mkdirs();
        }
    }
}
