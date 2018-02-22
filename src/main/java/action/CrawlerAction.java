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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        Map<String,String> requestHeader = new HashMap<String, String>();
        try {
            DefaultConfigEntity configEntity = new DefaultConfigEntity();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String dateTimeStr = String.valueOf(calendar.getTimeInMillis());
            configEntity.setUrl("http://a.jd.com/indexAjax/getCouponListByCatalogId.html?callback=jQuery3219893&catalogId=19&page=1&pageSize=50&_=" + dateTimeStr);
            configEntity.setMethod(DefaultRequest.Method.GET);
            configEntity.setHandleAction(Crawlerfj.Common.Const.contentHandleAction.handleAsJSON);
            configEntity.setRequestHeader(requestHeader);
            requestHeader.put("Referer","https://a.jd.com/");
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
