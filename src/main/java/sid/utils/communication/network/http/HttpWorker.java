package sid.utils.communication.network.http;

import java.io.IOException;

public interface HttpWorker {
    void request(Request r) throws IOException;

    Response getResponse();

    Request getRequest();

    void executeScript(String script);

    void close();
}
