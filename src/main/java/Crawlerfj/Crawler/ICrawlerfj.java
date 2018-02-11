package Crawlerfj.Crawler;

public interface ICrawlerfj {
    boolean CanHandle(Object configEntity);

    void Crawling(Object configEntity);
}
