package service;

import java.io.IOException;
import java.net.MalformedURLException;

public interface CrawlerService {
    String GetViewStr();

    String doCrawler(String path) throws IOException;
}
