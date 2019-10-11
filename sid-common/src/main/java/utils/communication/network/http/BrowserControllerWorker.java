package utils.communication.network.http;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pool.ObjectPool;

public class BrowserControllerWorker extends BaseHttpWorker {
    private static ObjectPool<WebDriver> driverPool;

    private WebDriver webDriver;

    static {
        driverPool = new ObjectPool<WebDriver>(8){

            @Override
            protected WebDriver newInstance() {
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless");//启动无头模式--无UI模式
                return new ChromeDriver(chromeOptions);
            }
        };
    }

    @Override
    public void request(Request r) {

    }

    @Override
    public Response getResponse() {
        return null;
    }

    @Override
    public void executeScript(String script) {
        super.executeScript(script);
    }


}
