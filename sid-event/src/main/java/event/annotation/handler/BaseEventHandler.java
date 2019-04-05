package event.annotation.handler;

@FunctionalInterface
public interface BaseEventHandler {
    void Excute(Object parameter) throws Exception;
}
