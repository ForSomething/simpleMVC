package crawler.crawlerfj.environment.impl;

import crawler.crawlerfj.environment.IEnvironmentCore;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pool.ObjectPool;
import utils.RegexUtils;
import utils.httputil.Request;
import utils.httputil.Response;

import java.util.HashMap;

public class BrowserEnvironment implements IEnvironmentCore {
    private static ObjectPool<WebDriver> driverPool;

    private WebDriver webDriver;

    static {
        driverPool = new ObjectPool(8){

            @Override
            protected Object newInstance() {
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless");//启动无头模式--无UI模式
                return new ChromeDriver(chromeOptions);
            }
        };
    }

    public static BrowserEnvironment getInstance() {
        BrowserEnvironment instance = new BrowserEnvironment();
        instance.webDriver = driverPool.blockingGet();
        return instance;
    }

    @Override
    public Response request(Request request) throws Exception {
        webDriver.get(request.getRequestURL());
        return buildResponse();
    }

    @Override
    public Response executeScript(String script) throws Exception  {
        ((ChromeDriver)webDriver).executeScript(script);
        return buildResponse();
    }

    private Response buildResponse() throws Exception {
        Response response = new Response();
        response.setStateCode(200);
        response.setProtocol(RegexUtils.getProtocol(webDriver.getCurrentUrl()));
        response.setDomain(RegexUtils.getDomain(webDriver.getCurrentUrl()));
        response.setBaseUrl(webDriver.getCurrentUrl());
        response.setResponseHeaderMap(new HashMap<>());
        response.setContent(webDriver.getPageSource().getBytes("UTF-8"));
        return response;
    }

    @Override
    public void free() {
        driverPool.returnInstance(webDriver);
        webDriver = null;
    }
}
