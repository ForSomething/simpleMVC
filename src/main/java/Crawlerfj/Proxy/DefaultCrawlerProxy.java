package Crawlerfj.Proxy;


import Crawlerfj.Crawler.ICrawlerfj;
import Crawlerfj.Crawler.Impl.SingleStepCrawler.SingleStepCrawler;

import java.util.ArrayList;
import java.util.List;

public class DefaultCrawlerProxy {
    // 这里分成两个列表是为了避免以下情况
    // 用户自定义的爬取器也是用的默认配置类，这时候只用一个列表的话，默认爬取器遇到默认配置类时，CanHandle就会返回true，那么永远都不会调用到用户自定义的爬取器
    List<ICrawlerfj> customCrawlerList; //用户自定义的爬取器列表
    List<ICrawlerfj> defaultCrawlerList; //我自己的爬取器列表
    //私有化构造函数，防止外部类利用new关键字实例化此对象
    private DefaultCrawlerProxy(){
        defaultCrawlerList = new ArrayList<>();
        defaultCrawlerList.add(SingleStepCrawler.GetInstance());
        customCrawlerList = new ArrayList<>();
    }

    private static DefaultCrawlerProxy instance = new DefaultCrawlerProxy();

    public static DefaultCrawlerProxy GetInstance(){
        return instance;
    }

    public void RegisterCrawler(ICrawlerfj crawler){
        customCrawlerList.add(crawler);
    }

    public void Crawling(Object config) throws Exception{
        ICrawlerfj crawler = null;
        if(config == null){
            throw new Exception("configEntity is null");
        }
        //我们先在用户自定义的列表中传递配置对象，如果自定义列表没有找到可以处理的爬取器，则在默认列表继续传递配置对象
        for(ICrawlerfj one : customCrawlerList){
            if(one.CanHandle(config)){
                crawler = one;
                break;
            }
        }

        if (crawler == null){
            for(ICrawlerfj one : defaultCrawlerList){
                if(one.CanHandle(config)){
                    crawler = one;
                    break;
                }
            }
        }

        if(crawler != null){
            crawler.Crawling(config);
        }else{
            throw new Exception("no one can handle this: " + config.getClass().getCanonicalName());
        }
    }
}
