package crawler.crawlers;

import utils.FileUtils;
import utils.JsonUtils;
import utils.communication.network.http.CustomWorker;
import utils.communication.network.http.Request;
import utils.communication.network.http.Response;

import java.util.Map;

public class JuejinCrawler extends CrawlerBase {
    public static void start() throws Exception{
//        Request request = new Request();
//        Response response;
//        request.setRequestURL("https://juejin.im/auth/type/phoneNumber");
//        request.setRequestMethod(Request.RequestMethod.POST);
//        request.setHeader("Content-Type",Request.ContentType.APPLICATION_JSON.toString());
//        request.setParam("phoneNumber","15605188902");
//        request.setParam("password","jjmm4284670");
//        response = CustomWorker.doRequest(request);
//        FileUtils.write(response.getContent(),"E:\\work\\zhixiaoyinhang\\文档\\人行\\result.txt",false);
//        Map<String,Object> map = JsonUtils.parseJsonString2Map(new String(page.getResponse().getContent(),"utf-8"));
//        String token = map.get("token").toString();
//        String clientId = String.valueOf(((Number)map.get("clientId")).longValue());
//        String userId = map.get("userId").toString();
//        page.getRequest().clearHeader();
//        page.getRequest().clearParam();
//        page.getRequest().setRequestMethod(Request.RequestMethod.GET);
//        page.getRequest().setParam("token",token);
//        page.getRequest().setParam("clientId",clientId);
//        page.getRequest().setParam("userId",userId);
//        page.getRequest().setParam("src","web");
//        page.getRequest().setParam("page","0");
//        page.getRequest().setRequestURL("https://collection-set-ms.juejin.im/v1/getUserCollectionSet/1");
//        page.fetchResponseSync();
//        FileUtils.write(page.getResponse().getContent(),"E:\\work\\zhixiaoyinhang\\文档\\人行\\result.txt",true);
    }

    @Override
    void start(Map<String, Object> param) {

    }
}
