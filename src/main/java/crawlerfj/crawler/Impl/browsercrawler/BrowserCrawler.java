package crawlerfj.crawler.Impl.browsercrawler;

import common.pool.DispatchPool;
import crawlerfj.crawler.ICrawlerfj;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BrowserCrawler extends ICrawlerfj {
    private static DispatchPool<WebDriver> driverDispatchPool;

    static {
        String browserDriverPath = System.getProperty("webdriver.chrome.driver");
        if(browserDriverPath == null){
            // TODO 找个地方配置一下
            System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        }
        driverDispatchPool = new DispatchPool(10){

            @Override
            protected Object NewInstance() {
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless");//启动无头模式--无UI模式
                return new ChromeDriver(chromeOptions);
            }
        };
    }

    @Override
    public boolean CanHandle(Object _config) {
        return _config != null && BrowserCrawlerConfig.class.getCanonicalName().equals(_config.getClass().getCanonicalName());
    }

    @Override
    public void Crawling(Object _config) throws Exception {
        BrowserCrawlerConfig browserCrawlerConfig = (BrowserCrawlerConfig)_config;
        super.executeThread(()->{
            driverDispatchPool.ExecuteWithFreeInstance((freeItem) ->{
                browserCrawlerConfig.getBrowserTask().execute(new BrowserInstance(freeItem),browserCrawlerConfig);
                return null;
            });
            return null;
        });
    }

    private class BrowserInstance implements BrowserCrawlerConfig.Browser{
        private WebDriver driver;

        protected BrowserInstance(WebDriver driver){
            this.driver = driver;
        }

        @Override
        public void toPage(String url) {
            driver.get(url);
        }

        @Override
        public String getPageContent() {
            return driver.getPageSource();
        }

        @Override
        public void executeScript(String script) {
            ((ChromeDriver)driver).executeScript(script);
        }
    }
}
