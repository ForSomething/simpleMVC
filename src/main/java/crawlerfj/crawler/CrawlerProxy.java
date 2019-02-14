package crawlerfj.crawler;

import java.util.LinkedList;
import java.util.List;

public class CrawlerProxy {
    private static List<ICrawlerfj> crawlerList = new LinkedList<>();

    public static void Crawling(Object config) throws Exception{
        ICrawlerfj crawler = null;
        if(config == null){
            throw new Exception("configEntity is null");
        }
        for(ICrawlerfj one : crawlerList){
            if(one.CanHandle(config)){
                crawler = one;
                break;
            }
        }

        if(crawler != null){
            crawler.Crawling(config);
        }else{
            throw new Exception("no one can handle this: " + config.getClass().getCanonicalName());
        }
    }

    public static void registerCrawler(ICrawlerfj crawler){
        crawlerList.add(crawler);
    }
}
