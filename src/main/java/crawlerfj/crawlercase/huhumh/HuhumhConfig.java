package crawlerfj.crawlercase.huhumh;

import common.eventhandlerinterface.BaseEventHandler;
import crawlerfj.common.Const;
import crawlerfj.crawler.CrawlerProxy;
import crawlerfj.crawler.Impl.SingleStepCrawler.SingleStep;
import crawlerfj.crawler.Impl.SingleStepCrawler.SingleStepConfig;
import crawlerfj.crawlercase.dataentity.Chapter;
import crawlerfj.crawlercase.dataentity.Error;
import toolroom.httputil.RequestEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class HuhumhConfig {
    static BaseEventHandler exceptionHandler;

    static SingleStep chapterListPagingStep;

    static SingleStep chapterContentPagingStep;

    static ReentrantLock lock = new ReentrantLock();

    static BaseEventHandler sleepBeforeCrawling;

    private static String logFileName = "E:\\yuanjunwuyu\\log.txt";

    private static String errLogFileName = "E:\\yuanjunwuyu\\error.txt";

    private static String chapterFileNamePrefix = "E:\\yuanjunwuyu\\";

    public static void ExecuteByConfig() throws Exception {
        init();
        SingleStepConfig startUpConfig = new SingleStepConfig();
        startUpConfig.setRequestEntity(new RequestEntity());
        startUpConfig.getRequestEntity().setRequestURL("http://www.huhumh.com/huhu8211.html");
        startUpConfig.getRequestEntity().setRequestMethod(Const.requestMethod.GET);
        startUpConfig.setSingleStep(chapterListPagingStep);
        startUpConfig.setExceptionHandler(exceptionHandler);
        CrawlerProxy.Crawling(startUpConfig);
    }

    private static void init(){
        String driverPath = System.getProperty("webdriver.chrome.driver");
        if(driverPath == null){
            System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        }

        exceptionHandler = parameter -> {
            try {
                SingleStepConfig theConfig = (SingleStepConfig)parameter;
                Exception exception = theConfig.getUserParam("exception");
                String errorMessage = exception.getMessage();
                new Error(new Timestamp(System.currentTimeMillis()),errorMessage,null).insert();
            }catch (Exception e){

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

        chapterContentPagingStep = (_responseEntity, _config) ->{
            try {
                String content = new String(_responseEntity.getContent(),"utf-8");
                Document document = Jsoup.parse(content);
                Elements pagesUrl = document.selectFirst("#iPageHtm").select("a");
                String ID = UUID.randomUUID().toString().replace("-","");
                new Chapter(ID,_config.getUserParam("parentID")+"",_config.getUserParam("chapterName")+"",
                        _config.getRequestEntity().getRequestURL(), String.valueOf(pagesUrl.size())).insert();


// TODO 这里不应该直接使用driver池，应该放在统一的httputils里面使用
//                webDriverDispatchPool.ExecuteWithFreeInstance((instance,params) ->{
//                    WebDriver webDriver = (WebDriver)instance;
//                    webDriver.get(_config.getRequestEntity().getRequestURL());
//                    int pageIndex = 1;
//                    for(Element pageUrl : pagesUrl){
//                        String script = pageUrl.attr("onclick");
//                        script = script.replace(";return false;","");
//                        ((ChromeDriver)webDriver).executeScript(script);
//                        Document _document = Jsoup.parse(webDriver.getPageSource());
//                        Element img = _document.selectFirst("#iBody").selectFirst("img");
//                        String _ID = UUID.randomUUID().toString().replace("-","");
//                        try {
//                            new Chapter(_ID,ID,"第" + String.valueOf(pageIndex) + "页",img.attr("src"),"").insert();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        pageIndex++;
//                    }
//                    return null;
//                });
            }catch (Exception e){
                _config.setUserParam("exception",e);
                exceptionHandler.Excute(_config);
            }
        };

        chapterListPagingStep = (_responseEntity, _config) ->{
            try {
                String content = new String(_responseEntity.getContent(),"utf-8");
                Document document = Jsoup.parse(content);
                Element chapterDiv = document.selectFirst(".cVolList");
                Elements chapterDivChildren = chapterDiv.children();
                String chapterType = "unkown";
                String chapterID = "";
                for(Element one : chapterDivChildren){
                    if("div".equals(one.tagName())){
                        chapterType = one.html();
                        chapterID = UUID.randomUUID().toString().replace("-","");
                        new Chapter(chapterID,"",chapterType,_config.getRequestEntity().getRequestURL(),"").insert();
                        continue;
                    }
                    if(!"周刊杂志每周每月连载单集".equals(chapterType)){
                        continue;
                    }
                    Elements as = one.select("a");
                    for(Element a : as){
                        String url = _responseEntity.getProtocol() + "://" + _responseEntity.getDomain() + a.attr("href");
                        SingleStepConfig chapterContentConfig = new SingleStepConfig();
                        chapterContentConfig.setRequestEntity(new RequestEntity());
                        chapterContentConfig.getRequestEntity().setRequestMethod(Const.requestMethod.GET);
                        chapterContentConfig.getRequestEntity().setRequestURL(url);
                        chapterContentConfig.setSingleStep(chapterContentPagingStep);
                        chapterContentConfig.setUserParam("chapterName",a.html());
                        chapterContentConfig.setUserParam("parentID",chapterID);
                        chapterContentConfig.setBeforeCrawlingHandler(sleepBeforeCrawling);
                        chapterContentConfig.setExceptionHandler(exceptionHandler);
                        CrawlerProxy.Crawling(chapterContentConfig);
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
