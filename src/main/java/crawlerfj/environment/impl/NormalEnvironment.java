package crawlerfj.environment.impl;

import crawlerfj.environment.IEnvironmentCore;
import toolroom.httputil.HttpUtils;
import toolroom.httputil.Request;
import toolroom.httputil.Response;

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
