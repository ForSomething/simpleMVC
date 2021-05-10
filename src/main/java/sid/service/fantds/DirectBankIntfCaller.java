package sid.service.fantds;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import sid.bo.directbank.DirectbankEnvironment;
import sid.exception.RTCDNotSuccessException;
import sid.utils.CommonStringUtils;
import sid.utils.JsonUtils;
import sid.utils.communication.network.http.CustomWorker;
import sid.utils.communication.network.http.Request;
import sid.utils.communication.network.http.Response;
import sid.utils.db.DBUtils;
import sid.utils.executor.JavaScriptExecutor;
import sid.utils.miscellaneous.CommonLogger;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

public class DirectBankIntfCaller {
    private static Map<String,Map> cookieMap = new HashMap<>();

    public Map<String,Object> processCall(String processFilePath,Map<String,Object> context) throws Exception{
        String javascript = FileUtils.readFileToString(new File(processFilePath),"utf-8");
//        //解析语义化的命令集成到js代码中
//        String parsedCinstructionStr = parseSemantiCinstructions();
//        javascript = javascript.replace("#{code}",parsedCinstructionStr);
//        CommonLogger.info("要执行的脚本是：\n" + javascript);
        if(true){
//            DirectbankEnvironment ins1 = DirectbankEnvironment.load("dev3");
//            DirectbankEnvironment ins2 = DirectbankEnvironment.load("local");
            DirectbankEnvironment ins3 = new DirectbankEnvironment();
            ins3.setName("test");
            ins3.setCoreDomain("setCoreDomain");
            ins3.setDbDomain("setDbDomain");
            ins3.setDbName("setDbName");
            ins3.setDbPassword("setDbPassword");
            ins3.setDbUser("setDbUser");
            ins3.setVueUrl("setVueUrl");
            ins3.save();
            return new HashMap<String, Object>(){{put("mesg","加了挡板");}};
        }
        JavaScriptExecutor executor = new JavaScriptExecutor(context,javascript);
        return executor.execute(Map.class);
    }

    public static void userLogin(String bkno,String mobile,String sessionId) throws Exception {
        if(StringUtils.isEmpty(sessionId)){
            throw new RTCDNotSuccessException("sessionId不可为空");
        }
        Response response = doRequest(bkno,getRequestUrl("EITFT","public"),new HashMap<String,Object>(){{
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
        Response response = doRequest(bkno,getRequestUrl("login","admin"),new HashMap<String,Object>(){{
            put("bkno",bkno);
            put("sleep","1");
            put("tspw","Hgm0+LNv7i0=");
            put("tsus",tsus);
        }},"");
        String responseStr = new String(response.getContent(),"utf-8");
        Map resultMap = JsonUtils.parse(responseStr,Map.class);
        String rtcd = CommonStringUtils.toString(resultMap.get("rtcd"));
        int rtcdInt = -1;
        try{
            rtcdInt = new BigDecimal(rtcd).intValue();
        }catch (Exception e){
            CommonLogger.info("rtcd="+rtcd,e);
        }
        if(rtcdInt != 0){
            throw new RTCDNotSuccessException(rtcd);
        }
        cookieMap.put(sessionId,response.getCookieMap());
    }

    public static Map<String,Object> doAdminTxReq(String bkno, String tx,String sessionId,String paramJsonStr) throws Exception {
        Map<String,Object> out= new HashMap<>();
        Map in = JsonUtils.parse(paramJsonStr,Map.class);
        Response response = doRequest(bkno,getRequestUrl(tx,"admin"),in,sessionId);
        String responseStr = new String(response.getContent(),"utf-8");
        Map resultMap = JsonUtils.parse(responseStr,Map.class);
        String rtcd = CommonStringUtils.toString(resultMap.get("rtcd"));
        if(!rtcd.equals("0000")){
            throw new RTCDNotSuccessException(rtcd);
        }
        out.putAll(resultMap);
        return out;
    }

    public static Map<String,Object> doPublicTxReq(String bkno, String tx,String sessionId,String paramJsonStr) throws Exception {
        Map<String,Object> out= new HashMap<>();
        Map in = JsonUtils.parse(paramJsonStr,Map.class);
        Response response = doRequest(bkno,getRequestUrl(tx,"public"),in,sessionId);
        String responseStr = new String(response.getContent(),"utf-8");
        Map resultMap = JsonUtils.parse(responseStr,Map.class);
        String rtcd = CommonStringUtils.toString(resultMap.get("rtcd"));
        if(!rtcd.equals("0000")){
            throw new RTCDNotSuccessException(rtcd);
        }
        out.putAll(resultMap);
        return out;
    }

    public static Map<String,Object> doTxReq(String bkno, String tx,String sessionId,String paramJsonStr,String txType) throws Exception {
        Map<String,Object> out= new HashMap<>();
        Map in = JsonUtils.parse(paramJsonStr,Map.class);
        Response response = doRequest(bkno,getRequestUrl(tx,txType),in,sessionId);
        String responseStr = new String(response.getContent(),"utf-8");
        Map resultMap = JsonUtils.parse(responseStr,Map.class);
        String rtcd = CommonStringUtils.toString(resultMap.get("rtcd"));
        if(!rtcd.equals("0000")){
            throw new RTCDNotSuccessException(rtcd);
        }
        out.putAll(resultMap);
        return out;
    }

    public static List<Map<String,Object>> executeSql(String sql, String paramJsonStr,Boolean autoCommit,Boolean ignoreNullParam) throws Exception {
        Map param = JsonUtils.parse(paramJsonStr,Map.class);
        sql = sql.trim();
        String preStr = sql.substring(0,"select".length());
        if(preStr.equalsIgnoreCase("select")){
            //查询
            return DBUtils.executeQuerySql(sql,param,true);
        }else{
            //其他
            autoCommit = autoCommit == null ? false : autoCommit;
            ignoreNullParam = ignoreNullParam == null ? true : ignoreNullParam;
            DBUtils.executeBySqlTemplate(sql,param,autoCommit,ignoreNullParam);
        }
        return new ArrayList<>();
    }

    private static Response doRequest(String bkno,String url,Map<String,Object> param,String sessionId) throws Exception {
        param.put("bkno",bkno);
        Request request = new Request();

        request.setRequestURL(url);
        CommonLogger.info(String.format("\n请求的路径:\n%s",url));
        request.setRequestMethod(Request.RequestMethod.POST);
        request.setHeader("Content-Type",Request.ContentType.APPLICATION_JSON.toString());
        request.setParamMap(param);
        CommonLogger.info(String.format("\n请求的参数:\n%s",JsonUtils.toJson(request.getParamMap())));
        request.setCookieMap(cookieMap.get(sessionId));
        CommonLogger.info(String.format("\n请求的cookie:\n%s",JsonUtils.toJson(request.getCookieMap())));
        CustomWorker customWorker = new CustomWorker();
        customWorker.request(request);
        CommonLogger.info(String.format("\n请求的结果:\n%s",new String(customWorker.getResponse().getContent(),"utf-8")));
        return customWorker.getResponse();
    }

    private static String getRequestUrl(String tx,String txType){
        String pre = null;
        String finalPre = "http://localhost:8080/fantds/";
//        String finalPre = "http://10.38.11.55:6017/fantds/";
        if("admin".equals(txType)){
            pre = "tx/json/";
        }else{
            pre = "ebank/tx/json/";
        }
        return finalPre + pre + tx;
    }

    private static String parseSemantiCinstructions() throws ParseException {
        List<List<String>> semantiCinstructions = genSemantiCinstructions();
        if(semantiCinstructions.size() == 0){
            return null;
        }
        int userIndex = -1;
        List<String> currentCinstructionSet = null;
        Map<String,List<String>> timePointMap = new HashMap<>();
        timePointMap.put("20190101",new LinkedList<>());
        for(List<String> subCinstructions : semantiCinstructions){
            for(String cinstructionItem : subCinstructions){
                switch (cinstructionItem){
                    case "loans":
                        currentCinstructionSet = timePointMap.get("20190101");
                        currentCinstructionSet.add(String.format("userLoan(users[%s].MOBILE,10000,false,'session');",++userIndex));
                        break;
                    case "loansg":
                        currentCinstructionSet = timePointMap.get("20190101");
                        currentCinstructionSet.add(String.format("userLoan(users[%s].MOBILE,10000,true,'session');",++userIndex));
                        break;
                    case "loano":
                        currentCinstructionSet = timePointMap.get("20190101");
                        currentCinstructionSet.add(String.format("userLoan(users[%s].MOBILE,10000,false,'once');",++userIndex));
                        break;
                    case "prepay":
                        currentCinstructionSet.add(String.format("userRepay(users[%s],100);",userIndex));
                        break;
                    case "clear":
                        currentCinstructionSet.add(String.format("userClear(users[%s]);",userIndex));
                        break;
                    case "repayOverdue":
                        currentCinstructionSet.add(String.format("userRepay(users[%s],30000);",userIndex));
                        break;
                    case "freeze":
                        currentCinstructionSet.add(String.format("users[%s].seq = doPublicTxReq('A114',{processType:'freeze',acct:users[%s].ACCT,eddt:'20990101'}).seq;",userIndex,userIndex));
                        break;
                    default:
                        if(cinstructionItem.length() != 8){
                            throw new RTCDNotSuccessException(cinstructionItem + "这个不是日期");
                        }
                        DateUtils.parseDate(cinstructionItem,"yyyyMMdd");
                        if(!timePointMap.containsKey(cinstructionItem)){
                            timePointMap.put(cinstructionItem,new LinkedList<>());
                        }
                        currentCinstructionSet = timePointMap.get(cinstructionItem);
                }
            }
        }
        StringBuilder totalCinstructions = new StringBuilder();
        timePointMap.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(e->{
            if(!e.getKey().equals("20190101")){
                totalCinstructions.append(String.format("tsdtTo('%s');\r\n",e.getKey()));
            }
            for(String one : e.getValue()){
                totalCinstructions.append(one).append("\r\n");
            }
        });
        return totalCinstructions.toString();
    }

    private static List<List<String>> genSemantiCinstructions(){
        List<List<String>> cinstructions = new LinkedList<>();
        List<String> subCinstructions;
        //首先加入一个大的日终值，确保所有借据都还上了
        cinstructions.add(subCinstructions = new LinkedList<>());
        subCinstructions.add("20191002");
        //当日借当日结清
        cinstructions.add(subCinstructions = new LinkedList<>());
        subCinstructions.add("loans");
        subCinstructions.add("clear");
        //当日借本月结清
        cinstructions.add(subCinstructions = new LinkedList<>());
        subCinstructions.add("loans");
        subCinstructions.add("20190109");
        subCinstructions.add("clear");
        //当日借次月结清
        cinstructions.add(subCinstructions = new LinkedList<>());
        subCinstructions.add("loans");
        subCinstructions.add("20190209");
        subCinstructions.add("clear");
        //接了之后不管他，让他自己还
        cinstructions.add(subCinstructions = new LinkedList<>());
        subCinstructions.add("loans");
        //第一期多次提前还款，第二期提前还款后结清
        cinstructions.add(subCinstructions = new LinkedList<>());
        subCinstructions.add("loans");
        subCinstructions.add("20190121");
        subCinstructions.add("prepay");
        subCinstructions.add("20190218");
        subCinstructions.add("prepay");
        subCinstructions.add("20190221");
        subCinstructions.add("prepay");
        subCinstructions.add("20190421");
        subCinstructions.add("clear");
        for(int coun = 0;coun < 4;coun++){
            //宽限期内和宽限期外都共用这套逻辑
            String loanType = coun < 2 ? "loans" : "loansg";
            cinstructions.add(subCinstructions = new LinkedList<>());
            subCinstructions.add(loanType);
            subCinstructions.add("freeze");
            //逾期到宽限期内偿还欠款
            subCinstructions.add("20190324");
            subCinstructions.add("repayOverdue");
            //单数不管他，让他自然还清，双数加入一些特殊交易
            if(coun % 2 == 0){
                subCinstructions.add("prepay");
                subCinstructions.add("20190327");
                subCinstructions.add("clear");
            }

            cinstructions.add(subCinstructions = new LinkedList<>());
            subCinstructions.add(loanType);
            subCinstructions.add("freeze");
            //逾期到宽限期外偿还欠款
            subCinstructions.add("20190421");
            subCinstructions.add("repayOverdue");
            //单数不管他，让他自然还清，双数加入一些特殊交易
            if(coun % 2 == 0){
                subCinstructions.add("prepay");
                subCinstructions.add("20190424");
                subCinstructions.add("clear");
            }

            cinstructions.add(subCinstructions = new LinkedList<>());
            subCinstructions.add(loanType);
            subCinstructions.add("freeze");
            //逾期两期，然后还清欠款（这时候实际上不用考虑什么宽限期了，肯定过了，但是为了代码逻辑写起来简单，这里还是加上了宽限期）
            subCinstructions.add("20190624");
            subCinstructions.add("repayOverdue");
            //单数不管他，让他自然还清，双数加入一些特殊交易
            if(coun % 2 == 0){
                subCinstructions.add("prepay");
                subCinstructions.add("20190627");
                subCinstructions.add("clear");
            }

            if(coun % 2 == 0){
                //一直逾期，到到期后才还上
                cinstructions.add(subCinstructions = new LinkedList<>());
                subCinstructions.add(loanType);
                subCinstructions.add("freeze");
                subCinstructions.add("20191021");
                subCinstructions.add("repayOverdue");
            }
        }
        
        //利随本清
        //当日借本月结清
        cinstructions.add(subCinstructions = new LinkedList<>());
        subCinstructions.add("loano");
        subCinstructions.add("20190109");
        subCinstructions.add("clear");
        //当日借次月结清
        cinstructions.add(subCinstructions = new LinkedList<>());
        subCinstructions.add("loano");
        subCinstructions.add("20190209");
        subCinstructions.add("clear");
        //接了之后不管他，让他自己还
        cinstructions.add(subCinstructions = new LinkedList<>());
        subCinstructions.add("loano");
        //宽限期内还清
        cinstructions.add(subCinstructions = new LinkedList<>());
        subCinstructions.add("loano");
        subCinstructions.add("freeze");
        subCinstructions.add("20191012");
        subCinstructions.add("repayOverdue");
        //宽限期外还清
        cinstructions.add(subCinstructions = new LinkedList<>());
        subCinstructions.add("loano");
        subCinstructions.add("freeze");
        subCinstructions.add("20191101");
        subCinstructions.add("repayOverdue");

        return cinstructions;
    }
}
