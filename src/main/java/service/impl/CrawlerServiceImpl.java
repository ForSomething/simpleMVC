package service.impl;


import Util.Const;
import Util.StringUtil;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import dao.CrawlerDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import service.CrawlerService;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

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
}
