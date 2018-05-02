package action;

import Crawlerfj.Common.StringUtil;
import Crawlerfj.Config.DefaultConfig.DefaultConfigEntity;
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



            /*这个是爬小说的*/
//            DefaultConfigEntity.TaskEntity taskEntity;
//            configEntity.setUrl("https://www.readnovel.com/chapter/8031300604386003/24857884854778500");
//            configEntity.setMethod(DefaultRequest.Method.GET);
//            getHtmlElementTaskBuilder.setElementHandleAction(Crawlerfj.Common.Const.elementHandleAction.getTag);
//            getHtmlElementTaskBuilder.setSelector(".read-content.j_readContent");
//            configEntity.addTask(getHtmlElementTaskBuilder.CreateTaskEntity());
//
//            doRedirectTaskBuilder.setSelector(".page a");
//            doRedirectTaskBuilder.AddTask(getHtmlElementTaskBuilder.CreateTaskEntity());
//            configEntity.addTask(doRedirectTaskBuilder.CreateTaskEntity());



            /*尝试爬新华网*/
            configEntity.setUrl("http://www.xinhuanet.com/world/2018-02/02/c_1122361569.htm");
            configEntity.setMethod(DefaultRequest.Method.GET);
            getHtmlElementTaskBuilder.setElementHandleAction(Crawlerfj.Common.Const.elementHandleAction.getTag);
            getHtmlElementTaskBuilder.setSelector(".article");
            configEntity.addTask(getHtmlElementTaskBuilder.CreateTaskEntity());

            doRedirectTaskBuilder.setSelector(".nextpage");
            doRedirectTaskBuilder.setMethod(DefaultRequest.Method.GET);
            doRedirectTaskBuilder.AddTask(getHtmlElementTaskBuilder.CreateTaskEntity());
            doRedirectTaskBuilder.AddTask(doRedirectTaskBuilder.CreateTaskEntity());
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
