package sid.crawler.crawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sid.utils.CommonFileUtils;
import sid.utils.CommonStringUtils;
import sid.utils.communication.network.http.HttpWorker;
import sid.utils.communication.network.http.Request;
import sid.utils.communication.network.http.Response;
import sid.utils.db.DBUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class NETangCrawler extends CrawlerBase {
    private HttpWorker httpWorker = getHttpWorker("CustomWorker");

    Request request;

    int totalDownloadCount = 0;

    int pageIndex = 1;

    int titleIndex = 1;

    int imageIndex = 1;

    String titleName = null;

    int startPageNumber = 1;

    int pageCount = 10;

    long imageSizeLimit = 5 * 1024;//过滤5K以下的图片

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

    @Override
    public void start(Map<String, Object> param) {
        File rootFolder = new File("E:\\Temp\\netang");
        //设置开始页码和总的爬取页数
        startPageNumber = 1;
        pageCount = 10;
        try{
            String tsdt = CommonStringUtils.toString(param.get("tsdt"));
            DBUtils.executeBySqlTemplate("delete from video_list where tsdt = ?",new ArrayList<Object>(){{
                add(tsdt);
            }},true);
            if(rootFolder.exists()){
                CommonFileUtils.forceDelete(rootFolder);
            }
            titleCrawler(param);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void titleCrawler(Map<String, Object> param) throws Exception{
        request = new Request();
        request.setTimeoutSeconds(5);
        request.setRequestMethod(Request.RequestMethod.GET);
//        request.setHeader("Host","bi-si7.xyz");
//        request.setHeader("Referer","http://bi-si777.xyz/forum.php");
//        request.setHeader("Origin","http://bi-si777.xyz");
//        request.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
//        request.setHeader("cache-control","no-cache");
        fetchTitleList(param);
        System.out.println("我们一共下载了"+totalDownloadCount+"张图");
    }

    private void fetchTitleList(Map<String, Object> param) throws Exception{
        for(int index = startPageNumber;index < startPageNumber + pageCount;index++){
            pageIndex = index;
            request.setRequestURL(getUrlOfPage(pageIndex));
            Response titleListPageResponse = fetchResponseSync(request);
            Document doc = Jsoup.parse(new String(titleListPageResponse.getContent()));
            Elements titleList = doc.select("h1");//他的帖子链接是在这个标签中的
            System.out.println("这是第"+pageIndex+"页，本页共"+titleList.size()+"张帖子");
            titleIndex = 0;
            for(Element one : titleList){
                titleIndex++;
                Element a = one.selectFirst("a");
                titleName = a.html();
                param.put("titleContentPageUrl",titleListPageResponse.getProtocol() + "://" + titleListPageResponse.getDomain() + "/" + a.attr("href"));
                fetchTitleContent(param);
                String url = param.get("titleContentPageUrl").toString();
                String link = param.get("titleContentLink").toString();
                String id = "" + pageIndex + titleIndex;
                String name = CommonFileUtils.toLegalFilename(titleName);
                String tsdt = param.get("tsdt").toString();
                DBUtils.executeBySqlTemplate("insert into video_list (ID,TSDT,NAME,URL,LINK) VALUES (?,?,?,?,?)",new ArrayList<Object>(){{
                    add(id);
                    add(tsdt);
                    add(name);
                    add(url);
                    add(link);
                }},true);
            }
        }
    }

    private void fetchTitleContent(Map<String, Object> param)throws Exception{
        request.setRequestURL(CommonStringUtils.toString(param.get("titleContentPageUrl")));
        Response tieziContentPageResponse = fetchResponseSync(request);
        System.out.println("这是第"+pageIndex+"页第"+titleIndex+"张帖子");
        Document doc = Jsoup.parse(new String(tieziContentPageResponse.getContent()));
        Elements message = doc.select(".message");
        param.put("titleContentLink",doc.selectFirst(".blockcode").selectFirst("li").html());
        Elements images = message.select("img");
        imageIndex = 0;
        for(Element img : images){
            imageIndex++;
            String fileAttr = img.attr("src");
            if(!fileAttr.startsWith("http")){
                fileAttr = tieziContentPageResponse.getProtocol() + "://" + tieziContentPageResponse.getDomain() + "/" + fileAttr;
            }
            request.setRequestURL(fileAttr);
            Response imageResponse = fetchResponseSync(request);
            if(imageResponse.getContent().length <= imageSizeLimit){
                System.out.println("这是第"+pageIndex+"页第"+titleIndex+"张帖子的第"+imageIndex+"张图，这张图没办法下载，大小不足" + imageSizeLimit +
                        ";url=" + request.getRequestURL());
                continue;
            }
            File imageFile = new File("E:\\Temp\\netang\\image\\id=" + pageIndex + titleIndex + "(" + CommonFileUtils.toLegalFilename(titleName) + ")" + imageIndex + "." + CommonFileUtils.getExtensions(fileAttr));
            CommonFileUtils.writeByteArrayToFile(imageFile,imageResponse.getContent(),false);
            System.out.println("这是第"+pageIndex+"页第"+titleIndex+"张帖子的第"+imageIndex+"张图");
            totalDownloadCount++;
        }
    }

    private static String getUrlOfPage(int pageIndex){
        return String.format("https://www.dftretre324.xyz/forum-38-%s.html",pageIndex);//欧美
//        return String.format("https://www.dftretre324.xyz/forum.php?mod=forumdisplay&fid=2&page=%s",pageIndex);//国产
//        return String.format("https://www.98tang.com/forum-2-%s.html",pageIndex);
    }
}
