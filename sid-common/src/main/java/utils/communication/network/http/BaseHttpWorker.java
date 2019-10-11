package utils.communication.network.http;

public abstract class BaseHttpWorker implements HttpWorker {
    @Override
    public void executeScript(String script) {
        throw new RuntimeException("不支持此功能");
    }
}
