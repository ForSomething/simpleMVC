package service.impl;


import Util.Const;
import Util.HttpHelper;
import Util.StringUtil;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import dao.CrawlerDao;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import service.CrawlerService;
import sun.net.www.http.HttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CrawlerServiceImpl implements CrawlerService {
    @Autowired
    CrawlerDao crawlerDao;

    public String GetViewStr() {
        return crawlerDao.GetViewStr();
    }

    public String doCrawler(String path) throws IOException {
        String resultStr = "ok";
        URL url = new URL(path);
        File file = new File("C:\\Users\\Administrator\\Desktop\\scrip.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file,true);
        Document doc = Jsoup.parse(url, Const.HttpConnectTimeOut);
        doc.outerHtml();
        Elements elements = doc.getElementsByClass("sku-name");
        String outterHTML;
        for(Element one : elements){
            outterHTML = one.html();
            fileOutputStream.write(StringUtil.GetByteArray(outterHTML));
        }
        fileOutputStream.close();
        return resultStr;
    }

    public String getSearchListInfo(String searchPath) throws IOException {
        HttpResponse response = HttpHelper.doGet(searchPath);
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            HttpEntity entity = response.getEntity();
            if(entity != null){
                List<String> hrefList = new LinkedList<String>();
                Document doc = Jsoup.parse(EntityUtils.toString(entity));
                Elements divElements = doc.getElementsByClass("p-img");
                for(Element one : divElements){
                    Elements as = one.getElementsByTag("a");
                    if(as != null && as.size() > 0){
                        String attrStr = as.first().attr("href");
                        String ustr = attrStr.startsWith("http") ? attrStr : "https:" + attrStr;
                        hrefList.add(ustr);
                    }
                }
                getItemInfo(hrefList);
            }
        }
        return "ok";
    }

    public String getItemInfo(List<String> itemPathList) throws IOException {
        File file = new File("C:\\Users\\Administrator\\Desktop\\scrip.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file,false);
        for(String itemPath : itemPathList){
            String itemID = itemPath.replace("https://item.jd.com/","").replace(".html","");
            HttpResponse response = HttpHelper.doGet(itemPath);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                if(entity != null){

                    Document doc = Jsoup.parse(EntityUtils.toString(entity));
                    Elements elements = doc.getElementsByClass("sku-name");
                    String outterHTML = "";
                    for(Element one : elements){
                        Elements imgs = one.getElementsByTag("img");
                        for(Element img : imgs){
                            img.remove();
                        }
                        outterHTML += one.html() + "    (" + itemID + ")    ";
                        outterHTML += "(价格：" + getPrice(itemID) + ")\r\n";
                    }
                    fileOutputStream.write(StringUtil.GetByteArray(outterHTML));
                }
            }
        }
        fileOutputStream.close();

        return "ok";
    }

    private String getPrice(String itemID) throws IOException {
        String urlStr = Const.jdPriceURLStr.replace("%%ii%%",itemID);
        HttpResponse response = HttpHelper.doGet(urlStr);
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            HttpEntity entity = response.getEntity();
            if(entity != null){
                return EntityUtils.toString(entity);
            }
        }
        return "";
    }
}
