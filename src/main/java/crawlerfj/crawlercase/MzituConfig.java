package crawlerfj.crawlercase;

import common.eventhandlerinterface.BaseEventHandler;
import crawlerfj.common.Const;
import crawlerfj.crawler.Impl.SingleStepCrawler.SingleStep;
import crawlerfj.crawler.Impl.SingleStepCrawler.SingleStepConfig;
import crawlerfj.proxy.DefaultCrawlerProxy;
import toolroom.FileUtils;
import toolroom.httputil.RequestEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class MzituConfig {
    static BaseEventHandler exceptionHandler;

    static SingleStep girlSetPagingStep;

    static SingleStep oneGirlImageSetListPagingStep;

    static SingleStep oneImageSetPagingStep;

    static SingleStep downloadImageStep;

    static ReentrantLock lock = new ReentrantLock();

    static BaseEventHandler sleepBeforeCrawling;

    private static String logFileName = "E:\\mzitu_test\\log.txt";

    private static String errLogFileName = "E:\\mzitu_test\\error.txt";

    public static void ExecuteByConfig() throws Exception {
        init();
        SingleStepConfig startUpConfig = new SingleStepConfig();
        startUpConfig.setRequestEntity(new RequestEntity());
//        startUpConfig.getRequestEntity().setBrowserConfig(new RequestEntity.BrowserConfig());
        startUpConfig.getRequestEntity().setRequestURL("https://www.mzitu.com/zhuanti/");
        startUpConfig.getRequestEntity().setRequestMethod(Const.requestMethod.GET);
        startUpConfig.setSingleStep(girlSetPagingStep);
        DefaultCrawlerProxy.GetInstance().Crawling(startUpConfig);
    }

    private static void init(){
        exceptionHandler = parameter -> {
            SingleStepConfig theConfig = (SingleStepConfig)parameter;
            List<String> errLogs = new LinkedList<>();
            errLogs.add(String.format("[%s]爬取出现异常---------------------------------------------",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())));
            errLogs.add(String.format("URL：%s",theConfig.getRequestEntity().getRequestURL()));
            errLogs.add(String.format("meesage：%s",theConfig.getUserParam("errorMessage")));
            errLogs.add(String.format(" "));
            lock.lock();
//            FileUtils.GetInstance().WriteLines(errLogs.toArray(new String[1]),errLogFileName,true);
            lock.unlock();
        };

        sleepBeforeCrawling = (parameter) -> {
            try {
                //防止反爬虫，在爬取之前先睡200毫秒
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        downloadImageStep = (_responseEntity,_config) ->{
            try{
                String saveFolder = "E:\\mzitu_test\\" + _config.getUserParam("girlName") + "//";
                String title = _config.getUserParam("title").toString();
                for(int charIndex = 0;charIndex < title.length();charIndex++){
                    char theChar = title.charAt(charIndex);
                    if(theChar > 126 || (theChar > 47 && theChar < 58) || (theChar > 64 && theChar < 91) || (theChar > 96 && theChar < 123) || theChar == '-'){
                        continue;
                    }
                    title = title.replace(String.valueOf(theChar),"-");
                }
                String saveFileName = title + "--" +_config.getUserParam("pageIndex").toString() + ".jpg";
                FileUtils.GetInstance().WriteBinaryData(_responseEntity.getContent(),saveFolder + saveFileName,false);
            }catch (Exception e){
                _config.setUserParam("errorMessage",e.getMessage());
                exceptionHandler.Excute(_config);
            }
        };

        oneImageSetPagingStep = (_responseEntity, _config) ->{
            try{
                String logString;
                String content = new String(_responseEntity.getContent(),"utf-8");
                Document document = Jsoup.parse(content);
                String imageUrl = document.selectFirst(".main-image").selectFirst("img").attr("src");
                Integer oneGirlPageIndex = _config.getUserParam("pageIndex");
                oneGirlPageIndex = oneGirlPageIndex == null ? 1 : oneGirlPageIndex;
                if(oneGirlPageIndex == 1){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.FFF");
                    logString = String.format("[%s] [%s]开始下载",sdf.format(System.currentTimeMillis()),_config.getUserParam("title").toString());
                    lock.lock();
                    FileUtils.GetInstance().WriteLine(logString,logFileName,true);
                    lock.unlock();
                }
                String nextPageUrl = null;
                Elements as = document.selectFirst(".pagenavi").select("a");
                for(Element a : as){
                    if(a.childNodeSize() > 0 && "下一页»".equals(a.child(0).html())){
                        nextPageUrl = a.attr("href");
                    }
                }
                //下载图片
                SingleStepConfig downloadImageConfig = new SingleStepConfig();
                downloadImageConfig.setRequestEntity(new RequestEntity());
                downloadImageConfig.getRequestEntity().setRequestURL(imageUrl);
                downloadImageConfig.getRequestEntity().setRequestMethod(Const.requestMethod.GET);
                downloadImageConfig.getRequestEntity().setRequestHeader("Referer",_config.getRequestEntity().getRequestURL());
                downloadImageConfig.setUserParam("title",_config.getUserParam("title"));
                downloadImageConfig.setUserParam("girlName",_config.getUserParam("girlName"));
                downloadImageConfig.setUserParam("pageIndex",oneGirlPageIndex);
                downloadImageConfig.setSingleStep(downloadImageStep);
                downloadImageConfig.setBeforeCrawlingHandler(sleepBeforeCrawling);
                DefaultCrawlerProxy.GetInstance().Crawling(downloadImageConfig);

                if(nextPageUrl == null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.FFF");
                    logString = String.format("[%s] [%s]下载完成",sdf.format(System.currentTimeMillis()),_config.getUserParam("title").toString());
                    lock.lock();
                    FileUtils.GetInstance().WriteLine(logString,logFileName,true);
                    lock.unlock();
                    return;
                }
                _config.getRequestEntity().setRequestURL(nextPageUrl);
                _config.setUserParam("pageIndex",++oneGirlPageIndex);
                DefaultCrawlerProxy.GetInstance().Crawling(_config);
            }catch (Exception e){
                _config.setUserParam("errorMessage",e.getMessage());
                exceptionHandler.Excute(_config);
            }
        };

        oneGirlImageSetListPagingStep = (_responseEntity, _config) ->{
            try {
                String content = new String(_responseEntity.getContent(),"utf-8");
                Document document = Jsoup.parse(content);
                Elements lis = document.selectFirst("#pins").select("li");
                String theGirlLink;
                String theGirlTitle;
                for(Element li : lis){
                    theGirlLink = li.selectFirst("a").attr("href");
                    theGirlTitle = li.selectFirst("a").selectFirst("img").attr("alt");
                    SingleStepConfig oneGirlImageSetListConfig = new SingleStepConfig();
                    oneGirlImageSetListConfig.setRequestEntity(new RequestEntity());
//                    oneGirlImageSetListConfig.getRequestEntity().setBrowserConfig(new RequestEntity.BrowserConfig());
                    oneGirlImageSetListConfig.getRequestEntity().setRequestURL(theGirlLink);
                    oneGirlImageSetListConfig.getRequestEntity().setRequestMethod(Const.requestMethod.GET);
                    oneGirlImageSetListConfig.setUserParam("title",theGirlTitle);
                    oneGirlImageSetListConfig.setUserParam("girlName",_config.getUserParam("girlName"));
                    oneGirlImageSetListConfig.setSingleStep(oneImageSetPagingStep);
                    oneGirlImageSetListConfig.setBeforeCrawlingHandler(sleepBeforeCrawling);
                    DefaultCrawlerProxy.GetInstance().Crawling(oneGirlImageSetListConfig);
                }
                Integer pageIndex = _config.getUserParam("pageIndex");
                pageIndex = pageIndex == null ? 1 : pageIndex;
                Element nextPageBtn = document.selectFirst(".next");
                if(nextPageBtn == null){
                    return;
                }
                String nextPageLink = nextPageBtn.attr("href");
                _config.getRequestEntity().setRequestURL(nextPageLink);
                _config.setUserParam("pageIndex",++pageIndex);
                DefaultCrawlerProxy.GetInstance().Crawling(_config);
            }catch (Exception e){
                _config.setUserParam("errorMessage",e.getMessage());
                exceptionHandler.Excute(_config);
            }
        };

        girlSetPagingStep = (_responseEntity, _config) ->{
            try {
                String content = new String(_responseEntity.getContent(),"utf-8");
                Document document = Jsoup.parse(content);
                Element dl = document.selectFirst(".tags");
                Elements dlChildren = dl.children();
                boolean shouldCrawling = false;
                for(Element one : dlChildren){
                    if("名站写真".equals(one.html())){
                        break;
                    }
                    if(!shouldCrawling && "妹子".equals(one.html())){
                        shouldCrawling = true;
                        continue;
                    }
                    if(shouldCrawling){
                        String girlImageSetPagingUrl = one.selectFirst("a").attr("href");
                        String girlName = one.selectFirst("img").attr("alt");
                        SingleStepConfig oneGirlConfig = new SingleStepConfig();
                        oneGirlConfig.setRequestEntity(new RequestEntity());
//                        oneGirlConfig.getRequestEntity().setBrowserConfig(new RequestEntity.BrowserConfig());
                        oneGirlConfig.getRequestEntity().setRequestMethod(Const.requestMethod.GET);
                        oneGirlConfig.getRequestEntity().setRequestURL(girlImageSetPagingUrl);
                        oneGirlConfig.setSingleStep(oneGirlImageSetListPagingStep);
                        oneGirlConfig.setUserParam("girlName",girlName);
                        oneGirlConfig.setBeforeCrawlingHandler(sleepBeforeCrawling);
                        DefaultCrawlerProxy.GetInstance().Crawling(oneGirlConfig);
                    }
                }
            }catch (Exception e){
                _config.setUserParam("errorMessage",e.getMessage());
                exceptionHandler.Excute(_config);
            }
        };
    }
}
