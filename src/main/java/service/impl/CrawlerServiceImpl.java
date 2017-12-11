package service.impl;


import dao.CrawlerDao;
import org.springframework.beans.factory.annotation.Autowired;
import service.CrawlerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class CrawlerServiceImpl implements CrawlerService {
    @Autowired
    CrawlerDao crawlerDao;

    public String GetViewStr() {
        return crawlerDao.GetViewStr();
    }

    public String doCrawler(String path) throws IOException {
        URL url = new URL(path);
        URLConnection con = url.openConnection();
        InputStreamReader streamReader = new InputStreamReader(con.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        return null;
    }
}
