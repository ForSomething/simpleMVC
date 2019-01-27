package crawlerfj.crawlercase;

import crawlerfj.crawler.Impl.SingleStepCrawler.SingleStep;
import crawlerfj.crawler.Impl.SingleStepCrawler.SingleStepConfig;
import crawlerfj.proxy.DefaultCrawlerProxy;
import toolroom.httputil.RequestEntity;

import java.io.UnsupportedEncodingException;

public class NemmwConfig {
    public static void ExecuteByConfig() throws Exception {
        SingleStepConfig config = new SingleStepConfig();
        RequestEntity requestEntity = new RequestEntity();
        //requestEntity.setRequestURL("http://www.nenmw.com/3/5941.html?page=1");//这个不是异步加载的
        //requestEntity.setRequestURL("http://www.meituri.com/a/22669/");//这个不是异步加载的
        requestEntity.setRequestURL("http://www.mt366.com/mistar/2101.html");
        requestEntity.setRequestMethod(crawlerfj.common.Const.requestMethod.GET);
        RequestEntity.BrowserConfig browserConfig = new RequestEntity.BrowserConfig();
        requestEntity.setBrowserConfig(browserConfig);
        browserConfig.setWaitForJSRenderingTime(30000);
        SingleStep step = (_responseEntity, _singleStepConfig) ->{
            try {
                String content = new String(_responseEntity.getContent(),"utf-8");
                String aaa = "";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        config.setRequestEntity(requestEntity);
        config.setSingleStep(step);
        DefaultCrawlerProxy.GetInstance().Crawling(config);
    }
}
