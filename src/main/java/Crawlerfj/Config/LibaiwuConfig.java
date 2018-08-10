package Crawlerfj.Config;

import Crawlerfj.Crawler.Impl.SingleStepCrawler.SingleStep;
import Crawlerfj.Crawler.Impl.SingleStepCrawler.SingleStepConfig;
import Crawlerfj.Proxy.DefaultCrawlerProxy;
import Util.HttpUtil.RequestEntity;
import Util.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class LibaiwuConfig {
    public static void ExecuteByConfig() throws Exception {
        DefaultCrawlerProxy proxy = DefaultCrawlerProxy.GetInstance();
        SingleStepConfig config = new SingleStepConfig();
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setRequestURL("https://www.libaiwu.com/12115.htm");
        requestEntity.setRequestMethod(Crawlerfj.Common.Const.requestMethod.GET);
        SingleStep step = (_responseEntity,_singleStepConfig) ->{
            try {
                String content = new String(_responseEntity.getContent(),"utf-8");
                Document document = Jsoup.parse(content);
                String headerStr = document.selectFirst(".entry-tit").selectFirst("h1").html();
                Elements ps = document.selectFirst(".entry-text").select("p");
                List<String> pList = new LinkedList<>();
                pList.add(headerStr);
                for(Element e : ps){
                    String innserHTMLStr = e.html();
                    if(!innserHTMLStr.startsWith("<"))
                        pList.add(e.html());
                }
                FileUtil.GetInstance().WriteLines(pList.toArray(new String[0]),"C:\\Users\\Administrator\\Desktop\\hehehe.txt");
                //这个网站反爬虫，但是每次请求之间只要等待个几秒就行了
                Thread.sleep(2000);
                String nextPageUrl = document.selectFirst("[rel=next]").attr("href");
                RequestEntity subRequestEntity = new RequestEntity();
                subRequestEntity.setRequestURL(nextPageUrl);
                subRequestEntity.setRequestMethod(Crawlerfj.Common.Const.requestMethod.GET);
                SingleStepConfig subConfig = config;
                subConfig.setRequestEntity(subRequestEntity);
                proxy.Crawling(subConfig);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        config.setRequestEntity(requestEntity);
        config.setSingleStep(step);
        proxy.Crawling(config);
    }
}
