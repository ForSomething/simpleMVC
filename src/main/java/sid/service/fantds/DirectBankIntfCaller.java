package sid.service.fantds;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import sid.exception.RTCDNotSuccessException;
import sid.utils.CommonStringUtils;
import sid.utils.JsonUtils;
import sid.utils.communication.network.http.CustomWorker;
import sid.utils.communication.network.http.Request;
import sid.utils.communication.network.http.Response;
import sid.utils.db.DBUtils;
import sid.utils.executor.JavaScriptExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectBankIntfCaller {
    private static Map<String,Map> cookieMap = new HashMap<>();

    public Map<String,Object> processCall(String processFilePath,Map<String,Object> context) throws Exception{
        String javascript = FileUtils.readFileToString(new File(processFilePath),"utf-8");
        JavaScriptExecutor executor = new JavaScriptExecutor(context,javascript);
        return executor.execute(Map.class);
    }

    public static void userLogin(String bkno,String mobile,String sessionId) throws Exception {
        if(StringUtils.isEmpty(sessionId)){
            throw new RTCDNotSuccessException("sessionId不可为空");
        }
        Response response = doRequest(bkno,getRequestUrl("EITFT"),new HashMap<String,Object>(){{
            put("bkno",bkno);
            put("mobileNo",mobile);
            put("loginType","1");
        }},"");
        String responseStr = new String(response.getContent(),"utf-8");
        Map resultMap = JsonUtils.parse(responseStr,Map.class);
        String rtcd = CommonStringUtils.toString(resultMap.get("rtcd"));
        if(!"0000".equals(rtcd)){
            throw new RTCDNotSuccessException(rtcd);
        }
        cookieMap.put(sessionId,response.getCookieMap());
    }

    public static void tellerLogin(String bkno,String tsus,String sessionId) throws Exception {
        if(StringUtils.isEmpty(sessionId)){
            throw new RTCDNotSuccessException("sessionId不可为空");
        }
        Response response = doRequest(bkno,getRequestUrl("login"),new HashMap<String,Object>(){{
            put("bkno",bkno);
            put("sleep",1);
            put("tspw","Hgm0+LNv7i0=");
            put("tsus",tsus);
        }},"");
        String responseStr = new String(response.getContent(),"utf-8");
        Map resultMap = JsonUtils.parse(responseStr,Map.class);
        String rtcd = CommonStringUtils.toString(resultMap.get("rtcd"));
        if(!"0000".equals(rtcd)){
            throw new RTCDNotSuccessException(rtcd);
        }
        cookieMap.put(sessionId,response.getCookieMap());
    }

    public static Map<String,Object> doTxReq(String bkno, String tx,String sessionId,Map<String,Object> in) throws Exception {
        Map<String,Object> out= new HashMap<>();
        Response response = doRequest(bkno,getRequestUrl(tx),in,sessionId);
        String responseStr = new String(response.getContent(),"utf-8");
        Map resultMap = JsonUtils.parse(responseStr,Map.class);
        String rtcd = CommonStringUtils.toString(resultMap.get("rtcd"));
        if(!rtcd.equals("0000")){
            throw new RTCDNotSuccessException(rtcd);
        }
        out.putAll(resultMap);
        return out;
    }

    public static List<Map<String,Object>> executeSql(String sql, Map param,Boolean autoCommit) throws Exception {
        sql = sql.trim();
        String preStr = sql.substring(0,"select".length());
        if(preStr.equalsIgnoreCase("select")){
            //查询
            return DBUtils.executeQuerySql(sql,param);
        }else{
            //其他
            autoCommit = autoCommit == null ? false : autoCommit;
            DBUtils.executeBySqlTemplate(sql,param,autoCommit);
        }
        return new ArrayList<>();
    }

    private static Response doRequest(String bkno,String url,Map<String,Object> param,String sessionId) throws Exception {
        param.put("bkno",bkno);
        Request request = new Request();
        request.setRequestURL(url);
        request.setRequestMethod(Request.RequestMethod.POST);
        request.setHeader("Content-Type",Request.ContentType.APPLICATION_JSON.toString());
        for(Map.Entry<String,Object> entry : param.entrySet()){
            request.setParam(entry.getKey(),CommonStringUtils.toString(entry.getValue()));
        }
        request.setCookieMap(cookieMap.get(sessionId));
        CustomWorker customWorker = new CustomWorker();
        customWorker.request(request);
        return customWorker.getResponse();
    }

    private static String getRequestUrl(String tx){
        return "http://127.0.0.1:8080/fantds/ebank/tx/json/" + tx;
    }
}
