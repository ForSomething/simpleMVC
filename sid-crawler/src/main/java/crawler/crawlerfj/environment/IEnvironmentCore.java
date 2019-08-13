package crawler.crawlerfj.environment;

import crawler.crawlerfj.EnvironmentType;
import crawler.crawlerfj.environment.impl.BrowserEnvironment;
import crawler.crawlerfj.environment.impl.NormalEnvironment;
import utils.communication.network.http.Request;
import utils.communication.network.http.Response;

public interface IEnvironmentCore {
    static IEnvironmentCore getInstance(EnvironmentType environmentType){
        switch (environmentType){
            case NORMAL:return new NormalEnvironment();
            default: return BrowserEnvironment.getInstance();
        }
    }

    Response request(Request request) throws Exception;
    Response executeScript(String script) throws Exception;
    void free();
}
