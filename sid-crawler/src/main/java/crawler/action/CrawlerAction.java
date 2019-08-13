package crawler.action;

import crawler.crawlers.JuejinCrawler;
import crawler.dao.bo.Chapter;
import org.springframework.web.bind.annotation.PathVariable;
import utils.FileUtils;
import utils.JsonUtils;
import utils.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping(path = "/crawler")
public class CrawlerAction {
    @RequestMapping(path = "/")
    @ResponseBody
    public String index(){
        return "";
    }

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
//            Chapter.load("396590a910d04771b1a29e49a8591c25");
            String sdfsdf = "";
        } catch (Exception e){
            e.printStackTrace();
            errStr = e.toString();
        }
        if(!StringUtils.isNullOrWihtespace(errStr)){
            return errStr;
        }
        return "management crawling!";
    }

    @RequestMapping(path = "/getChapter")
    @ResponseBody
    public void getChapter(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HashMap<String,String> cond = new HashMap<>();
        cond.put("ID",request.getParameter("ID"));
        cond.put("parentID",request.getParameter("parentID"));
        List<Object> dataList = new LinkedList();
        for(Chapter chapter : Chapter.load(cond)){
            Map<String,Object> data = new HashMap<>();
            dataList.add(data);
            data.put("id",chapter.getId());
            data.put("text",chapter.getChapterName());
            data.put("value",chapter.getChapterContent());
            Map<String,Object> disMap = new HashMap<>();
            if(JsonUtils.tryParseJsonString2Map(chapter.getRemark(),disMap)){
                data.put("remark",disMap);
            }else{
                data.put("remark",chapter.getRemark());
            }
        }
        response.getWriter().write(JsonUtils.parse2Json(dataList));
    }

    @RequestMapping(path = "/getChapterContent")
    @ResponseBody
    public String getChapterContent(HttpServletRequest request){
        try {
            String currentPath = request.getParameterMap().keySet().iterator().next();
//            String[] lines = FileUtils.ReadLinesFromFile(currentPath);
            StringBuilder returnStringBuilder = new StringBuilder();
//            for(String line : lines){
//                returnStringBuilder.append(String.format("<tr><td><img src=\"%s\"></td></tr>",line));
//            }
            return returnStringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping(path = {"/{name}/*","/{name}"})
    @ResponseBody
    public String commonHandler(@PathVariable("name") String var){
        //这个方法只是个例子，介绍@PathVariable这个注解的用法，在访问的url与上面的都不匹配时，会进这个方法
        return "the name is " + var;
    }
}
