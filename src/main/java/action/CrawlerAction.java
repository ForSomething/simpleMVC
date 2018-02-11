package action;

import Crawlerfj.Common.StringUtil;
import Crawlerfj.Config.DefaultConfigEntity;
import Crawlerfj.Proxy.DefaultCrawlerProxy;
import Crawlerfj.Request.DefaultRequest;
import Util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.CrawlerService;

import java.io.IOException;

@Controller
@RequestMapping(path = "/crawler")
public class CrawlerAction {
    @Autowired
    CrawlerService crawlerService;

    @RequestMapping(path = "/test")
    @ResponseBody
    public String Test(){
        String errStr = StringUtil.emptyString;
        DefaultCrawlerProxy proxy = DefaultCrawlerProxy.GetInstance();
        try {
            DefaultConfigEntity configEntity = new DefaultConfigEntity();
            configEntity.setUrl("http://a.jd.com/indexAjax/getCouponListByCatalogId.html?callback=jQuery4112838&catalogId=0&page=1&pageSize=9&_=1518365576811");
            configEntity.setMethod(DefaultRequest.Method.GET);
            DefaultConfigEntity.ElementEntity elementEntity = configEntity.new ElementEntity();
            elementEntity.setHandleAction(Crawlerfj.Common.Const.elementHandleAction.getJson);
//            elementEntity.setDownloadFolderPath("C:\\Users\\Administrator\\Desktop\\img");
//            elementEntity.nextLevelSelector(".img")
//                    .nextLevelSelector("p")
//                    .nextLevelSelector("img");
            configEntity.addElementEntity(elementEntity);
            proxy.Crawling(configEntity);
        } catch (Exception e) {
            errStr = e.getMessage();
        }
        if(!StringUtil.IsNullOrWihtespace(errStr)){
            return errStr;
        }
        return "start crawling!";
    }

    @RequestMapping(path = "/getImg")
    @ResponseBody
    public String getImg(String path){
        try {
            return crawlerService.doCrawler("https://item.jd.com/2953318.html");
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping(path = "/getPrice")
    @ResponseBody
    public String getPrice(String keyWord){
        try {
            return crawlerService.getSearchListInfo(Const.jdSearchURLStr.replace("%%kw%%",keyWord));
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
