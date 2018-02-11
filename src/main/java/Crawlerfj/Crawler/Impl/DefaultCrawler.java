package Crawlerfj.Crawler.Impl;

import Crawlerfj.Common.StringUtil;
import Crawlerfj.Config.DefaultConfigEntity;
import Crawlerfj.Crawler.ICrawlerfj;
import Crawlerfj.Entity.AttachmentEntity;
import Crawlerfj.Entity.ResponseEntity;
import Crawlerfj.Request.DefaultRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

public class DefaultCrawler implements ICrawlerfj {

    public boolean CanHandle(Object configEntity) {
        if(configEntity != null &&
                DefaultConfigEntity.class.getCanonicalName().equals(configEntity.getClass().getCanonicalName())){
            return true;
        }
        return false;
    }

    public void Crawling(Object _configEntity) {
        try{
            DefaultConfigEntity configEntity = (DefaultConfigEntity)_configEntity;
            DefaultRequest requestHandler = DefaultRequest.GetInstance();
            ResponseEntity responseEntity = requestHandler.doRequest(configEntity.getMethod(), configEntity.getUrl(), configEntity.getParam());

            Document doc = Jsoup.parse(responseEntity.getContent(),responseEntity.getBaseUrl());
            Elements elements = new Elements(doc);
            Elements tempElements;

            //第一层循环遍历需要爬取的元素
            for(DefaultConfigEntity.ElementEntity elementEntity : configEntity.getElementEntityList()){
                //第二层循环按选择器层次结构遍历选择器
                for(String selector : elementEntity.getSelectorList()){
                    tempElements = new Elements();
                    //第三层循环，利用遍历到的选择器从上次获取到的元素列表中获取下一层的元素
                    for(Element element : elements){
                        tempElements.addAll(element.select(selector));
                    }
                    elements = tempElements;
                }
                //里面两次遍历结束之后，获取到的就是第n个需要爬取的元素的列表
                //之后我们就根据handleAction来处理这些元素
                if(elements.size() == 0){
                    continue;
                }
                switch (elementEntity.getHandleAction()){
                    case getTag:doGetTag(elements);break;
                    case redirectTo:doRedirectTo(elements,responseEntity);break;
                    case download:doDownload(elements,responseEntity,elementEntity.getDownloadFolderPath());break;
                    default:break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void doRedirectTo(Elements elements,ResponseEntity resEntity){
        //TODO

    }

    private void doDownload(Elements elements,ResponseEntity resEntity,String folderPath){
        File folder = new File(folderPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
        for(Element element : elements){
            String src = element.attr("src");
            //如果src没有包含域名，则加上域名
            if(src != null && !src.startsWith("http") && resEntity.getDomain() != null){
                src = "http://" + resEntity.getDomain() + src;
            }
            DefaultRequest requestHandler = DefaultRequest.GetInstance();
            FileOutputStream fileOutputStream = null;
            try {
                AttachmentEntity attachmentEntity = requestHandler.getAttachment(src);
                File file = new File(folderPath + "\\" + attachmentEntity.getFileName() + "." + attachmentEntity.getFileType());
                if(!file.exists()){
                    file.createNewFile();
                }
                fileOutputStream = new FileOutputStream(file,false);
                fileOutputStream.write(attachmentEntity.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doGetTag(Elements elements){
        //写文件
        File file = new File("C:\\Users\\Administrator\\Desktop\\test.txt");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file,false);
            for(Element element : elements){
                fileOutputStream.write((element.outerHtml() + "\n").getBytes("utf-8"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fileOutputStream != null){
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


