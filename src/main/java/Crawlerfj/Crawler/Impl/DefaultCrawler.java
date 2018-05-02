package Crawlerfj.Crawler.Impl;

import Crawlerfj.Config.DefaultConfig.CrawlingTask;
import Crawlerfj.Config.DefaultConfig.DefaultConfigEntity;
import Crawlerfj.CrawlQueue.CrawlQueueHandler;
import Crawlerfj.Crawler.ICrawlerfj;
import Crawlerfj.Entity.AttachmentEntity;
import Crawlerfj.Entity.ResponseEntity;
import Crawlerfj.Request.DefaultRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.LinkedList;

public class DefaultCrawler implements ICrawlerfj {
    public boolean CanHandle(Object configEntity) {
        if(configEntity != null &&
                DefaultConfigEntity.class.getCanonicalName().equals(configEntity.getClass().getCanonicalName())){
            return true;
        }
        return false;
    }

    public void Crawling(Object _configEntity) {
        LinkedList<CrawlingTask> redirectTaskList = new LinkedList<CrawlingTask>();
        try{
            DefaultConfigEntity configEntity = (DefaultConfigEntity)_configEntity;
            DefaultRequest requestHandler = DefaultRequest.GetInstance();
            ResponseEntity responseEntity = requestHandler.doRequest(configEntity.getMethod(),
                    configEntity.getUrl(), configEntity.getRequestHeader(),configEntity.getParam());

            //如果用户设置了ContentFormat，则用ContentFormat先处理一下Content
            if(configEntity.getContentFormatter() != null){
                responseEntity.setContent(configEntity.getContentFormatter().execute(responseEntity.getContent()));
            }
            for(CrawlingTask task : configEntity.getTaskList()){
                switch (task.getTaskType()){
                    case redirect:HandleRedirectTask(task,responseEntity);break;
                    case getContent: GetContent(task,responseEntity);break;
                    case getHtmlElement: GetHtmlElement(task,responseEntity);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void HandleRedirectTask(CrawlingTask taskEntity, ResponseEntity responseEntity){
        //将重定向任务转成一个configEntity，放入配置项队列中，作为一个新的爬取任务来处理
        Document doc = Jsoup.parse(responseEntity.getContent(),responseEntity.getBaseUrl());
        Elements elements = doc.select(taskEntity.getSelector());
        for(Element element : elements){
            String url = element.attr("href");
            DefaultConfigEntity newConfigEntity = new DefaultConfigEntity();
            newConfigEntity.setUrl(url);
            newConfigEntity.setMethod(taskEntity.getMethod());
            newConfigEntity.setParam(taskEntity.getParam());
            newConfigEntity.setRequestHeader(taskEntity.getRequestHeader());
            newConfigEntity.setTaskList(taskEntity.getTaskList());
            newConfigEntity.setContentFormatter(taskEntity.getContentFormatter());
            CrawlQueueHandler.GetInstance().PutConfigEntityIntoQueue(newConfigEntity);
        }
    }

    private void GetHtmlElement(CrawlingTask taskEntity, ResponseEntity responseEntity){
        Document doc = Jsoup.parse(responseEntity.getContent(),responseEntity.getBaseUrl());
        Elements elements = doc.select(taskEntity.getSelector());
        //之后我们就根据handleAction来处理这些元素
        if(elements.size() == 0){
            return;
        }
        switch (taskEntity.getElementHandleAction()){
            case getTag:doGetTag(elements);break;
            case download:doDownload(elements,responseEntity,taskEntity.getDownloadFolderPath());break;
            default:break;
        }
    }

    private void GetContent(CrawlingTask taskEntity, ResponseEntity responseEntity){
        //TODO 因为存在response的content中不完全是JSON的情况，就先把conten写到文件中
        File file = new File("C:\\Users\\Administrator\\Desktop\\scrip.txt");
        OutputStreamWriter writer = null;

        try {
            writer = new FileWriter(file,false);
            writer.write(responseEntity.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
            fileOutputStream = new FileOutputStream(file,true);
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


