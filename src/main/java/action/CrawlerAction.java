package action;

import crawlerfj.crawlercase.dataentity.Chapter;
import crawlerfj.crawlercase.dataentity.CrawlerLog;
import crawlerfj.crawlercase.huhumanhua.HuhumhConfig;
import toolroom.FileUtils;
import toolroom.JsonUtils;
import toolroom.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.CrawlerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
            HuhumhConfig.ExecuteByConfig();
//            Chapter.load("396590a910d04771b1a29e49a8591c25");
        } catch (Exception e){
            errStr = e.getMessage();
        }
        if(!StringUtils.IsNullOrWihtespace(errStr)){
            return errStr;
        }
        return "start crawling!";
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
            String[] lines = FileUtils.ReadLinesFromFile(currentPath);
            StringBuilder returnStringBuilder = new StringBuilder();
            for(String line : lines){
                returnStringBuilder.append(String.format("<tr><td><img src=\"%s\"></td></tr>",line));
            }
            return returnStringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
