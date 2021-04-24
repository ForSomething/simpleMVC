package sid.crawler.crawlers;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sid.utils.CommonFileUtils;
import sid.utils.CommonStringUtils;
import sid.utils.communication.network.http.HttpWorker;
import sid.utils.communication.network.http.Request;
import sid.utils.communication.network.http.Response;

import java.io.File;
import java.util.Map;

public class BisiCrawler extends CrawlerBase {
    private HttpWorker httpWorker = getHttpWorker("CustomWorker");

    Request request;

    int totalDownloadCount = 0;

    int pageIndex = 1;

    int titleIndex = 1;

    int imageIndex = 1;

    String titleName = null;

    int startPageNumber = 1;

    int pageCount = 10;

    String pageUrlLogFilePath = "E:\\Temp\\bisi\\url.txt";
    String torrentLinkLogFilePath = "E:\\Temp\\bisi\\link.txt";

    private Response fetchResponseSync(Request request) throws Exception{
        int retryTimes = 1;
        while (true){
            try{
                httpWorker.request(request);
                return httpWorker.getResponse();
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

    private boolean deleteFile(File file){
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
        }
        return file.delete();
    }

    @Override
    public void start(Map<String, Object> param) {
        File rootFolder = new File("E:\\Temp\\bisi");
        if(rootFolder.exists()){
            deleteFile(rootFolder);
        }
        //设置开始页码和总的爬取页数
        startPageNumber = 1;
        pageCount = 10;
        try{
//            if(true){
//                //todo 这里面的是测试的
//                cTest();
//                return;
//            }
            //下面这个链接是下载图片的
//            param.put("titleListPageUrl","http://bi-si888.xyz/forum.php?mod=forumdisplay&fid=18&forumdefstyle=yes&page=");
            //下面这个链接是下载亚洲片的
//            param.put("titleListPageUrl","http://bi-si888.xyz/forum.php?mod=forumdisplay&fid=2&orderby=dateline&filter=author&orderby=dateline&page=");
            //下面这个是亚洲片中的国产区
            param.put("titleListPageUrl","http://bccard.xyz/forum.php?mod=forumdisplay&fid=2&orderby=dateline&typeid=2293&filter=author&orderby=dateline&typeid=2293&page=");
            //下面这个链接是下载欧美片的
//            param.put("titleListPageUrl","http://bi-si888.xyz/forum.php?mod=forumdisplay&fid=10&filter=author&orderby=dateline&page=");
            titleCrawler(param);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void titleCrawler(Map<String, Object> param) throws Exception{
        login(param);
        fetchTitleList(param);
        System.out.println("我们一共下载了"+totalDownloadCount+"张图");
        System.out.println(DateFormatUtils.format(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
    }

    private void login(Map<String, Object> param)throws Exception{
        request = new Request();
        request.setTimeoutSeconds(4);
        request.setRequestURL("http://bccard.xyz/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1");
        request.setRequestMethod(Request.RequestMethod.POST);
        request.setHeader("Content-Type",Request.ContentType.MULTIPART_FORM_DATA.toString());
//        request.setHeader("Host","bi-si7.xyz");
//        request.setHeader("Referer","http://bi-si888.xyz/forum.php");
//        request.setHeader("Origin","http://bi-si888.xyz");
//        request.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
//        request.setHeader("cache-control","no-cache");
        request.setParam("fastloginfield","username");
        request.setParam("username","zclebszh");
        request.setParam("password","!234QwerAsdf");
        request.setParam("quickforward","yes");
        request.setParam("handlekey","ls");
        Response response = fetchResponseSync(request);
        request.setCookieMap(response.getCookieMap());
        request.removeHeader("Content-Type");
        request.clearParam();
        request.setRequestMethod(Request.RequestMethod.GET);
    }

    private void fetchTitleList(Map<String, Object> param) throws Exception{
        for(int index = startPageNumber;index < startPageNumber + pageCount;index++){
            pageIndex = index;
            request.setRequestURL(CommonStringUtils.toString(param.get("titleListPageUrl"))+pageIndex);
            Response titleListPageResponse = fetchResponseSync(request);
            Document doc = Jsoup.parse(new String(titleListPageResponse.getContent()));
            Elements titleList = doc.select(".xst");
            System.out.println("这是第"+pageIndex+"页，本页共"+titleList.size()+"张帖子");
            titleIndex = 0;
            for(Element one : titleList){
                titleIndex++;
                titleName = one.html();
                param.put("titleContentPageUrl",titleListPageResponse.getProtocol() + "://" + titleListPageResponse.getDomain() + "/" + one.attr("href"));
                fetchTitleContent(param);
                if(imageIndex != 0){
                    //说明这张帖子下载到了图片
                    CommonFileUtils.writeStringToFileAsOneLine(new File(pageUrlLogFilePath),"number:" + pageIndex + titleIndex + ":"+param.get("titleContentPageUrl").toString(),true);
                }
            }
        }
    }

    private void fetchTitleContent(Map<String, Object> param)throws Exception{
        request.setRequestURL(CommonStringUtils.toString(param.get("titleContentPageUrl")));
        Response tieziContentPageResponse = fetchResponseSync(request);
        System.out.println("这是第"+pageIndex+"页第"+titleIndex+"张帖子");
        Document doc = Jsoup.parse(new String(tieziContentPageResponse.getContent()));
        Elements ignore_js_op = doc.select("ignore_js_op");
        Elements images = ignore_js_op.select("img");
        imageIndex = 0;
        for(Element img : images){
            imageIndex++;
            String fileAttr;
            if(!CommonStringUtils.isEmptyOrWihtespace((fileAttr = img.attr("file")))){
                if(!fileAttr.startsWith("http")){
                    fileAttr = tieziContentPageResponse.getProtocol() + "://" + tieziContentPageResponse.getDomain() + "/" + fileAttr;
                }
                request.setRequestURL(fileAttr);
                Response imageResponse = fetchResponseSync(request);
                if(imageResponse.getContent().length == 0){
                    System.out.println("这是第"+pageIndex+"页第"+titleIndex+"张帖子的第"+imageIndex+"张图，这张图没办法下载，大小是0");
                    continue;
                }
                File imageFile = new File("E:\\Temp\\netang\\image\\" + pageIndex + titleIndex + "(" + CommonFileUtils.toLegalFilename(titleName) + ")" + imageIndex + "." + CommonFileUtils.getExtensions(fileAttr));
                CommonFileUtils.writeByteArrayToFile(imageFile,imageResponse.getContent(),false);
                System.out.println("这是第"+pageIndex+"页第"+titleIndex+"张帖子的第"+imageIndex+"张图");
                totalDownloadCount++;
            }else{
                String srcStr = img.attr("src");
                if(!srcStr.startsWith("http")){
                    srcStr = tieziContentPageResponse.getProtocol() + "://" + tieziContentPageResponse.getDomain() + "/" + srcStr;
                }
                System.out.println("这是第"+pageIndex+"页第"+titleIndex+"张帖子的第"+imageIndex+"张图，但是这张图无法下载，src="+srcStr);
            }
        }
    }
}
