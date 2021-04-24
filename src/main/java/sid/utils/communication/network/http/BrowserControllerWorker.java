package sid.utils.communication.network.http;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import sid.utils.CommonUrlUtils;

import java.util.HashMap;

public class BrowserControllerWorker extends BaseHttpWorker {
    private WebDriver webDriver;

    private Request request = null;

    BrowserControllerWorker(){
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");//启动无头模式--无UI模式
        webDriver = new ChromeDriver(chromeOptions);
    }

    @Override
    public void request(Request r) {
        request = r;
        webDriver.get(r.getRequestURL());
    }

    @Override
    public Response getResponse() {
        try {
            Response response = new Response();
            response.setStateCode(200);
            response.setProtocol(CommonUrlUtils.getProtocol(webDriver.getCurrentUrl()));
            response.setDomain(CommonUrlUtils.getDomain(webDriver.getCurrentUrl()));
            response.setBaseUrl(webDriver.getCurrentUrl());
            response.setHeaderMap(new HashMap<>());
            response.setContent(webDriver.getPageSource().getBytes("UTF-8"));
            return response;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public void executeScript(String script) {
        ((ChromeDriver)webDriver).executeScript(script);
    }

    @Override
    public void close() {
        webDriver.close();
    }
}
