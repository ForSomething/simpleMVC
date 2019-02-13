package crawlerfj.crawler.Impl.browsercrawler;

public class BrowserCrawlerConfig {
    private BrowserTask browserTask;

    public BrowserTask getBrowserTask() {
        return browserTask;
    }

    public void setBrowserTask(BrowserTask browserTask) {
        this.browserTask = browserTask;
    }

    @FunctionalInterface
    public interface BrowserTask{
        void execute(Browser browser);
    }

    public interface Browser{
        void toPage(String url);

        String getPageContent();

        void executeScript(String script);
    }
}
