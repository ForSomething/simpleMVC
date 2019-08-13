package crawler.crawlerfj.environment.impl;

import crawler.crawlerfj.environment.IEnvironmentCore;
import utils.communication.network.http.Request;
import utils.communication.network.http.Response;

public class InternalBrowserEnvironment implements IEnvironmentCore {
    @Override
    public Response request(Request request) throws Exception {
        return null;
    }

    @Override
    public Response executeScript(String script) throws Exception {
        return null;
    }

    @Override
    public void free() {

    }
}
