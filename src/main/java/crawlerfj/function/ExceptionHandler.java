package crawlerfj.function;

import crawlerfj.Page;

@FunctionalInterface
public interface ExceptionHandler {
    void execute(Exception exception, Page page, Callback callback);
}
