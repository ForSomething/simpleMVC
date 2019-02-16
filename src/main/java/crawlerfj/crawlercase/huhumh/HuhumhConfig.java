package crawlerfj.crawlercase.huhumh;

import common.eventhandlerinterface.BaseEventHandler;
import crawlerfj.common.Const;
import crawlerfj.crawler.CrawlerProxy;
import crawlerfj.crawler.Impl.SingleStepCrawler.SingleStep;
import crawlerfj.crawler.Impl.SingleStepCrawler.SingleStepConfig;
import crawlerfj.crawler.Impl.browsercrawler.BrowserCrawlerConfig;
import crawlerfj.crawlercase.dataentity.Chapter;
import crawlerfj.crawlercase.dataentity.Error;
import toolroom.FileUtils;
import toolroom.httputil.RequestEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class HuhumhConfig {
    static BaseEventHandler exceptionHandler;

    static SingleStep chapterListPagingStep;

    static BrowserCrawlerConfig.BrowserTask contentPageBrowserTask;

    static BaseEventHandler sleepBeforeCrawling;

    static SingleStep downloadImgStap;

    static String imagePathFormatString;

    public static void ExecuteByConfig() throws Exception {
        init();
        HashMap<String,String> cond = new HashMap<>();
        cond.put("parentID","b4e1e14686da49c5b1a13a09b4a81e47");
        List<Chapter> chapterList = Chapter.load(cond);
        Integer imageIndex = 1;
        for(Chapter chapter : chapterList){
            cond.clear();
            cond.put("parentID",chapter.getId());
            List<Chapter> imageList = Chapter.load(cond);
            for(Chapter image : imageList){
                SingleStepConfig startUpConfig = new SingleStepConfig();
                startUpConfig.setRequestEntity(new RequestEntity());
                startUpConfig.getRequestEntity().setRequestURL(image.getChapterContent());
                startUpConfig.getRequestEntity().setRequestMethod(Const.requestMethod.GET);
                startUpConfig.setSingleStep(downloadImgStap);
                startUpConfig.setExceptionHandler(exceptionHandler);
                startUpConfig.setUserParam("imageIndex",imageIndex);
                startUpConfig.setUserParam("chapterName",chapter.getChapterName());
                startUpConfig.setUserParam("pageIndex",image.getChapterName());
                CrawlerProxy.Crawling(startUpConfig);
                imageIndex++;
            }
        }
//        SingleStepConfig startUpConfig = new SingleStepConfig();
//        startUpConfig.setRequestEntity(new RequestEntity());
//        startUpConfig.getRequestEntity().setRequestURL("http://www.huhumh.com/huhu8211.html");
//        startUpConfig.getRequestEntity().setRequestMethod(Const.requestMethod.GET);
//        startUpConfig.setSingleStep(chapterListPagingStep);
//        startUpConfig.setExceptionHandler(exceptionHandler);
//        CrawlerProxy.Crawling(startUpConfig);
    }

    private static void init(){
        exceptionHandler = parameter -> {
            BrowserCrawlerConfig theConfig = (BrowserCrawlerConfig)parameter;
            Exception exception = theConfig.getUserParam("exception");
            try {
                Integer retryTimes = theConfig.getUserParam("retryTimes");
                retryTimes = retryTimes == null ? 0 : retryTimes;
                theConfig.setUserParam("retryTimes",retryTimes);
                if(retryTimes < 3){
                    new Error(new Timestamp(System.currentTimeMillis()),exception.toString() + "---->开始重新进行爬取，本次为第" + retryTimes + "次",
                            theConfig.getUserParam("url")).insert();
                    retryTimes++;
                    CrawlerProxy.Crawling(theConfig);
                }else{
                    new Error(new Timestamp(System.currentTimeMillis()),exception.toString() + "---->进行了多次重新爬取，都未成功",
                            theConfig.getUserParam("url")).insert();
                }
            }catch (Exception e){
                try {
                    new Error(new Timestamp(System.currentTimeMillis()),"异常处理方法里出现异常，e为：" + e.getMessage(),
                            theConfig.getUserParam("url")).insert();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        };

        sleepBeforeCrawling = (parameter) -> {
            try {
                //防止反爬虫，在爬取之前先睡700毫秒
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        imagePathFormatString = "E:\\source\\源氏物语\\%s-%s-%s.jpg";
        downloadImgStap = (responseEntity, config) -> {
            Integer imageIndex = config.getUserParam("imageIndex");
            String chapterName = config.getUserParam("chapterName");
            String pageIndex = config.getUserParam("pageIndex");
            String imagePath = String.format(imagePathFormatString,imageIndex.toString(),chapterName,pageIndex);
            try {
                FileUtils.WriteBinaryData(responseEntity.getContent(),imagePath,false);
            } catch (IOException e) {
                try {
                    new Error(new Timestamp(System.currentTimeMillis()),"写入文件发生异常，e为：" + e.toString(),
                            imagePath).insert();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        };

        contentPageBrowserTask = (browser,config) -> {
            try{
                String url = config.getUserParam("url");
                browser.toPage(url);
                int pageIndex = 1;
                Document _document = Jsoup.parse(browser.getPageContent());
                Elements pagesUrl = _document.selectFirst("#iPageHtm").select("a");
                String chapterID = UUID.randomUUID().toString().replace("-","");
                new Chapter(chapterID,config.getUserParam("chapterSetID"),config.getUserParam("chapterName"),url,
                        String.valueOf(pagesUrl.size()),config.getUserParam("chapterIndex")).insert();
                for(Element pageUrl : pagesUrl){
                    String script = pageUrl.attr("onclick");
                    script = script.replace(";return false;","");
                    browser.executeScript(script);
                    _document = Jsoup.parse(browser.getPageContent());
                    Element img = _document.selectFirst("#iBody").selectFirst("img");
                    String pageID = UUID.randomUUID().toString().replace("-","");
                    try {
                        new Chapter(pageID,chapterID,"第" + pageIndex + "页",img.attr("src"),"",pageIndex).insert();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pageIndex++;
                }
            }catch (Exception e){
                config.setUserParam("exception",e);
                exceptionHandler.Excute(config);
            }
        };

        chapterListPagingStep = (_responseEntity, _config) ->{
            try {
                String content = new String(_responseEntity.getContent(),"utf-8");
                Document document = Jsoup.parse(content);
                Element chapterDiv = document.selectFirst(".cVolList");
                Elements chapterDivChildren = chapterDiv.children();
                String chapterType = "unkown";
                String chapterSetID = "";
                int chapterSetIndex = 0;
                int chapterIndex = 0;
                for(Element one : chapterDivChildren){
                    if("div".equals(one.tagName())){
                        chapterType = one.html();
                        chapterSetID = UUID.randomUUID().toString().replace("-","");
                        new Chapter(chapterSetID,"",chapterType,_config.getRequestEntity().getRequestURL(),"",chapterSetIndex++).insert();
                        continue;
                    }
                    if(!"周刊杂志每周每月连载单集".equals(chapterType)){
                        continue;
                    }
                    Elements chapterLinks = one.select("a");
                    for(Element chapterLink : chapterLinks){
                        String url = _responseEntity.getProtocol() + "://" + _responseEntity.getDomain() + chapterLink.attr("href");
                        BrowserCrawlerConfig browserCrawlerConfig = new BrowserCrawlerConfig();
                        browserCrawlerConfig.setUserParam("chapterSetID",chapterSetID);
                        browserCrawlerConfig.setUserParam("url",url);
                        browserCrawlerConfig.setUserParam("chapterName",chapterLink.html());
                        browserCrawlerConfig.setUserParam("chapterIndex",chapterIndex++);
                        browserCrawlerConfig.setBrowserTask(contentPageBrowserTask);
                        browserCrawlerConfig.setExceptionHandler(exceptionHandler);
                        CrawlerProxy.Crawling(browserCrawlerConfig);
                    }
                    chapterType = "unkown";
                }
            }catch (Exception e){
                _config.setUserParam("exception",e);
                exceptionHandler.Excute(_config);
            }
        };
    }
}
