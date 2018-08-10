package Util;

import java.io.*;

public class FileUtil {
    private static FileUtil instance = new FileUtil();

    public static FileUtil GetInstance(){
        return instance;
    }

    public void WriteLines(String[] lines, String fileName){
        fileName = fileName.replace("/","\\");
        CreateFolder(fileName.substring(0,fileName.lastIndexOf('\\')));
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileName,true));
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

    public void WriteBinaryData(byte[] data,String fileName){
        fileName = fileName.replace("/","\\");
        CreateFolder(fileName.substring(0,fileName.lastIndexOf('\\')));
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(fileName));
            bos.write(data);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CreateFolder(String folderName){
        File file = new File(folderName);
        file.mkdirs();
    }
}
