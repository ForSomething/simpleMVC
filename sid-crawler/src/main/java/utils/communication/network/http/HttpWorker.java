package utils.communication.network.http;

import java.io.IOException;

public interface HttpWorker {
    void request(Request r) throws IOException;

    Response getResponse();

    void executeScript(String script);

    void close();
}
