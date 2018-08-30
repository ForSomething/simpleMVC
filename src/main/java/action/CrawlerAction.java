package action;

import Crawlerfj.Config.*;
import Util.Const;
import Util.StringUtils;
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
        String errStr = StringUtils.emptyString;
        try {

            /*这个是爬京东优惠券列表的*/
//            requestHeader.put("Referer","https://a.jd.com/");
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(new Date());
//            String dateTimeStr = String.valueOf(calendar.getTimeInMillis());
//            //这个url中有个callback参数，京东会根据这个参数拼接返回的JSON，将这个参数置空方便下面对JSON的处理
//            configEntity.setUrl("http://a.jd.com/indexAjax/getCouponListByCatalogId.html?callback=&catalogId=19&page=1&pageSize=50&_=" + dateTimeStr);
//            configEntity.setRequestHeader(requestHeader);
//            configEntity.setMethod(DefaultRequest.Method.GET);
//            configEntity.setContentFormatter(value -> {
//                value = value.replaceAll("^\\(",""); //去掉字符串首的左括号
//                value = value.replaceAll("\\)$",""); //去掉字符串尾的右括号
//                return value;
//            });
            NenmwConfig.ExecuteByConfig();
        } catch (Exception e){
            errStr = e.getMessage();
        }
        if(!StringUtils.IsNullOrWihtespace(errStr)){
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
