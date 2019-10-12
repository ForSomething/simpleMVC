package crawler.crawlers;

import crawler.crawlerfj.EnvironmentType;
import crawler.crawlerfj.Page;
import utils.communication.network.http.Request;

public class FormTest {
    public static void start()throws Exception{
        Page page = new Page("http://localhost:2333/crawler/getUserCredit",EnvironmentType.NORMAL);
//        page.getRequest().setHeader("Content-Type",Request.ContentType.MULTIPART_FORM_DATA.toString());
        page.getRequest().setCharSet("utf-8");
//        page.getRequest().setHeader("Content-Type","");
        page.getRequest().setRequestMethod(Request.RequestMethod.GET);
        page.getRequest().setParam("name","就是你们");
//        page.getRequest().setParam("password","齐白石");
        page.getRequest().setParam("code","ri_0,1,2,0");
        page.getRequest().setParam("remember","1");
        page.fetchResponseSync();
//        FileUtils.write(page.getResponse().getContent(),"E:\\work\\zhixiaoyinhang\\文档\\人行\\formTest.txt",false);
    }
}
