package common.eventhandlerinterface;

@FunctionalInterface
public interface BaseEventHandler {
    void Excute(Object parameter) throws Exception;
}
