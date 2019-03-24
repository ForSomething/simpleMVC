package crawlerfj.environment;

import crawlerfj.EnvironmentType;
import crawlerfj.environment.impl.BrowserEnvironment;
import crawlerfj.environment.impl.NormalEnvironment;
import toolroom.httputil.Request;
import toolroom.httputil.Response;

import java.util.Map;

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
