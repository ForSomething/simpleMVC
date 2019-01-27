package crawlerfj.crawlercase;

import crawlerfj.common.Const;
import crawlerfj.crawler.Impl.SingleStepCrawler.SingleStepConfig;
import crawlerfj.proxy.DefaultCrawlerProxy;
import toolroom.httputil.RequestEntity;

import java.io.UnsupportedEncodingException;

public class BisiImageSetConfig {
    public static void ExecuteByConfig() throws Exception {
        SingleStepConfig startipConfig = new SingleStepConfig();
        startipConfig.setRequestEntity(new RequestEntity());
        startipConfig.getRequestEntity().setBrowserConfig(new RequestEntity.BrowserConfig());
        startipConfig.getRequestEntity().setRequestURL("http://www.huhumh.com/hu295530/1.html?s=1");
        startipConfig.getRequestEntity().setRequestMethod(Const.requestMethod.GET);
        startipConfig.setSingleStep((_responseEntity,_config) -> {
            try {
                String content = new String(_responseEntity.getContent(),"utf-8");
                String aaa = "";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        DefaultCrawlerProxy.GetInstance().Crawling(startipConfig);
    }
}
