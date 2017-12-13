package service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface CrawlerService {
    String GetViewStr();

    String doCrawler(String path) throws IOException;

    String getSearchListInfo(String searchPath) throws IOException;

    String getItemInfo(List<String> itemPathList) throws IOException;
}
