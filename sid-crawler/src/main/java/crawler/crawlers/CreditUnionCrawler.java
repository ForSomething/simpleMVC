package crawler.crawlers;

import crawler.crawlerfj.EnvironmentType;
import crawler.crawlerfj.Page;
import utils.communication.network.http.Request;

public class CreditUnionCrawler {
    public static void start(){
        try{
            //先看看登录会不会给jsessionid
            Page page = new Page("http://9.132.47.2/shcreditunion/logon.do?isDissentLogon=null",EnvironmentType.NORMAL); // todo 这个地址好像不对
            page.fetchResponseSync();
            //然后进行登录，并记录登陆后的返回的内容
            page.getRequest().setRequestMethod(Request.RequestMethod.POST);
            page.getRequest().setRequestURL("http://9.132.47.2/shcreditunion/logon.do?isDissentLogin=null");
            page.getRequest().setParam("userid","NCBK8200317");
            page.getRequest().setParam("password","shangke712");
            page.fetchResponseSync();
        }catch (Exception e){
            //todo 这里要写日志
        }
    }
}
