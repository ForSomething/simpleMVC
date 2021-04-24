package sid.intf;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sid.bo.common.MonitorLog;
import sid.crawler.crawlers.BisiCrawler;
import sid.crawler.crawlers.NETangCrawler;
import sid.utils.CommonStringUtils;
import sid.utils.communication.network.http.HttpWorker;
import sid.utils.communication.network.http.HttpWorkerFactory;
import sid.utils.communication.network.http.Request;
import sid.utils.db.DBUtils;
import sid.utils.timer.CommonScheduler;
import sun.awt.HToolkit;
import sun.awt.windows.WToolkit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.*;
import java.util.List;

@Controller
@RequestMapping(path = "/sid/fetch")
public class Fetch {

    @RequestMapping(path = "/crawler")
    @ResponseBody
    public Object doLogin(@RequestBody Map paraMap) throws Exception{
//        String lockStr;
//        if((lockStr = RedisUtils.get("bisiCrawlerLock")) != null && "lock".equals(lockStr)){
//            paraMap.put("result", "already started");
//            return paraMap;
//        }
//        if(!RedisUtils.set("bisiCrawlerLock","lock")){//两小时有效期
//            paraMap.put("result", "get lock fail");
//            return paraMap;
//        }
        String tsdt = paraMap.get("tsdt").toString();
        Thread thread = new Thread(()->{
            new NETangCrawler().start(new HashMap<String,Object>(){{
                put("tsdt",tsdt);
            }});
//            RedisUtils.set("bisiCrawlerLock","unlock");
        });
        thread.start();
        paraMap.clear();
        paraMap.put("result", "success");
        return paraMap;
    }

    @RequestMapping(path = "/video")
    @ResponseBody
    @CrossOrigin
    public Object fetchVideo(@RequestBody Map paraMap) throws Exception{
        String id;
        String path;
        if(!CommonStringUtils.isEmptyOrWihtespace(path = CommonStringUtils.toString(paraMap.get("path")))){
            path = StringUtils.substringAfter(path,"id=");
            id = StringUtils.substringBefore(path,"(");
        }else{
            id = CommonStringUtils.toString(paraMap.get("id"));
        }
        if (CommonStringUtils.isEmptyOrWihtespace(id)) {
            throw new Exception("id不能为空");
        }
        String tsdt = CommonStringUtils.toString(paraMap.get("tsdt"));
        if(CommonStringUtils.isEmptyOrWihtespace(tsdt)){
            List<Map<String, Object>> result = DBUtils.executeQuerySql("select max(tsdt) maxTsdt from video_list ",null);
            if(result.size() == 0){
                throw new Exception("表是空的");
            }
            tsdt = result.get(0).get("maxTsdt").toString();
        }
        List<Map<String, Object>> result = DBUtils.executeQuerySql(String.format("select * from video_list where id = '%s' and tsdt = '%s'",id,tsdt),null);
        if(result.size() > 0){
            Map<String, Object> itemMap = result.get(0);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable trans = new StringSelection(itemMap.get("LINK").toString());
            clipboard.setContents(trans, null);
            //睡500毫秒，等到迅雷的下载弹窗弹出来
            Thread.sleep(500);
            trans = new StringSelection(itemMap.get("NAME").toString());
            clipboard.setContents(trans, null);

        }else{
            throw new Exception("找不到记录");
        }
        Map resultMap = new HashMap(paraMap);
        paraMap.clear();
        paraMap.put("result", resultMap);
        return paraMap;
    }

    @RequestMapping(path = "/videoList")
    @ResponseBody
    @CrossOrigin
    public Object getVideoList(@RequestBody Map paraMap) throws Exception{
        File rootFolder = new File("E:\\Temp\\netang\\image");
        List<String> vedioPathList = new LinkedList<>();
        for (File video : rootFolder.listFiles()) {
            vedioPathList.add(video.getPath());
        }
        paraMap.clear();
        paraMap.put("result", vedioPathList);
        return paraMap;
    }

    @RequestMapping(path = "/startMonitor")
    @ResponseBody
    public Object callMonitor(@RequestBody Map paraMap) throws Exception{
        paraMap.clear();
        Thread thread = new Thread(()->{
            HttpWorker httpWorker = HttpWorkerFactory.getWorker("CustomWorker");
            Request request = new Request();
            request.setRequestURL("https://www.baidu.com");
            request.setTimeoutSeconds(5);
            while (true){
                try {
                    httpWorker.request(request);
                    new MonitorLog(new Date(System.currentTimeMillis()),"1","").insert();
                } catch (Exception e) {
                    try {
                        new MonitorLog(new Date(System.currentTimeMillis()),"0",e.toString()).insert();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        paraMap.clear();
        paraMap.put("result", "success");
        return paraMap;
    }

    Map<String,Object> testMap = new HashMap<>();
    @RequestMapping(path = "/test")
    @ResponseBody
    public Object doTest(@RequestBody Map paraMap) throws Exception{
//        Map<String,Object> result = new HashMap<>();
//        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
//        String script = FileUtils.readFileToString(new File("C:\\Users\\Administrator\\Desktop\\test.js"));
//        CommonScheduler.addTask(null,e->{});
//        return new HashMap<String,Object>(){{
//            put("result1",engine.eval(script,new SimpleBindings(){{
//                put("testObj","5555");
//            }}));
//        }};
        return invokeTest(Fetch::staticMethodTest, CommonStringUtils.toString(paraMap.get("testParam")));
    }

    @RequestMapping(path = "/getIndex")
    public void getIndex(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.getOutputStream().write(FileUtils.readFileToString(new File("E:\\workspace\\commonPage\\index.html")).getBytes("utf-8"));
        response.flushBuffer();
    }

    @RequestMapping(path = "/domainTest")
    public void domainTest(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.sendRedirect("https://www.shenglifubang.cn/page/get/1936/51");
//        response.getOutputStream().write(FileUtils.readFileToString(new File("E:\\workspace\\commonPage\\index.html")).getBytes("utf-8"));
//        response.flushBuffer();
    }

    @RequestMapping(path = "/getBookmark")
    @ResponseBody
    public Object getBookmark() throws Exception{
        return DBUtils.executeQuerySql("select * from bookmark order by create_time desc",null);
    }

    @RequestMapping(path = "/updateBookmark")
    @ResponseBody
    public Object updateBookmark(@RequestBody Map paraMap) throws Exception{
        if(StringUtils.isEmpty(CommonStringUtils.toString(paraMap.get("url")))){
            return "fail of null url";
        }
        String sql = String.format("update bookmark set url = '%s',create_time = now() where descr = '%s'",paraMap.get("url"),paraMap.get("descr"));
        return "0000";
    }

    private Map invokeTest(CommonScheduler.TaskHandler taskHandler,String taskParam) throws NoSuchMethodException {
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("className",taskHandler.getClass().getCanonicalName());
        Method method = taskHandler.getClass().getMethod("execute", CommonScheduler.Task.class);
        String modifier = Modifier.toString(method.getModifiers());
        resultMap.put("modifier",modifier);
        resultMap.put("isStatic",Modifier.isStatic(method.getModifiers()));
        return resultMap;
    }

    public static void staticMethodTest(CommonScheduler.Task task){

    }
}
