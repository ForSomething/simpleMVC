package crawler.crawlerfj.function;

import crawler.crawlerfj.Page;

@FunctionalInterface
public interface ExceptionHandler {
    void execute(Exception exception, Page page, Callback callback);
}
