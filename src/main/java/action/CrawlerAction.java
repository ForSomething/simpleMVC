package action;

import Crawlerfj.Common.StringUtil;
import Crawlerfj.Config.DefaultConfig.DefaultConfigEntity;
import Crawlerfj.Config.DefaultConfig.TaskBuilder;
import Crawlerfj.Exception.ConfigIllegalException;
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
        TaskBuilder.GetContentTaskBuilder getContentTaskBuilder = TaskBuilder.CreateGetContentTaskBuilder();
        TaskBuilder.GetHtmlElementTaskBuilder getHtmlElementTaskBuilder = TaskBuilder.CreateGetHtmlElementTaskBuilder();
        TaskBuilder.DoRedirectTaskBuilder doRedirectTaskBuilder = TaskBuilder.CreateDoRedirectTaskBuilder();
        DefaultConfigEntity configEntity = new DefaultConfigEntity();
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



            /*这个是爬图片的*/
            DefaultConfigEntity.TaskEntity taskEntity;
            configEntity.setUrl("http://www.xgyw.cc/YouMi/8202.html");
//            htmlCrawlerConfigBuilder.setUrl("http://www.comicool.cn/content/reader.html?comic_id=12485&ep_id=19&update_weekday=0");
            configEntity.setMethod(DefaultRequest.Method.GET);
            getHtmlElementTaskBuilder.setDownloadFolderPath("C:\\Users\\Administrator\\Desktop\\img");
            getHtmlElementTaskBuilder.setElementHandleAction(Crawlerfj.Common.Const.elementHandleAction.download);
            getHtmlElementTaskBuilder.setSelector("p img");
            configEntity.addTask(getHtmlElementTaskBuilder.CreateTaskEntity());

            doRedirectTaskBuilder.setSelector(".page a");
            doRedirectTaskBuilder.AddTask(getHtmlElementTaskBuilder.CreateTaskEntity());
            configEntity.addTask(doRedirectTaskBuilder.CreateTaskEntity());




            proxy.Crawling(configEntity);
        } catch (ConfigIllegalException e) {
            errStr = e.getMessage();
        }catch (Exception e){
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
