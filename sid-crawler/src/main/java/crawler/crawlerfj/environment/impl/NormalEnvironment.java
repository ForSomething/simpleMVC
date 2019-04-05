package crawler.crawlerfj.environment.impl;

import crawler.crawlerfj.environment.IEnvironmentCore;
import utils.httputil.HttpUtils;
import utils.httputil.Request;
import utils.httputil.Response;

public class NormalEnvironment implements IEnvironmentCore {
    @Override
    public Response request(Request request) throws Exception {
        Response response = null;
        switch (request.getRequestMethod()){
            case GET: response = HttpUtils.doGet(request);break;
            case POST: response = HttpUtils.doPost(request);break;
        }
        return response;
    }

    @Override
    public Response executeScript(String script) {
        return null;
    }

    @Override
    public void free() {

    }
}
