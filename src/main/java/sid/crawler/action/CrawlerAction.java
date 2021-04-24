package sid.crawler.action;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sid.bo.common.Chapter;
import sid.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping(path = "/sid/crawler")
public class CrawlerAction {
    @RequestMapping(path = "/")
    @ResponseBody
    public String index(){
        return "";
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
            if(JsonUtils.tryParse(chapter.getRemark(),disMap)){
                data.put("remark",disMap);
            }else{
                data.put("remark",chapter.getRemark());
            }
        }
        response.getWriter().write(JsonUtils.toJson(dataList));
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
