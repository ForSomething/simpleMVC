package crawler.crawlerfj.environment.impl;

import crawler.crawlerfj.environment.IEnvironmentCore;
import utils.communication.network.http.CustomWorker;
import utils.communication.network.http.Request;
import utils.communication.network.http.Response;

public class NormalEnvironment implements IEnvironmentCore {
    @Override
    public Response request(Request request) throws Exception {
        return CustomWorker.doRequest(request);
    }

    @Override
    public Response executeScript(String script) {
        return null;
    }

    @Override
    public void free() {

    }
}
