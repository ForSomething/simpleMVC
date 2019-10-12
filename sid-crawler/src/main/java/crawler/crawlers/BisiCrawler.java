package crawler.crawlers;

import crawler.crawlerfj.EnvironmentType;
import crawler.crawlerfj.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.FileUtils;
import utils.RegexUtils;
import utils.StringUtils;
import utils.communication.network.http.Request;

public class BisiCrawler {
    public static void start() throws Exception{
        //登录
        Page page = new Page("http://bi-si10.xyz/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1",EnvironmentType.NORMAL);
        page.getRequest().setTimeoutSeconds(2);
        page.getRequest().setRequestMethod(Request.RequestMethod.POST);
        page.getRequest().setHeader("Content-Type",Request.ContentType.MULTIPART_FORM_DATA.toString());
        page.getRequest().setHeader("Host","bi-si7.xyz");
        page.getRequest().setHeader("Referer","http://bi-si10.xyz/forum.php");
        page.getRequest().setHeader("Origin","http://bi-si10.xyz");
        page.getRequest().setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        page.getRequest().setHeader("cache-control","no-cache");
        page.getRequest().setParam("fastloginfield","username");
        page.getRequest().setParam("username","zclebszh");
        page.getRequest().setParam("password","Qwer1234");
        page.getRequest().setParam("quickforward","yes");
        page.getRequest().setParam("handlekey","ls");
        fetchResponseSync(page);
//        FileUtils.write(page.getResponse().getContent(),"E:\\work\\zhixiaoyinhang\\文档\\人行\\bisi.txt",false);
        //逐页爬取
        int totalDownloadCount = 0;
        for(int pageIndex = 1;pageIndex < 6;pageIndex++){
            page.getRequest().setRequestMethod(Request.RequestMethod.GET);
            page.getRequest().setCookieMap(page.getResponse().getCookieMap());
            page.getRequest().setRequestURL("http://bi-si10.xyz/forum.php?mod=forumdisplay&fid=2&filter=author&orderby=dateline&page="+pageIndex);
            page.getRequest().clearParam();
            page.getRequest().removeHeader("Content-Type");
            fetchResponseSync(page);
            Document doc = Jsoup.parse(new String(page.getResponse().getContent()));
            Elements tieziList = doc.select(".xst");
            System.out.println("这是第"+pageIndex+"页，本页共"+tieziList.size()+"张帖子");
            int noImageCount = 0;
            for(Element one : tieziList){
                noImageCount++;
                String tieziName = one.html();
                page.getRequest().setRequestURL(page.getResponse().getProtocol() + "://" + page.getResponse().getDomain() + "/" + one.attr("href"));
                fetchResponseSync(page);
                System.out.println("这是第"+pageIndex+"页第"+noImageCount+"张帖子");
                doc = Jsoup.parse(new String(page.getResponse().getContent()));
                Elements ignore_js_op = doc.select("ignore_js_op");
                Elements images = ignore_js_op.select("img");
                int index = 0;
                for(Element img : images){
                    index++;
                    String fileAttr;
                    if(!StringUtils.isNullOrWihtespace((fileAttr = img.attr("file")))){
                        if(!fileAttr.startsWith("http")){
                            fileAttr = page.getResponse().getProtocol() + "://" + page.getResponse().getDomain() + "/" + fileAttr;
                        }
                        Page tempPage = new Page(fileAttr,EnvironmentType.NORMAL);
                        tempPage.getRequest().setTimeoutSeconds(2);
                        fetchResponseSync(tempPage);
                        if(tempPage.getResponse().getContent().length == 0){
                            System.out.println("这是第"+pageIndex+"页第"+noImageCount+"张帖子的第"+index+"张图，这张图没办法下载，大小是0");
                            continue;
                        }
                        String fileFiltedName = RegexUtils.fileNameFilter(tieziName);
                        FileUtils.writeLine(fileFiltedName,"E:\\work\\zhixiaoyinhang\\文档\\人行\\bisi.txt",true);
                        FileUtils.write(tempPage.getResponse().getContent(),"E:\\work\\zhixiaoyinhang\\文档\\人行\\bisiImage2\\" +
                                "(" + RegexUtils.fileNameFilter(tieziName) + ")" + index + "." + RegexUtils.getExtensions(fileAttr),false);
//                        FileUtils.write(tempPage.getResponse().getContent(),"E:\\work\\zhixiaoyinhang\\文档\\人行\\bisiImage2\\" +
//                                "这是第"+pageIndex+"页第"+noImageCount+"张帖子的第"+index+"张图" + "." + RegexUtils.getExtensions(fileAttr),false);
                        System.out.println("这是第"+pageIndex+"页第"+noImageCount+"张帖子的第"+index+"张图");
                        totalDownloadCount++;
                    }else{
                        String srcStr = img.attr("src");
                        if(!srcStr.startsWith("http")){
                            srcStr = page.getResponse().getProtocol() + "://" + page.getResponse().getDomain() + "/" + srcStr;
                        }
                        System.out.println("这是第"+pageIndex+"页第"+noImageCount+"张帖子的第"+index+"张图，但是这张图无法下载，src="+srcStr);
                    }
                }
            }
        }
        System.out.println("我们一共下载了"+totalDownloadCount+"张图");
    }

    private static void fetchResponseSync(Page page) throws Exception{
        int retryTimes = 1;
        while (true){
            try{
                page.fetchResponseSync();
            }catch (Exception e){
                if(!e.toString().toLowerCase().contains("timeout")){
                    //如果不是超时异常，就跳出循环，否则疯狂重试
                    throw e;
                }
                //5s后重试
                Thread.sleep(5000);
                System.out.println("超时了，这是第"+retryTimes+"次重试");
                retryTimes++;
            }
        }
    }
}
